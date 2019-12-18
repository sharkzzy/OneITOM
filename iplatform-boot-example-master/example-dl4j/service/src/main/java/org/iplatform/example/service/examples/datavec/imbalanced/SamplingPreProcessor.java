package org.iplatform.example.service.examples.datavec.imbalanced;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.iplatform.example.service.examples.IExample;
import org.iplatform.microservices.core.dataset.api.preprocessor.preprocessor.classimbalance.SmoteSamplingPreProcessor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

@Component
public class SamplingPreProcessor implements IExample {

    @Override
    public void run() {
        try {
            DataSet dataset = getDataSet();
            //开启debug模式，自动把label扩充为3类，类别3为生成的数据
            SmoteSamplingPreProcessor preProcessor = new SmoteSamplingPreProcessor.Builder().numPossibleLabels(2).near_count(5).loss_factor(0.1f).sample_rate(1).debug(true).build();
            //SmoteSamplingPreProcessor preProcessor = new SmoteSamplingPreProcessor.Builder().numPossibleLabels(2).near_count(5).loss_factor(0.1f).sample_rate(1).build();
            //SmoteSamplingPreProcessor preProcessor = new SmoteSamplingPreProcessor.Builder().numPossibleLabels(2).near_count(5).loss_factor(0.1f).max_amount(1000).build();
            preProcessor.preProcess(dataset);

            // 打印图形
            JPanel panel = new ChartPanel(createChart(dataset));
            JFrame f = new JFrame();
            f.add(panel);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.pack();
            f.setTitle("Data");
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataSet getDataSet() {
        RecordReader rr = new CSVRecordReader();
        try {
            final File file = new ClassPathResource("data/sampling/linear_data_train.csv").getFile();
            rr.initialize(new FileSplit(file));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr, 1000, 0, 2);
        DataSet dataset = trainIter.next();
        dataset.getLabels();
        return dataset;
    }

    private XYDataset createDataSetTrain(INDArray features, INDArray labels) {
        int nRows = features.rows();
        int nClasses = labels.columns();
        XYSeries[] series = new XYSeries[nClasses];
        for (int i = 0; i < series.length; i++) series[i] = new XYSeries("Class " + String.valueOf(i));
        INDArray argMax = Nd4j.getExecutioner().exec(new IMax(labels), 1);
        for (int i = 0; i < nRows; i++) {
            int classIdx = (int) argMax.getDouble(i);
            series[classIdx].add(features.getDouble(i, 0), features.getDouble(i, 1));
        }
        XYSeriesCollection c = new XYSeriesCollection();
        for (XYSeries s : series) c.addSeries(s);
        return c;
    }

    private JFreeChart createChart(DataSet dataset) {
        double[] mins = dataset.getFeatures().min(0).data().asDouble();
        double[] maxs = dataset.getLabels().max(0).data().asDouble();
        XYDataset xyData = createDataSetTrain(dataset.getFeatures(), dataset.getLabels());

        //x坐标
        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setRange(mins[0], maxs[0]);

        //y坐标
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setRange(mins[1], maxs[1]);

        JFreeChart chart = ChartFactory.createScatterPlot("", "X", "Y", xyData, PlotOrientation.VERTICAL, true, true, false);
        chart.getXYPlot().getDomainAxis().setRange(mins[0], maxs[0]);
        chart.getXYPlot().getRangeAxis().setRange(mins[1], maxs[1]);
        chart.getXYPlot().setDataset(xyData);
        ;
        return chart;
    }
}
