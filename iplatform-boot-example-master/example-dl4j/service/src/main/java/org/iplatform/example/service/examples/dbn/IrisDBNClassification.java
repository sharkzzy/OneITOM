package org.iplatform.example.service.examples.dbn;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.ClassificationScoreCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * deeplearning4j IRIS example with dbn
 */
@Component
public class IrisDBNClassification implements IExample {

    private static final Logger LOG = LoggerFactory.getLogger(IrisDBNClassification.class);

    @Override
    public void run() {
        try {
            Nd4j.ENFORCE_NUMERICAL_STABILITY = true;
            int batchSize = 150;

            // 读取训练用数据
            RecordReader rr = new CSVRecordReader(0, ',');
            rr.initialize(new FileSplit(new ClassPathResource("IrisData/iris.txt").getFile()));
            DataSetIterator trainData = new RecordReaderDataSetIterator(rr, batchSize, 4, 3);

            MultiLayerConfiguration conf = getConfiguration();

            //最大循环
            int maxEpochs = 10000;
            //最大耗时
            int maxTimeMinutes = 20;

            //Early配置
            EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
                    .epochTerminationConditions(new MaxEpochsTerminationCondition(maxEpochs))
                    //.iterationTerminationConditions(new MaxScoreIterationTerminationCondition(0.8))
                    .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxTimeMinutes, TimeUnit.MINUTES))
                    //.scoreCalculator(new DataSetLossCalculator(trainData, true))
                    .scoreCalculator(new ClassificationScoreCalculator(Evaluation.Metric.F1,trainData))
                    .evaluateEveryNEpochs(1)
                    .modelSaver(new LocalFileModelSaver("/Users/zhanglei/Desktop/dl4j-model"))
                    .build();
            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf,conf,trainData);

            //Early fit
            EarlyStoppingResult result = trainer.fit();
            LOG.info("------------------- 训练结束 --------------------");
            LOG.info("Termination reason: " + result.getTerminationReason());
            LOG.info("Termination details: " + result.getTerminationDetails());
            LOG.info("Total epochs: " + result.getTotalEpochs());
            LOG.info("Best epoch number: " + result.getBestModelEpoch());
            LOG.info("Score at best epoch: " + result.getBestModelScore());

            //获取最佳模型
            MultiLayerNetwork bestModel = (MultiLayerNetwork) result.getBestModel();
            bestModel.save(new File("iris.model"),true);

            //使用测试数据对模型进行评估
            trainData.reset();
            DataSet testData  = trainData.next().copy();
            Evaluation eval = new Evaluation(3);
            INDArray output = bestModel.output(testData.getFeatures());
            eval.eval(testData.getLabels(), output);
            LOG.info(eval.stats());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MultiLayerConfiguration getConfiguration() {
        int numInputs = 4;
        int outputNum = 3;
        int numHiddenNodes = 20;
        double learningRate = 0.005;
        int seed = 6;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .updater(new RmsProp(learningRate))
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(outputNum).build())
                .build();

        return conf;
    }
}
