package org.iplatform.example.service.examples.kpi;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.DataAnalysis;
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
import java.util.Arrays;
import java.util.List;

@Component
public class KPIAnalysis implements IExample {
    private static Logger LOG = LoggerFactory.getLogger(KPIAnalysis.class);

    public void run() {
        try {
            Schema inputDataSchema = new Schema.Builder()
                    .addColumnString("时间")
                    .addColumnsDouble("kpi_业务探测成功率_全省")
                    .addColumnsDouble("kpi_业务量_合作式渠道")
                    .addColumnsDouble("kpi_业务量_客服")
                    .addColumnsDouble("kpi_业务量_营业厅")
                    .addColumnsDouble("kpi_平台登录量_crm")
                    .addColumnsDouble("kpi_平台登录量_客服")
                    .addColumnsDouble("kpi_平台登录量_智能crm")
                    .addColumnsDouble("kpi_接口调用系统成功率_合作式渠道系统")
                    .addColumnsDouble("kpi_接口调用系统成功率_手厅")
                    .addColumnsDouble("kpi_接口调用系统成功率_新版客户关系管理系统")
                    .addColumnsDouble("kpi_接口调用系统成功率_智能版crm")
                    .addColumnsDouble("kpi_接口调用系统成功率_短厅")
                    .addColumnsDouble("kpi_接口调用系统成功率_网厅")
                    .addColumnsDouble("kpi_活跃用户数_合作式渠道系统")
                    .addColumnsDouble("kpi_活跃用户数_营业厅")
                    .addColumnsDouble("kpi_前台卡顿_营业厅")
                    .addColumnsDouble("kpi_前台卡顿_合作式渠道")
                    .addColumnsDouble("kpi_智能CRM_套卡激活")
                    .addColumnsDouble("baseLine_kpi_业务探测成功率_全省")
                    .addColumnsDouble("baseLine_kpi_业务量_合作式渠道")
                    .addColumnsDouble("baseLine_kpi_业务量_客服")
                    .addColumnsDouble("baseLine_kpi_业务量_营业厅")
                    .addColumnsDouble("baseLine_kpi_平台登录量_crm")
                    .addColumnsDouble("baseLine_kpi_平台登录量_客服")
                    .addColumnsDouble("baseLine_kpi_平台登录量_智能crm")
                    .addColumnsDouble("baseLine_kpi_接口调用系统成功率_合作式渠道系统")
                    .addColumnsDouble("baseLine_kpi_接口调用系统成功率_手厅")
                    .addColumnsDouble("baseLine_kpi_接口调用系统成功率_新版客户关系管理系统")
                    .addColumnsDouble("baseLine_kpi_接口调用系统成功率_智能版crm")
                    .addColumnsDouble("baseLine_kpi_接口调用系统成功率_短厅")
                    .addColumnsDouble("baseLine_kpi_接口调用系统成功率_网厅")
                    .addColumnsDouble("baseLine_kpi_活跃用户数_合作式渠道系统")
                    .addColumnsDouble("baseLine_kpi_活跃用户数_营业厅")
                    .addColumnsDouble("baseLine_kpi_前台卡顿_营业厅")
                    .addColumnsDouble("baseLine_kpi_前台卡顿_合作式渠道")
                    .addColumnsDouble("baseLine_kpi_智能CRM_套卡激活")
                    .addColumnInteger("label1")
                    .addColumnInteger("label2")
                    .build();

            TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
                    .stringToTimeTransform("时间", "yyyy/MM/dd HH:mm", DateTimeZone.UTC)
                    .renameColumn("时间", "DateTime")
                    .transform(new DeriveColumnsFromTimeTransform.Builder("DateTime")
                            .addIntegerDerivedColumn("小时", DateTimeFieldType.hourOfDay())
                            .build())
                    .removeColumns("DateTime")
                    .build();

            Schema formatSchema = tp.getFinalSchema();
            LOG.info(formatSchema.toString());

            SparkConf conf = new SparkConf();
            conf.setMaster("local[*]");
            conf.setAppName("DataVec Example");

            JavaSparkContext sc = new JavaSparkContext(conf);

            String csvfile = new ClassPathResource("data/kpi/kpi.csv").getFile().getAbsolutePath();

            JavaRDD<String> allRows = sc.textFile(csvfile);
            List<String> headers = Arrays.asList(allRows.take(1).get(0).split(","));
            String field = "时间";
            JavaRDD<String> dataWithoutHeaders = allRows.filter(x -> !(x.split(",")[headers.indexOf(field)]).equals(field));
            RecordReader rr = new CSVRecordReader();
            JavaRDD<List<Writable>> parsedInputData = dataWithoutHeaders.map(new StringToWritablesFunction(rr));

            JavaRDD<List<Writable>> processedData = SparkTransformExecutor.execute(parsedInputData, tp);


            int maxHistogramBuckets = 10;
            DataAnalysis dataAnalysis = AnalyzeSpark.analyze(formatSchema, processedData, maxHistogramBuckets);

            System.out.println(dataAnalysis);

            HtmlAnalysis.createHtmlAnalysisFile(dataAnalysis, new File("KPIAnalysis.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}