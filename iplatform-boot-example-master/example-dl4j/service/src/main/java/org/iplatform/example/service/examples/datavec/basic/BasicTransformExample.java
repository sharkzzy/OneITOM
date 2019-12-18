package org.iplatform.example.service.examples.datavec.basic;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.datavec.spark.transform.misc.WritablesToStringFunction;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.linalg.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Another basic preprocessing and filtering example. We download the raw iris dataset from the internet.
 * The file we downloaded has 2 problems:
 * 1. an empty line at the bottom.
 * 2. the labels are in the form of strings. We want an integer.
 * The output matches the iris.txt file you can find in the resources of the datavec and dl4j examples
 **/
@Component
public class BasicTransformExample implements IExample {
    @Override
    public void run() {
        try {
            String path = new ClassPathResource("BasicDataVecExample/iris.data").getFile().getAbsolutePath();

            SparkConf conf = new SparkConf();
            conf.setMaster("local[*]");
            conf.setAppName("DataVec Example");

            JavaSparkContext sc = new JavaSparkContext(conf);
            JavaRDD<String> stringData = sc.textFile(path);

            //Take out empty lines.
            RecordReader rr = new CSVRecordReader();
            JavaRDD<List<Writable>> parsedInputData = stringData.filter((x) -> !x.isEmpty()).map(new StringToWritablesFunction(rr));

            // Print the original text file.  Not the empty line at the bottom,
            List<String> inputDataCollected = stringData.collect();
            System.out.println("\n\n---- Original Data ----");
            for (String s : inputDataCollected) System.out.println("'" + s + "'");

            //
            JavaRDD<String> processedAsString = parsedInputData.map(new WritablesToStringFunction(","));
            List<String> inputDataParsed = processedAsString.collect();
            System.out.println("\n\n---- Parsed Data ----");
            for (String s : inputDataParsed) System.out.println("'" + s + "'");

            // the String to label conversion. Define schema and transform:
            Schema schema = new Schema.Builder()
                    .addColumnsDouble("Sepal length", "Sepal width", "Petal length", "Petal width")
                    .addColumnCategorical("Species", "Iris-setosa", "Iris-versicolor", "Iris-virginica")
                    .build();

            TransformProcess tp = new TransformProcess.Builder(schema)
                    .categoricalToInteger("Species")
                    .build();

            // do the transformation.
            JavaRDD<List<Writable>> processedData = SparkTransformExecutor.execute(parsedInputData, tp);

            // This is where we print the final result (which you would save to a text file.
            processedAsString = processedData.map(new WritablesToStringFunction(","));
            inputDataParsed = processedAsString.collect();
            System.out.println("\n\n---- Parsed and filtered data ----");
            for (String s : inputDataParsed) System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
