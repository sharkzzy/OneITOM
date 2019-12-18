package org.iplatform.example.service.examples.distance;

import org.datavec.api.transform.Distance;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.iplatform.example.service.examples.IExample;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 求欧氏距离
 */
@Component
public class EuclideanDistanceExample implements IExample {
    @Override
    public void run() {
        try {
            Schema s = new Schema.Builder()
                    .addColumnDouble("col0").addColumnNDArray("col1", new long[]{1, 10})
                    .addColumnNDArray("col2", new long[]{1, 10}).build();

            TransformProcess tp = new TransformProcess.Builder(s)
                    .ndArrayDistanceTransform("dist", Distance.COSINE, "col1", "col2").build();


            List<String> expColNames = Arrays.asList("col0", "col1", "col2", "dist");
            assertEquals(expColNames, tp.getFinalSchema().getColumnNames());

            Nd4j.getRandom().setSeed(12345);
            INDArray arr1 = Nd4j.rand(1, 10);
            INDArray arr2 = Nd4j.rand(1, 10);
            double cosine = Transforms.cosineSim(arr1, arr2); // 计算点之间的余弦相似度

            List<Writable> in = Arrays.<Writable>asList(new DoubleWritable(0), new NDArrayWritable(arr1.dup()),
                    new NDArrayWritable(arr2.dup()));
            List<Writable> out = tp.execute(in);

            List<Writable> exp = Arrays.<Writable>asList(new DoubleWritable(0), new NDArrayWritable(arr1),
                    new NDArrayWritable(arr2), new DoubleWritable(cosine));

            assertEquals(exp, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
