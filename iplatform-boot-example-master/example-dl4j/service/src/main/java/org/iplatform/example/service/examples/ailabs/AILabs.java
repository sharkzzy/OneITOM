package org.iplatform.example.service.examples.ailabs;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.join.Join;
import org.datavec.api.transform.metadata.ColumnMetaData;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.time.DeriveColumnsFromTimeTransform;
import org.datavec.api.transform.ui.HtmlAnalysis;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.AnalyzeSpark;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.iplatform.example.service.examples.IExample;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class AILabs implements IExample {
    private static Logger LOG = LoggerFactory.getLogger(AILabs.class);

    @Override
    public void run() {
        try {
            // 定义 Label Schema
            Schema schemaLabel = new Schema.Builder()
                    .addColumnString("DateTimeString")
                    .addColumnInteger("Classes")
                    .build();
            LOG.info("----- 原始 Label Schema -----");
            LOG.info(schemaLabel.toString());

            // 定义 Data Schema
            Schema schemaData = new Schema.Builder()
                    .addColumnString("DateTimeString")
                    .addColumnDouble("RateBusinessAll") //业务探测成功率_全省
                    .addColumnDouble("AmountChannel") //业务量_合作式渠道
                    .addColumnDouble("AmountCRM") //业务量_客服
                    .addColumnDouble("AmountHall") //业务量_营业厅
                    .addColumnDouble("AmountLoginCRM") //平台登录量_crm
                    .addColumnDouble("AmountLoginCustomerService") //平台登录量_客服
                    .addColumnDouble("AmountLoginSmartCRM") //平台登录量_智能crm
                    .addColumnDouble("RateIFChannel") //接口调用系统成功率_合作式渠道系统
                    .addColumnDouble("RateIFMobileHall") //接口调用系统成功率_手厅
                    .addColumnDouble("RateIFNewCRM") //接口调用系统成功率_新版客户关系管理系统
                    .addColumnDouble("RateIFSmartCRM") //接口调用系统成功率_智能版crm
                    .addColumnDouble("RateIFSMS") //接口调用系统成功率_短厅
                    .addColumnDouble("RateIFNetHall") //接口调用系统成功率_网厅
                    .addColumnDouble("ActiveUsersChannel") //活跃用户数_合作式渠道系统
                    .addColumnDouble("ActiveUsersHall") //活跃用户数_营业厅
                    .build();
            LOG.info("----- 原始 Data Schema -----");
            LOG.info(schemaData.toString());

            // 定义 DataLabel Schema
            List<ColumnMetaData> metas = schemaData.getColumnMetaData();
            Schema.Builder schemaBuilder = new Schema.Builder();
            for(ColumnMetaData meta : metas){
                schemaBuilder.addColumn(meta);
            }
            schemaBuilder.addColumnInteger("Classes");
            Schema schemaDataLabel = schemaBuilder.build();
            LOG.info("----- 原始 Data Label Schema -----");
            LOG.info(schemaDataLabel.toString());


            // 配置 Shark
            SparkConf conf = new SparkConf();
            conf.setMaster("local[*]");
            conf.setAppName("DataVec Example");
            JavaSparkContext sc = new JavaSparkContext(conf);

            RecordReader rr = new CSVRecordReader();
            // 加载原始数据
            String datafile = new ClassPathResource("ailabs/data.csv").getFile().getAbsolutePath();
            JavaRDD<String> stringData = sc.textFile(datafile);
            JavaRDD<List<Writable>> parsedInputData = stringData.filter((x) -> !x.isEmpty()).map(new StringToWritablesFunction(rr));

            // 加载原始标签
            String labelfile = new ClassPathResource("ailabs/label.csv").getFile().getAbsolutePath();
            JavaRDD<String> stringLabel = sc.textFile(labelfile);
            JavaRDD<List<Writable>> parsedInputLabel = stringLabel.filter((x) -> !x.isEmpty()).map(new StringToWritablesFunction(rr));

            // 数据标签连接
            Join join = new Join.Builder(Join.JoinType.Inner)
                    .setJoinColumns("DateTimeString")
                    .setSchemas(schemaData, schemaLabel)
                    .build();
            JavaRDD<List<Writable>> inputDataLabel = SparkTransformExecutor.executeJoin(join, parsedInputData, parsedInputLabel);
            toFile(new File("data_label.csv"),inputDataLabel);

            // 原始数据转换
            //定义 Schema 转换
            TransformProcess tp = new TransformProcess.Builder(schemaDataLabel)
                    .stringToTimeTransform("DateTimeString", "yyyy-MM-dd HH:mm:ss", DateTimeZone.UTC)
                    .renameColumn("DateTimeString", "DateTime")
                    .transform(new DeriveColumnsFromTimeTransform.Builder("DateTime")
                            .addIntegerDerivedColumn("HourOfDay", DateTimeFieldType.hourOfDay())
                            .build())
                    .removeColumns("DateTime")
                    .build();
            Schema schema = tp.getFinalSchema();
            LOG.info("----- 转换后 Data Label Schema -----");
            LOG.info(schema.toString());
            JavaRDD<List<Writable>> processedData = SparkTransformExecutor.execute(inputDataLabel, tp);

            // 转换后数据落地
            toFile(new File("data_label_vector.csv"),processedData);

//            JavaRDD<String> processedAsString = parsedInputData.map(new WritablesToStringFunction(","));
//            List<String> inputDataParsed = processedAsString.collect();
//            processedAsString = processedData.map(new WritablesToStringFunction(","));
//            inputDataParsed = processedAsString.collect();
//            System.out.println("\n\n---- Parsed and filtered data ----");
//            for (String s : inputDataParsed) System.out.println(s);

            // 数据报告
            int maxHistogramBuckets = 20;
            DataAnalysis dataAnalysis = AnalyzeSpark.analyze(schema, processedData, maxHistogramBuckets);
            System.out.println(dataAnalysis);
            HtmlAnalysis.createHtmlAnalysisFile(dataAnalysis, new File("ailabs.html"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toFile(File outputFile, JavaRDD<List<Writable>> data) throws Exception {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        RecordWriter rw = new CSVRecordWriter();
        Partitioner p = new NumberOfRecordsPartitioner();
        rw.initialize(new FileSplit(outputFile), p);
        rw.writeBatch(data.collect());
        rw.close();
    }


//    public static void main(String[] ags){
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
//        DateTime dt1 =formatter.parseDateTime("2018-07-01 00:04:00");
//        DateTime dt2 =formatter.parseDateTime("2018-07-01 00:05:00");
//        System.out.println(dt1.hourOfDay());
//    }
}
