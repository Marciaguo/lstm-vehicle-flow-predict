package com.udgrp.main;

import com.udgrp.network.LstmNetworks;
import com.udgrp.bean.VehicleFlow;
import com.udgrp.iterator.VehicleFlowSetIterator;
import com.udgrp.utils.DBUtil;
import com.udgrp.utils.DateUtil;
import com.udgrp.utils.PlotUtil;
import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 实现循环训练
 *
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class VehicleFlowPrediction {

    private static final Logger log = LoggerFactory.getLogger(VehicleFlowPrediction.class);

    private static File modelPath;
    private static MultiLayerNetwork net;
    private static VehicleFlowSetIterator iterator;

    private static int epochs = 1000; // training epochs
    private static int miniBatchSize = 64;// mini-batch size
    private static int exampleLength = 24; // time series length, assume 22 working days per month
    private static int predictLength = 24; //  default 1, say, one day ahead prediction
    private static int printIterations = 100; // frequency with which to print scores

    public static void main(String[] args) throws IOException {
        String[] inout = {"1"};
        List<String[]> list = DBUtil.getStationIdLists();
        for (String inoutType : inout) {
            for (String[] arr : list) {
                //模型训练阶段
                initPreTrain(inoutType, arr[0]);
                trainModel();
                testModel();

                //预测数据阶段
                //initPrePredict(inoutType, arr[0]);
                //predict();
            }
        }
    }

    /**
     * 训练
     *
     * @throws IOException
     */
    private static void trainModel() throws IOException {
        log.info("Training...");
        for (int i = 0; i < epochs; i++) {
            while (iterator.hasNext()) {
                net.fit(iterator.next()); // fit model using mini-batch data
            }
            iterator.reset(); // reset iterator
            net.rnnClearPreviousState(); // clear previous state
        }
        log.info("Saving model...");
        ModelSerializer.writeModel(net, modelPath, true);
    }

    /**
     * 测试
     */
    private static void testModel() throws IOException {
        log.info("Load model...");
        net = ModelSerializer.restoreMultiLayerNetwork(modelPath);

        List<Pair<INDArray, INDArray>> testData = iterator.getTestDataSet();
        double max = iterator.getMaxNum();
        double min = iterator.getMinNum();

        double[] predicts = new double[testData.size()];
        double[] actuals = new double[testData.size()];
        log.info("Testing...");
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getDouble(exampleLength - predictLength) * (max - min) + min;
            actuals[i] = testData.get(i).getValue().getDouble(0);
        }
        log.info("Print out Predictions and Actual Values...");
        log.info("Predict,Actual");
        for (int i = 0; i < predicts.length; i++) {
            log.info(predicts[i] + "," + actuals[i]);
        }
        log.info("Plot...");
        PlotUtil.plot(predicts, actuals, String.valueOf("flow"));
    }

    /**
     * 预测
     */
    private static void predict() throws IOException {
        log.info("Load model...");
        net = ModelSerializer.restoreMultiLayerNetwork(modelPath);

        List<VehicleFlow> lastDayDataList = iterator.getPredictDataList();
        List<Pair<INDArray, INDArray>> predictData = iterator.getPredict();
        double max = iterator.getMaxNum();
        double min = iterator.getMinNum();

        INDArray arrs = net.rnnTimeStep(predictData.get(0).getKey());
        INDArray arr = arrs.getColumn(exampleLength - predictLength);

        if (arr.length() != 24) {
            return;
        }
        // System.out.println(arrs);
        // System.out.println(arr);
        double[] predicts = new double[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            predicts[i] = arr.getDouble(i) * (max - min) + min;
            VehicleFlow flow = lastDayDataList.get(i);
            flow.setDate(DateUtil.getAfterDay(flow.getDate()));
            flow.setFlow(predicts[i]);
            DBUtil.insert(flow);
            log.info(predicts[i] + "");
        }
    }

    /**
     * 初始化
     *
     * @param stationId
     * @return
     */
    private static void initPreTrain(String inoutType, String stationId) {
        log.info("Create dataSet iterator...");
        iterator = new VehicleFlowSetIterator(inoutType, stationId, miniBatchSize, exampleLength, predictLength);

        log.info("Create model path...");
        String path = "src/main/resources/model/" + stationId + "_" + inoutType + "_model.zip";
        modelPath = new File(path);

        log.info("Build lstm networks...");
        net = LstmNetworks.buildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        net.setListeners(new ScoreIterationListener(printIterations));
    }

    /**
     * 初始化
     *
     * @param stationId
     * @return
     */
    private static void initPrePredict(String inoutType, String stationId) {
        log.info("Create model path...");
        String path = "src/main/resources/model/" + stationId + "_" + inoutType + "_model.zip";
        modelPath = new File(path);

        log.info("Create dataSet iterator...");
        iterator = new VehicleFlowSetIterator(inoutType, stationId, exampleLength, predictLength);

        log.info("Build lstm networks...");
        net = LstmNetworks.buildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        net.setListeners(new ScoreIterationListener(printIterations));
    }
}
