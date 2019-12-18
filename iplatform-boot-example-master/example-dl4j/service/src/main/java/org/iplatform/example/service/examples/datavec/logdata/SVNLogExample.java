package org.iplatform.example.service.examples.datavec.logdata;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.regex.RegexLineRecordReader;
import org.datavec.api.transform.ReduceOp;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.quality.DataQualityAnalysis;
import org.datavec.api.transform.reduce.Reducer;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.AnalyzeSpark;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.iplatform.example.service.examples.IExample;
import org.iplatform.microservices.core.datavec.api.transform.TransformProcess.TransformProcessEx;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Simple example performing some preprocessing/aggregation operations on some web log data using DataVec.
 * Specifically:
 * - Load some data
 * - Perform data quality analysis
 * - Perform basic data cleaning and preprocessing
 * - Group records by host, and calculate some aggregate values for each (such as number of requests and total number of bytes)
 * - Analyze the resulting data, and print some results
 *
 * Examples of some log lines
 * [30/Nov/2018:09:31:52 +0800] lichenglong@boco.com.cn commit r10779
 * [30/Nov/2018:09:34:41 +0800] jobtools checkout-or-export /itosi-microservices/iplatform-bomc/branches/1.0.0.100/bomc-service r10780 depth=infinity
 * [30/Nov/2018:09:35:25 +0800] lichenglong@boco.com.cn get-dir /BOMC-V3.0/04_SCL/APP/hennan_webalarm/WEB_FM/ r145535 text
 *
 * @author Alex Black
 */

@Component
public class SVNLogExample implements IExample ,Serializable {

    @Override
    public void run() {
        try {
            SparkConf conf = new SparkConf();
            conf.setMaster("local[*]");
            conf.setAppName("DataVec Log Data Example");
            JavaSparkContext sc = new JavaSparkContext(conf);


            //=====================================================================
            //                 Step 1: Define the input data schema
            //=====================================================================

            //First: let's specify a schema for the data. This is based on the information from: http://ita.ee.lbl.gov/html/contrib/NASA-HTTP.html
            Schema schema = new Schema.Builder()
                    .addColumnString("timestamp")
                    .addColumnString("username")
                    //.addColumnString("command")
                    .build();

            //=====================================================================
            //                     Step 2: Clean Invalid Lines
            //=====================================================================

            //Second: let's load the data. Initially as Strings
            JavaRDD<String> logLines = sc.textFile("/Volumes/MyWallet/data/svnlog");

            String regex = "\\[(\\S+ \\+\\d{4})\\] (\\S+) commit \\S+";
            logLines = logLines.filter(new Function<String, Boolean>() {
                @Override
                public Boolean call(String s) throws Exception {
                    return s.matches(regex);   //Regex for the format we expect
                }
            });
            RecordReader rr = new RegexLineRecordReader(regex, 0);
            JavaRDD<List<Writable>> parsed = logLines.map(new StringToWritablesFunction(rr));
            List<List<Writable>> parsedData = parsed.collect();

            DataQualityAnalysis dqa = AnalyzeSpark.analyzeQuality(schema, parsed);
            System.out.println("----- Data Quality -----");
            System.out.println(dqa);


            TransformProcessEx tp = new TransformProcessEx.Builder(schema)
                    .stringToTimeTransform("timestamp", "dd/MMM/YYYY:HH:mm:ss Z", DateTimeZone.forOffsetHours(+8),Locale.US)
                    .reduce(new Reducer.Builder(ReduceOp.CountUnique)
                            .keyColumns("username")                 //keyColumns == columns to group by
                            .countColumns("timestamp")          //Count the number of values
                            .build())
                    .renameColumn("count(timestamp)", "commitNum")
                    .build();

            JavaRDD<List<Writable>> processed = SparkTransformExecutor.execute(parsed, tp);
            processed.cache();


            //=====================================================================
            //       Step 5: Perform Analysis on Final Data; Display Results
            //=====================================================================

            Schema finalDataSchema = tp.getFinalSchema();
            long finalDataCount = processed.count();
            List<List<Writable>> sample = processed.take(100);

            DataAnalysis analysis = AnalyzeSpark.analyze(finalDataSchema, processed);

            sc.stop();
            Thread.sleep(4000); //Give spark some time to shut down (and stop spamming console)


            System.out.println("----- Final Data Schema -----");
            System.out.println(finalDataSchema);

            System.out.println("\n\nFinal data count: " + finalDataCount);

            System.out.println("\n\n----- Samples of final data -----");
            for (List<Writable> l : sample) {
                System.out.println(l);
            }

            System.out.println("\n\n----- Analysis -----");
            System.out.println(analysis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//    public static void main(String[] args){
//        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MMM/YYYY:HH:mm:ss Z").withZone(DateTimeZone.forOffsetHours(-4)).withLocale(Locale.US);
//        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);
//        String a = dateTime.toString(fmt);
//        DateTime dt = fmt.parseDateTime("17/Jul/1995:13:25:22 -0400");
//        System.out.print(dt);
//    }
}
