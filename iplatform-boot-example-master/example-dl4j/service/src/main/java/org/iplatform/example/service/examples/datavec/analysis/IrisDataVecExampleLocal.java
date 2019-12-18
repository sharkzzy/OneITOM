package org.iplatform.example.service.examples.datavec.analysis;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.datavec.local.transforms.misc.WritablesToStringFunction;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.linalg.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class IrisDataVecExampleLocal implements IExample {
    @Override
    public void run() {
        try {
            Schema schema = new Schema.Builder()
                    .addColumnsDouble("Sepal length", "Sepal width", "Petal length", "Petal width")
                    .addColumnInteger("Species")
                    .build();

            TransformProcess tp = new TransformProcess.Builder(schema)
                    .removeColumns("Species")
                    .build();

            File inputFile = new ClassPathResource("BasicDataVecExample/iris.data").getFile();
            RecordReader rr = new CSVRecordReader(0, ',');
            rr.initialize(new FileSplit(inputFile));

            List<List<Writable>> originalData = new ArrayList<>();
            while (rr.hasNext()) {
                originalData.add(rr.next());
            }

            System.out.println("\n\n---- Original Data File ----");
            for(List<Writable> writables : originalData){
                WritablesToStringFunction f = new WritablesToStringFunction(",");
                String line = f.apply(writables);
                System.out.println(line);
            }

            List<List<Writable>> processedData = LocalTransformExecutor.execute(originalData, tp);

            System.out.println("\n\n---- Processed Data File ----");
            for(List<Writable> writables : processedData){
                WritablesToStringFunction f = new WritablesToStringFunction(",");
                String line = f.apply(writables);
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
