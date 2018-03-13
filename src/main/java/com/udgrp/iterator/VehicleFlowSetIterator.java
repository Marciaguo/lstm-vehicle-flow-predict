package com.udgrp.iterator;

import com.udgrp.bean.VehicleFlow;
import com.udgrp.utils.DBUtil;
import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class VehicleFlowSetIterator implements DataSetIterator {

    private final int VECTOR_SIZE = 1; // number of features for a stock data
    private int miniBatchSize; // mini-batch size
    private int exampleLength; // default 22, say, 22 working days per month
    private int predictLength; // default 1, say, one day ahead prediction

    /**
     * minimal values of each feature in stock dataset
     */
    private double[] minArray = new double[VECTOR_SIZE];
    /**
     * maximal values of each feature in stock dataset
     */
    private double[] maxArray = new double[VECTOR_SIZE];

    /**
     * mini-batch offset
     */
    private LinkedList<Integer> exampleStartOffsets = new LinkedList<>();

    /**
     * stock dataset for training
     */
    private List<VehicleFlow> train;
    /**
     * adjusted stock dataset for testing
     */
    private List<Pair<INDArray, INDArray>> test;

    private List<VehicleFlow> predictDataList;

    /**
     * adjusted stock dataset for testing
     */
    private List<Pair<INDArray, INDArray>> predict;

    public VehicleFlowSetIterator(String inoutType, String stationId, int miniBatchSize, int exampleLength, int predictLength) {
        List<VehicleFlow> trainDataList = readDataFromDB(inoutType, "train", stationId);
        List<VehicleFlow> testDataList = readDataFromDB(inoutType, "test", stationId);
        this.miniBatchSize = miniBatchSize;
        this.exampleLength = exampleLength;
        this.predictLength = predictLength;
        train = trainDataList;
        test = generateTestDataSet(testDataList);
        initializeOffsets();
    }

    public VehicleFlowSetIterator(String inoutType, String stationId, int exampleLength, int predictLength) {
        predictDataList = readDataFromDB(inoutType, "predict", stationId);
        this.exampleLength = exampleLength;
        this.predictLength = predictLength;
        predict = generateLastDayDataSet(predictDataList);
    }

    /**
     * initialize the mini-batch offsets
     */
    private void initializeOffsets() {
        exampleStartOffsets.clear();
        int window = exampleLength + predictLength;
        for (int i = 0; i < train.size() - window; i++) {
            exampleStartOffsets.add(i);
        }
    }

    public List<Pair<INDArray, INDArray>> getPredict() {
        return predict;
    }

    public List<VehicleFlow> getPredictDataList() {
        return predictDataList;
    }

    public List<Pair<INDArray, INDArray>> getTestDataSet() {
        return test;
    }

    public double[] getMaxArray() {
        return maxArray;
    }

    public double[] getMinArray() {
        return minArray;
    }

    public double getMaxNum() {
        return maxArray[0];
    }

    public double getMinNum() {
        return minArray[0];
    }

    @Override
    public DataSet next(int num) {
        if (exampleStartOffsets.size() == 0) {
            throw new NoSuchElementException();
        }
        int actualMiniBatchSize = Math.min(num, exampleStartOffsets.size());
        INDArray input = Nd4j.create(new int[]{actualMiniBatchSize, VECTOR_SIZE, exampleLength}, 'f');
        INDArray label = Nd4j.create(new int[]{actualMiniBatchSize, predictLength, exampleLength}, 'f');

        for (int index = 0; index < actualMiniBatchSize; index++) {
            int startIdx = exampleStartOffsets.removeFirst();
            int endIdx = startIdx + exampleLength;
            VehicleFlow curData = train.get(startIdx);
            VehicleFlow nextData;
            for (int i = startIdx; i < endIdx; i++) {
                int c = i - startIdx;
                input.putScalar(new int[]{index, 0, c}, (curData.getFlow() - minArray[0]) / (maxArray[0] - minArray[0]));
                nextData = train.get(i + 1);
                label.putScalar(new int[]{index, 0, c}, feedLabel(nextData));
                curData = nextData;
            }
            if (exampleStartOffsets.size() == 0) break;
        }
        return new DataSet(input, label);
    }

    private double feedLabel(VehicleFlow data) {
        double value = (data.getFlow() - minArray[0]) / (maxArray[0] - minArray[0]);
        return value;
    }

    @Override
    public int totalExamples() {
        return train.size() - exampleLength - predictLength;
    }

    @Override
    public int inputColumns() {
        return VECTOR_SIZE;
    }

    @Override
    public int totalOutcomes() {
        return predictLength;
    }

    @Override
    public boolean resetSupported() {
        return false;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void reset() {
        initializeOffsets();
    }

    @Override
    public int batch() {
        return miniBatchSize;
    }

    @Override
    public int cursor() {
        return totalExamples() - exampleStartOffsets.size();
    }

    @Override
    public int numExamples() {
        return totalExamples();
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor dataSetPreProcessor) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<String> getLabels() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean hasNext() {
        return exampleStartOffsets.size() > 0;
    }

    @Override
    public DataSet next() {
        return next(miniBatchSize);
    }

    /**
     * 测试用
     *
     * @param vehicleFlowList
     * @return
     */
    private List<Pair<INDArray, INDArray>> generateTestDataSet(List<VehicleFlow> vehicleFlowList) {
        int window = exampleLength + predictLength;
        List<Pair<INDArray, INDArray>> test = new ArrayList<>();
        for (int i = 0; i < vehicleFlowList.size() - window; i++) {
            INDArray input = Nd4j.create(new int[]{exampleLength, VECTOR_SIZE}, 'f');
            for (int j = i; j < i + exampleLength; j++) {
                VehicleFlow flow = vehicleFlowList.get(j);
                input.putScalar(new int[]{j - i, 0}, (flow.getFlow() - minArray[0]) / (maxArray[0] - minArray[0]));
            }
            VehicleFlow flow = vehicleFlowList.get(i + exampleLength);
            INDArray label;
            label = Nd4j.create(new int[]{1}, 'f');
            label.putScalar(new int[]{0}, flow.getFlow());
            test.add(new Pair<>(input, label));
        }
        return test;
    }

    /**
     * 预测用
     *
     * @param vehicleFlowList
     * @return
     */
    public List<Pair<INDArray, INDArray>> generateLastDayDataSet(List<VehicleFlow> vehicleFlowList) {
        List<Pair<INDArray, INDArray>> test = new ArrayList<>();
        System.out.println(vehicleFlowList.size());
        for (int i = 0; i < 1; i++) {
            INDArray input = Nd4j.create(new int[]{exampleLength, VECTOR_SIZE}, 'f');
            for (int j = i; j < vehicleFlowList.size(); j++) {
                VehicleFlow flow = vehicleFlowList.get(j);
                input.putScalar(new int[]{j - i, 0}, (flow.getFlow() - minArray[0]) / (maxArray[0] - minArray[0]));
            }
            VehicleFlow flow = vehicleFlowList.get(i);
            INDArray label = Nd4j.create(new int[]{1}, 'f');
            label.putScalar(new int[]{0}, flow.getFlow());
            test.add(new Pair<>(input, label));
        }
        return test;
    }

    private List<VehicleFlow> readDataFromDB(String inoutType, String type, String stationId) {
        List<VehicleFlow> vehicleFlow = new ArrayList<>();
        for (int i = 0; i < maxArray.length; i++) { // initialize max and min arrays
            maxArray[i] = Double.MIN_VALUE;
            minArray[i] = Double.MAX_VALUE;
        }
        List<String[]> list = DBUtil.readData(inoutType, type, stationId); // load all elements in a list

        for (String[] arr : list) {
            double[] nums = new double[VECTOR_SIZE];
            String station_Id = arr[0];
            String date = arr[1];
            int hour = Integer.valueOf(arr[2]);
            nums[0] = Double.valueOf(arr[3]);

            if (nums[0] > maxArray[0]) maxArray[0] = nums[0];
            if (nums[0] < minArray[0]) minArray[0] = nums[0];
            vehicleFlow.add(new VehicleFlow(inoutType,station_Id, date, hour, nums[0]));
        }
        return vehicleFlow;
    }
}
