package org.iplatform.example.service.examples.datavec.basic;

import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.column.CategoricalColumnCondition;
import org.datavec.api.transform.condition.column.DoubleColumnCondition;
import org.datavec.api.transform.filter.ConditionFilter;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.time.DeriveColumnsFromTimeTransform;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.iplatform.example.service.examples.IExample;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.nd4j.linalg.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class BasicDataVecExampleLocal implements IExample {
    @Override
    public void run() {
        try {
//=====================================================================
            //                 Step 1: Define the input data schema
            //=====================================================================

            //Let's define the schema of the data that we want to import
            //The order in which columns are defined here should match the order in which they appear in the input data
            Schema inputDataSchema = new Schema.Builder()
                    //We can define a single column
                    .addColumnString("DateTimeString")
                    //Or for convenience define multiple columns of the same type
                    .addColumnsString("CustomerID", "MerchantID")
                    //We can define different column types for different types of data:
                    .addColumnInteger("NumItemsInTransaction")
                    .addColumnCategorical("MerchantCountryCode", Arrays.asList("USA", "CAN", "FR", "MX"))
                    //Some columns have restrictions on the allowable values, that we consider valid:
                    .addColumnDouble("TransactionAmountUSD", 0.0, null, false, false)   //$0.0 or more, no maximum limit, no NaN and no Infinite values
                    .addColumnCategorical("FraudLabel", Arrays.asList("Fraud", "Legit"))
                    .build();

            //Print out the schema:
            System.out.println("Input data schema details:");
            System.out.println(inputDataSchema);

            System.out.println("\n\nOther information obtainable from schema:");
            System.out.println("Number of columns: " + inputDataSchema.numColumns());
            System.out.println("Column names: " + inputDataSchema.getColumnNames());
            System.out.println("Column types: " + inputDataSchema.getColumnTypes());


            //=====================================================================
            //            Step 2: Define the operations we want to do
            //=====================================================================

            //Lets define some operations to execute on the data...
            //We do this by defining a TransformProcessEx
            //At each step, we identify column by the name we gave them in the input data schema, above

            TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
                    //Let's remove some column we don't need
                    .removeColumns("CustomerID", "MerchantID")

                    //Now, suppose we only want to analyze transactions involving merchants in USA or Canada. Let's filter out
                    // everything except for those countries.
                    //Here, we are applying a conditional filter. We remove all of the examples that match the condition
                    // The condition is "MerchantCountryCode" isn't one of {"USA", "CAN"}
                    .filter(new ConditionFilter(
                            new CategoricalColumnCondition("MerchantCountryCode", ConditionOp.NotInSet, new HashSet<>(Arrays.asList("USA", "CAN")))))

                    //Let's suppose our data source isn't perfect, and we have some invalid data: negative dollar amounts that we want to replace with 0.0
                    //For positive dollar amounts, we don't want to modify those values
                    //Use the ConditionalReplaceValueTransform on the "TransactionAmountUSD" column:
                    .conditionalReplaceValueTransform(
                            "TransactionAmountUSD",     //Column to operate on
                            new DoubleWritable(0.0),    //New value to use, when the condition is satisfied
                            new DoubleColumnCondition("TransactionAmountUSD", ConditionOp.LessThan, 0.0)) //Condition: amount < 0.0

                    //Finally, let's suppose we want to parse our date/time column in a format like "2016/01/01 17:50.000"
                    //We use JodaTime internally, so formats can be specified as follows: http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
                    .stringToTimeTransform("DateTimeString", "YYYY-MM-DD HH:mm:ss.SSS", DateTimeZone.UTC)

                    //However, our time column ("DateTimeString") isn't a String anymore. So let's rename it to something better:
                    .renameColumn("DateTimeString", "DateTime")

                    //At this point, we have our date/time format stored internally as a long value (Unix/Epoch format): milliseconds since 00:00.000 01/01/1970
                    //Suppose we only care about the hour of the day. Let's derive a new column for that, from the DateTime column
                    .transform(new DeriveColumnsFromTimeTransform.Builder("DateTime")
                            .addIntegerDerivedColumn("HourOfDay", DateTimeFieldType.hourOfDay())
                            .build())

                    //We no longer need our "DateTime" column, as we've extracted what we need from it. So let's remove it
                    .removeColumns("DateTime")

                    //We've finished with the sequence of operations we want to do: let's create the final TransformProcessEx object
                    .build();


            //After executing all of these operations, we have a new and different schema:
            Schema outputSchema = tp.getFinalSchema();

            System.out.println("\n\n\nSchema after transforming data:");
            System.out.println(outputSchema);


            //=====================================================================
            //      Step 3: Load our data and execute the operations locally
            //=====================================================================

            //Define input and output paths:
            File inputFile = new ClassPathResource("BasicDataVecExample/exampledata.csv").getFile();
            File outputFile = new File("BasicDataVecExampleLocalOut.csv");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();

            //Define input reader and output writer:
            RecordReader rr = new CSVRecordReader(0, ',');
            rr.initialize(new FileSplit(inputFile));

            RecordWriter rw = new CSVRecordWriter();
            Partitioner p = new NumberOfRecordsPartitioner();
            rw.initialize(new FileSplit(outputFile), p);

            //Process the data:
            List<List<Writable>> originalData = new ArrayList<>();
            while (rr.hasNext()) {
                originalData.add(rr.next());
            }

            List<List<Writable>> processedData = LocalTransformExecutor.execute(originalData, tp);


            System.out.println("\n\n---- Processed Data File 2 ----");
            rw.writeBatch(processedData);
            rw.close();


            //Print before + after:
            System.out.println("\n\n---- Original Data File ----");
            String originalFileContents = FileUtils.readFileToString(inputFile);
            System.out.println(originalFileContents);

            String fileContents = FileUtils.readFileToString(outputFile);
            System.out.println(fileContents);

//            System.out.println("\n\nDONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
