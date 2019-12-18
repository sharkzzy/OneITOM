package org.iplatform.microservices.core.dataset.api.preprocessor.preprocessor.classimbalance;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * SMOTE：随机选取比例小的数据进行插值，直到达到数据平衡
 */
public class SmoteSamplingPreProcessor implements DataSetPreProcessor {

    private static Logger LOG = LoggerFactory.getLogger(SmoteSamplingPreProcessor.class);
    Map<Integer, DataSet> classDataSet = new HashMap<>();

    boolean debug = false;
    int numPossibleLabels; //分类数量
    int sample_rate = 1; //正负样本要达到的比例
    int max_amount = 0; //生成样本的最大数量（优先于sample_rate）
    float loss_factor = 0.1f; //少类样本随机选择比例（默认取10%的样本）
    int near_count = 5;//获取样本点的临近点数量

    public SmoteSamplingPreProcessor(int numPossibleLabels, int max_amount, float loss_factor, int near_count, int sample_rate, boolean debug) {
        this.numPossibleLabels = numPossibleLabels;
        this.max_amount = max_amount;
        this.loss_factor = loss_factor;
        this.near_count = near_count;
        this.sample_rate = sample_rate;
        this.debug = debug;
        for (int num = 0; num < numPossibleLabels; num++) {
            classDataSet.put(Integer.valueOf(num), null);
        }
    }

    @Override
    public void preProcess(DataSet dataSet) {
        DataSet dToPreProcess = dataSet.copy();

        if (this.debug) {
            INDArray larray = dataSet.getLabels();
            INDArray larray_ex = Nd4j.zeros(larray.rows(), 1);
            dataSet.setLabels(Nd4j.hstack(larray, larray_ex));
        }

        int max = 0;
        for (int num = 0; num < numPossibleLabels; num++) {
            DataSet ds = dToPreProcess.filterBy(new int[]{num});
            classDataSet.put(Integer.valueOf(num), ds);
            if (ds.numExamples() > max) {
                max = ds.numExamples();
            }
        }


        Iterator<Integer> it = classDataSet.keySet().iterator();
        while (it.hasNext()) {
            Integer classNum = it.next();
            DataSet less_dataset = classDataSet.get(classNum);
            if (less_dataset.numExamples() < max) {
                int amount = 0;
                if (max_amount > 0) {
                    amount = max_amount;
                } else {
                    amount = (max / this.sample_rate) - less_dataset.numExamples();
                }

                LOG.info("生成样本数量 {}", amount);

                int new_amount = 0;
                while (new_amount < amount) {
                    /**
                     * 随机选取少量样本数据的10%作为扩充基点
                     * */
                    less_dataset.shuffle();
                    DataSet smote_base_ds = less_dataset.sample(Float.valueOf(less_dataset.numExamples() * loss_factor).intValue());
                    LOG.debug("stome 基点数量 {}", smote_base_ds.numExamples());

                    /**
                     * 计算每条基点数据的N个邻居（欧式距离）
                     * */
                    Iterator<org.nd4j.linalg.dataset.DataSet> baseit = smote_base_ds.iterator();
                    while (baseit.hasNext()) {
                        List<Distance> distanceSameList = new ArrayList<>();
                        org.nd4j.linalg.dataset.DataSet xds = baseit.next();

                        //基点
                        INDArray xattr = xds.getFeatures();
                        INDArray xlabel = xds.getLabels();
                        Iterator<org.nd4j.linalg.dataset.DataSet> allit = less_dataset.iterator();
                        while (allit.hasNext()) {
                            // 遍历计算点
                            org.nd4j.linalg.dataset.DataSet yds = allit.next();
                            INDArray yattr = yds.getFeatures();
                            double distance = Transforms.euclideanDistance(xattr, yattr); //计算两点间的欧式距离
                            if (distance > 0) {
                                //排除基点自己
                                Distance set = new Distance(distance, xattr, yattr);
                                distanceSameList.add(set);
                            }
                        }

                        //根据欧式距离排序后取出距离基点临近的5个点
                        Collections.sort(distanceSameList);
                        distanceSameList = distanceSameList.subList(0, near_count);
                        for (Distance distanceSame : distanceSameList) {
                            //采用mean方法获取两点间一个meat点
                            INDArray new_attr = Nd4j.vstack(distanceSame.xattr, distanceSame.yattr).mean(0);
                            INDArray featuresAttr = Nd4j.vstack(dataSet.getFeatures(), new_attr);
                            dataSet.setFeatures(featuresAttr);

                            if(this.debug){
                                INDArray new_label = Nd4j.create(new double[][]{{0, 0, 1}});
                                INDArray labelsAttr = Nd4j.vstack(dataSet.getLabels(), new_label);
                                dataSet.setLabels(labelsAttr);
                            }else{
                                INDArray labelsAttr = Nd4j.vstack(dataSet.getLabels(), xlabel);
                                dataSet.setLabels(labelsAttr);
                            }
                            new_amount++;
                        }
                    }
                }
            }
        }
    }

    public static class Builder {
        int numPossibleLabels;
        int max_amount;
        float loss_factor = 0.1f;
        int near_count = 5;
        int sample_rate = 1;
        boolean debug = false;

        public Builder numPossibleLabels(int numPossibleLabels) {
            this.numPossibleLabels = numPossibleLabels;
            return this;
        }

        public Builder max_amount(int max_amount) {
            this.max_amount = max_amount;
            return this;
        }

        public Builder loss_factor(float loss_factor) {
            this.loss_factor = loss_factor;
            return this;
        }

        public Builder near_count(int near_count) {
            this.near_count = near_count;
            return this;
        }

        public Builder sample_rate(int sample_rate) {
            this.sample_rate = sample_rate;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public SmoteSamplingPreProcessor build() {
            return new SmoteSamplingPreProcessor(this.numPossibleLabels, this.max_amount, this.loss_factor, this.near_count, this.sample_rate, this.debug);
        }
    }

    class Distance implements Comparable<Distance> {
        private double distance;
        private INDArray xattr;
        private INDArray yattr;

        public Distance(double distance, INDArray xattr, INDArray yattr) {
            this.distance = distance;
            this.xattr = xattr;
            this.yattr = yattr;
        }

        @Override
        public int compareTo(Distance o) {
            return Double.compare(this.distance, o.distance);
        }
    }
}
