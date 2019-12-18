package org.iplatform.example.service.examples.kpi;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.conf.updater.SgdSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.OptimizationResult;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataSource;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultReference;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultSaver;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxTimeCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.TerminationCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.impl.EvaluationScoreFunction;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
public class KPIHyperparameterOptimization implements IExample {
    @Override
    public void run() {
        try {
            int numInputs = 4;
            int outputNum = 3;

            //随机生成均匀的学习速率
            ParameterSpace<Double> learningRateHyperparam = new ContinuousParameterSpace(0.0001, 0.1);
            //随机生成均匀的神经元
            ParameterSpace<Integer> layerSizeHyperparam = new IntegerParameterSpace(16, 256);

            //配置超参数选择网络
            MultiLayerSpace hyperparameterSpace = new MultiLayerSpace.Builder()
                    .weightInit(WeightInit.XAVIER)
                    .l2(0.0001)
                    //学习速率使用随机超参数
                    .updater(new SgdSpace(learningRateHyperparam))
                    .addLayer(new DenseLayerSpace.Builder()
                            .nIn(numInputs)
                            .activation(Activation.LEAKYRELU)
                            .nOut(layerSizeHyperparam)
                            .build())
                    .addLayer(new OutputLayerSpace.Builder()
                            .nOut(outputNum)
                            .activation(Activation.SOFTMAX)
                            .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .build())
                    .numEpochs(1000)
                    .build();

            //使用随机选择超参
            CandidateGenerator candidateGenerator = new RandomSearchGenerator(hyperparameterSpace, null);

            //使用网格选择超参
            //CandidateGenerator candidateGenerator = new GridSearchCandidateGenerator<>(hyperparameterSpace, 5, GridSearchCandidateGenerator.Mode.RandomOrder);

            //提供用来训练的数据
            Class<? extends DataSource> dataSourceClass = IrisDataSource.class;
            Properties dataSourceProperties = new Properties();

            //定义模型存储目录
            String baseSaveDirectory = "arbiterKPI/";
            File f = new File(baseSaveDirectory);
            if (f.exists()) f.delete();
            f.mkdir();
            ResultSaver modelSaver = new FileModelSaver(baseSaveDirectory);

            //优化方向,这里我们希望优化f1
            ScoreFunction scoreFunction = new EvaluationScoreFunction(Evaluation.Metric.ACCURACY);

            //定义训练终止条件，这里定义训练15分钟或者有10个候选模型后终止
            TerminationCondition[] terminationConditions = {
                    new MaxTimeCondition(15, TimeUnit.MINUTES),
                    new MaxCandidatesCondition(50)};

            //Given these configuration options, let's put them all together:
            OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
                    .candidateGenerator(candidateGenerator)
                    .dataSource(dataSourceClass, dataSourceProperties)
                    .modelSaver(modelSaver)
                    .scoreFunction(scoreFunction)
                    .terminationConditions(terminationConditions)
                    .build();

            IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

            //启动监控器 http://localhost:9000/arbiter
            StatsStorage ss = new FileStatsStorage(new File("arbiterExampleUiStats.dl4j"));
            runner.addListeners(new ArbiterStatusListener(ss));
            UIServer.getInstance().attach(ss);

            //运行超参选择
            runner.execute();

            //打印统计信息和优化过程
            String s = "Best score: " + runner.bestScore() + "\n" +
                    "Index of model with best score: " + runner.bestScoreCandidateIndex() + "\n" +
                    "Number of configurations evaluated: " + runner.numCandidatesCompleted() + "\n";
            System.out.println(s);


            //得到所有结果，并打印最好结果
            int indexOfBestResult = runner.bestScoreCandidateIndex();
            List<ResultReference> allResults = runner.getResults();

            OptimizationResult bestResult = allResults.get(indexOfBestResult).getResult();
            MultiLayerNetwork bestModel = (MultiLayerNetwork) bestResult.getResultReference().getResultModel();

            System.out.println("\n\nConfiguration of best model:\n");
            System.out.println(bestModel.getLayerWiseConfigurations().toJson());

            //停止监控器
            //Thread.sleep(60000);
            //UIServer.getInstance().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class IrisDataSource implements DataSource {
        private DataSet trainData;
        private DataSet testData;

        public IrisDataSource() {

        }

        @Override
        public void configure(Properties properties) {
            try {
                RecordReader rr = new CSVRecordReader(0, ',');
                rr.initialize(new FileSplit(new ClassPathResource("kpi_dev.csv").getFile()));
                DataSet trainDataSet = new RecordReaderDataSetIterator(rr, 150, 4, 3).next();
                trainDataSet.shuffle();
                SplitTestAndTrain split = trainDataSet.splitTestAndTrain(0.8);
                trainData = split.getTrain();
                testData = split.getTest();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object trainData() {
            return new ListDataSetIterator(trainData.asList());
        }

        @Override
        public Object testData() {
            return new ListDataSetIterator(testData.asList());
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }
}
