package org.iplatform.microservices.core.dataset.api.preprocessor.preprocessor.classimbalance;

import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 欠采样：丢弃一部分比例大的数据直到达到数据平衡
 */
public class UnderSamplingPreProcessor implements DataSetPreProcessor {
    int[] labels;
    Map<Integer, DataSet> classDataSet = new HashMap<>();

    public UnderSamplingPreProcessor(int[] labels) {
        this.labels = labels;
        for (int label : labels) {
            classDataSet.put(Integer.valueOf(label), null);
        }
    }

    @Override
    public void preProcess(DataSet dataSet) {
        DataSet empty = org.nd4j.linalg.dataset.DataSet.empty();
        DataSet dToPreProcess = dataSet.copy();
        dToPreProcess.getLabels();
        int min = Integer.MAX_VALUE;
        Iterator<Integer> it = classDataSet.keySet().iterator();
        while (it.hasNext()) {
            Integer classNum = it.next();
            DataSet ds = dToPreProcess.filterBy(new int[]{classNum});
            classDataSet.put(classNum, ds);
            if (ds.numExamples() < min) {
                min = ds.numExamples();
            }
        }

        it = classDataSet.keySet().iterator();
        while (it.hasNext()) {
            Integer classNum = it.next();
            DataSet ds = classDataSet.get(classNum);
            if (ds.numExamples() > min) {
                ds.shuffle();
                DataSet sampling_ds = ds.sample(min);
                empty = org.nd4j.linalg.dataset.DataSet.merge(Arrays.asList(empty, sampling_ds));
            }else{
                empty = org.nd4j.linalg.dataset.DataSet.merge(Arrays.asList(empty, ds));
            }
        }
        dataSet.setFeatures(empty.getFeatures());
        dataSet.setLabels(empty.getLabels());
    }
}
