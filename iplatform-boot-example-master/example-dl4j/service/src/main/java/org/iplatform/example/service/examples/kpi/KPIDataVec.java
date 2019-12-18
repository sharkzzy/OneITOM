package org.iplatform.example.service.examples.kpi;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.time.DeriveColumnsFromTimeTransform;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.iplatform.example.service.examples.IExample;
import org.iplatform.microservices.core.dataset.api.preprocessor.preprocessor.classimbalance.SmoteSamplingPreProcessor;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.nd4j.linalg.dataset.DataSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class KPIDataVec implements IExample {

    @Override
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
                    .removeColumns("label1")
                    .removeColumns("baseLine_kpi_业务探测成功率_全省")
                    .removeColumns("baseLine_kpi_业务量_合作式渠道")
                    .removeColumns("baseLine_kpi_业务量_客服")
                    .removeColumns("baseLine_kpi_业务量_营业厅")
                    .removeColumns("baseLine_kpi_平台登录量_crm")
                    .removeColumns("baseLine_kpi_平台登录量_客服")
                    .removeColumns("baseLine_kpi_平台登录量_智能crm")
                    .removeColumns("baseLine_kpi_接口调用系统成功率_合作式渠道系统")
                    .removeColumns("baseLine_kpi_接口调用系统成功率_手厅")
                    .removeColumns("baseLine_kpi_接口调用系统成功率_新版客户关系管理系统")
                    .removeColumns("baseLine_kpi_接口调用系统成功率_智能版crm")
                    .removeColumns("baseLine_kpi_接口调用系统成功率_短厅")
                    .removeColumns("baseLine_kpi_接口调用系统成功率_网厅")
                    .removeColumns("baseLine_kpi_活跃用户数_合作式渠道系统")
                    .removeColumns("baseLine_kpi_活跃用户数_营业厅")
                    .removeColumns("baseLine_kpi_前台卡顿_营业厅")
                    .removeColumns("baseLine_kpi_前台卡顿_合作式渠道")
                    .removeColumns("baseLine_kpi_智能CRM_套卡激活")
                    .renameColumn("label2", "label")
                    .build();

            File csvfile = new ClassPathResource("data/kpi/kpi.csv").getFile();
            RecordReader rr = new CSVRecordReader(1, ',');
            rr.initialize(new FileSplit(csvfile));

            List<List<Writable>> originalData = new ArrayList<>();
            while (rr.hasNext()) {
                originalData.add(rr.next());
            }

            File outputFile = new File("kpi_dev.csv");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();
            RecordWriter rw = new CSVRecordWriter();
            Partitioner p = new NumberOfRecordsPartitioner();
            rw.initialize(new FileSplit(outputFile), p);

            List<List<Writable>> processedData = LocalTransformExecutor.execute(originalData, tp);
            rw.writeBatch(processedData);
            rw.close();

            System.out.println(tp.getFinalSchema());

            rr = new CSVRecordReader(0, ',');
            rr.initialize(new FileSplit(outputFile));
            DataSet dataset = new RecordReaderDataSetIterator(rr, processedData.size(), 19, 2).next();
            SmoteSamplingPreProcessor preProcessor = new SmoteSamplingPreProcessor.Builder().numPossibleLabels(2).near_count(5).loss_factor(0.1f).sample_rate(1).build();
            preProcessor.preProcess(dataset);


            dataset.save(new File("kpi_dev_smote.csv"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
