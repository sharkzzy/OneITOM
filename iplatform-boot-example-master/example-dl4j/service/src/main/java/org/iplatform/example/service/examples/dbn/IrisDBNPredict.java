package org.iplatform.example.service.examples.dbn;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class IrisDBNPredict implements IExample {

    @Override
    public void run() {
        try {
            List<String> varietyList = Arrays.asList("Setosa", "Versicolor", "Virginica");

            File inputFile = new org.nd4j.linalg.io.ClassPathResource("IrisData/iris.csv").getFile();
            RecordReader rr = new CSVRecordReader();
            rr.initialize(new FileSplit(inputFile));

            MultiLayerNetwork net = MultiLayerNetwork.load(new File("iris.model"), true);
            int total = 0;
            int succeed = 0;
            int failed = 0;
            while (rr.hasNext()) {
                List<Writable> writables = rr.next();
                double sepallength = writables.get(0).toDouble();
                double sepalwidth = writables.get(1).toDouble();
                double petallength = writables.get(2).toDouble();
                double petalwidth = writables.get(3).toDouble();
                String variety = writables.get(4).toString();
                INDArray inputArray = Nd4j.create(new double[]{sepallength, sepalwidth, petallength, petalwidth});
                int[] predicted = net.predict(inputArray);
                System.out.println(inputArray + " -> " + variety + " -> " + varietyList.get(predicted[0]));
                total = total + 1;
                if (variety.equalsIgnoreCase(varietyList.get(predicted[0]))) {
                    succeed = succeed + 1;
                } else {
                    failed = failed + 1;
                }
            }
            System.out.println(String.format("total %d, succeed %d, failed %d", total, succeed, failed));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
