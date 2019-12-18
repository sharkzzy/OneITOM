package org.iplatform.microservices.core.dataset.api.preprocessor.preprocessor.classimbalance;

import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 过采样：重复比例小的数据直到达到数据平衡
 */
public class OverSamplingPreProcessor implements DataSetPreProcessor {
    int[] labels;
    Map<Integer, DataSet> classDataSet = new HashMap<>();

    public OverSamplingPreProcessor(int[] labels) {
        this.labels = labels;
        for (int label : labels) {
            classDataSet.put(Integer.valueOf(label), null);
        }
    }

    @Override
    public void preProcess(DataSet dataSet) {
        DataSet dToPreProcess = dataSet.copy();
        dToPreProcess.getLabels();
        int max = 0;
        Iterator<Integer> it = classDataSet.keySet().iterator();
        while (it.hasNext()) {
            Integer classNum = it.next();
            DataSet ds = dToPreProcess.filterBy(new int[]{classNum});
            classDataSet.put(classNum, ds);
            if (ds.numExamples() > max) {
                max = ds.numExamples();
            }
        }

        it = classDataSet.keySet().iterator();
        while (it.hasNext()) {
            Integer classNum = it.next();
            DataSet ds = classDataSet.get(classNum);
            if (ds.numExamples() < max) {
                ds.shuffle();
                DataSet sampling_ds = ds.sample(max - ds.numExamples());
                DataSet mergeds = org.nd4j.linalg.dataset.DataSet.merge(Arrays.asList(dataSet, sampling_ds));
                dataSet.setFeatures(mergeds.getFeatures());
                dataSet.setLabels(mergeds.getLabels());
            }
        }
    }
}
