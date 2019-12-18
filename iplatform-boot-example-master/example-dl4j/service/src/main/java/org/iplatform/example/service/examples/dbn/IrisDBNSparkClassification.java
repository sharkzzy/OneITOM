package org.iplatform.example.service.examples.dbn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.normalize.Normalize;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.AnalyzeSpark;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.datavec.spark.transform.misc.WritablesToStringFunction;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.spark.api.stats.SparkTrainingStats;
import org.deeplearning4j.spark.datavec.DataVecDataSetFunction;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.deeplearning4j.spark.stats.EventStats;
import org.deeplearning4j.spark.stats.StatsUtils;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * deeplearning4j IRIS example with dbn
 * https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-spark-examples/dl4j-spark/src/main/java/org/deeplearning4j/legacyExamples/TrainingStatsExample.java
 */
@Component
public class IrisDBNSparkClassification implements IExample {

    private static final Logger LOG = LoggerFactory.getLogger(IrisDBNSparkClassification.class);

    private boolean useSparkLocal = true;

    @Override
    public void run() {
        try {
            // 网络配置
            MultiLayerConfiguration config = getConfiguration();

            // 配置Spark

            SparkConf sparkConf = new SparkConf();
            if (useSparkLocal) {
                sparkConf.setMaster("local[*]");
                LOG.info("Using Spark Local");
            }
            sparkConf.setAppName("DL4J Spark Example");
            JavaSparkContext sc = new JavaSparkContext(sparkConf);

            // 获取训练数据
            List<JavaRDD<DataSet>> datasets = getTrainingData(sc);
            JavaRDD<DataSet> trainingData = datasets.get(0);
            JavaRDD<DataSet> testingData = datasets.get(1);

            // 配置TrainingMaster控制学习的参数
            int averagingFrequency = 3;     //Frequency with which parameters are averaged
            int examplesPerDataSetObject = 1;
            int batchSizePerWorker = 8; //minibatch size that each worker gets
            ParameterAveragingTrainingMaster tm = new ParameterAveragingTrainingMaster.Builder(examplesPerDataSetObject)
                    .workerPrefetchNumBatches(2)    //Asynchronously prefetch up to 2 batches
                    .averagingFrequency(averagingFrequency)
                    .batchSizePerWorker(batchSizePerWorker)
                    .build();

            // 创建Spark网络
            SparkDl4jMultiLayer sparkNetwork = new SparkDl4jMultiLayer(sc, config, tm);
            sparkNetwork.setCollectTrainingStats(true);
            sparkNetwork.setListeners(new ScoreIterationListener(1));

            // Fit epochs
            int epochs = 1000;
            for (int i = 0; i < epochs; i++) {
                MultiLayerNetwork net = sparkNetwork.fit(trainingData);

                // 评估
                Evaluation eval = sparkNetwork.evaluate(testingData);
                if (eval.f1() > 0.99) {
                    LOG.info("epcho " + i);
                    LOG.info(eval.stats());
                    net.save(new File("iris.model"),true);
                    break;
                }
            }

            // 删除训练时产生的临时文件
            tm.deleteTempFiles(sc);

            // 获得训练统计信息
            SparkTrainingStats stats = sparkNetwork.getSparkTrainingStats();
            Set<String> statsKeySet = stats.getKeySet();    //Keys for the types of statistics
            LOG.info("--- Collected Statistics ---");
            for (String s : statsKeySet) {
                LOG.info(s);
            }

            //Demo purposes: get one statistic and print it
            String first = statsKeySet.iterator().next();
            List<EventStats> firstStatEvents = stats.getValue(first);
            EventStats es = firstStatEvents.get(0);
            LOG.info("Training stats example:");
            LOG.info("Machine ID:     " + es.getMachineID());
            LOG.info("JVM ID:         " + es.getJvmID());
            LOG.info("Thread ID:      " + es.getThreadID());
            LOG.info("Start time ms:  " + es.getStartTime());
            LOG.info("Duration ms:    " + es.getDurationMs());

            StatsUtils.exportStatsAsHtml(stats, "SparkStats.html", sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MultiLayerConfiguration getConfiguration() {
        int numInputs = 4;
        int outputNum = 3;
        int seed = 6;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(3)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(3).nOut(3)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(3).nOut(outputNum).build())
                .build();

        return conf;
    }

    private List<JavaRDD<DataSet>> getTrainingData(JavaSparkContext sc) {
        // 构造数据Schema
        Schema inputDataSchema = new Schema.Builder()
                .addColumnDouble("sepal.length")
                .addColumnDouble("sepal.width")
                .addColumnDouble("petal.length")
                .addColumnDouble("petal.width")
                .addColumnCategorical("variety", Arrays.asList("Setosa", "Versicolor", "Virginica"))
                .build();

        // 构造数据转换，variety枚举转换为数据1，2，3
        TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
                .categoricalToInteger("variety")
                .build();
        Schema schema = tp.getInitialSchema();

        // 读取数据文件
        String file = null;
        try {
            file = new ClassPathResource("IrisData/iris.csv").getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JavaRDD<String> stringData = sc.textFile(file);
        RecordReader rr = new CSVRecordReader();
        JavaRDD<List<Writable>> parsedInputData = stringData.map(new StringToWritablesFunction(rr));
        JavaRDD<List<Writable>> processedData = SparkTransformExecutor.execute(parsedInputData, tp);
        printData(processedData, "---- Original Data ----");

        // 数据表转化
        DataAnalysis dataAnalysis = AnalyzeSpark.analyze(schema, processedData);
        TransformProcess tp2 = new TransformProcess.Builder(schema)
                .normalize("sepal.length", Normalize.Standardize, dataAnalysis)
                .normalize("sepal.width", Normalize.Standardize, dataAnalysis)
                .normalize("petal.length", Normalize.Standardize, dataAnalysis)
                .normalize("petal.width", Normalize.Standardize, dataAnalysis)
                .build();
        JavaRDD<List<Writable>> normalizerData = SparkTransformExecutor.execute(processedData, tp2);
        printData(normalizerData, "---- Normalizer Data ----");

        // 数据拆分成训练集和测试集
        JavaRDD<List<Writable>>[] rdds = processedData.randomSplit(new double[]{0.6, 0.4});
        JavaRDD<List<Writable>> trainData = rdds[0];
        JavaRDD<List<Writable>> testData = rdds[1];
        printData(trainData, "---- Train Data ----");
        printData(testData, "---- Test Data ----");

        // 训练集数据转换为DataSet
        List<JavaRDD<DataSet>> datasets = new ArrayList<>();
        JavaRDD<DataSet> train = trainData.map(new DataVecDataSetFunction(4, 3, false));
        JavaRDD<DataSet> test = testData.map(new DataVecDataSetFunction(4, 3, false));
        datasets.add(train);
        datasets.add(test);
        return datasets;
    }

    private void printData(JavaRDD<List<Writable>> rddData, String title) {
        JavaRDD<String> rdd = rddData.map(new WritablesToStringFunction(","));
        List<String> data = rdd.collect();
        LOG.info("\n" + title);
        for (String s : data) LOG.info(s);
    }
}
