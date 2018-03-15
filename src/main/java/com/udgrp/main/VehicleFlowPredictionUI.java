package com.udgrp.main;

import com.udgrp.network.LstmNetworks;
import com.udgrp.iterator.VehicleFlowSetIterator;
import com.udgrp.utils.FileUtil;
import com.udgrp.utils.PlotUtil;
import javafx.util.Pair;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 可视化训练模型
 * http://localhost:9000/
 *
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/3/12
 */
public class VehicleFlowPredictionUI {
    private static final Logger log = LoggerFactory.getLogger(VehicleFlowPrediction.class);

    private static File modelPath;
    private static UIServer uiServer;
    private static MultiLayerNetwork net;
    private static VehicleFlowSetIterator iterator;

    private static int epochs = 180; // training epochs
    private static int miniBatchSize = 64;// mini-batch size
    private static int exampleLength = 24; // time series length, assume 22 working days per month
    private static int predictLength = 24; // default 1, say, one day ahead prediction
    private static int listenerFrequency = 1;  //

    private static String inoutType = "1";  //
    private static String stationId = "4412-90-29";  //

    public static void main(String[] args) throws IOException {
        uiServer = UIServer.getInstance();
        initialize(inoutType, stationId);
        trainModel();
        testModel();
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
     * 初始化
     *
     * @param stationId
     */
    private static void initialize(String inoutType, String stationId) {
        log.info("Create dataSet iterator...");
        iterator = new VehicleFlowSetIterator(inoutType, stationId, miniBatchSize, exampleLength, predictLength);

        log.info("Create model path...");
        String path = "src/main/resources/model/" + stationId + "_" + inoutType + "_model.zip";
        modelPath = FileUtil.createFile(path);

        log.info("Build lstm networks...");
        net = LstmNetworks.buildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        StatsStorage statsStorage = new InMemoryStatsStorage();
        net.setListeners(new StatsListener(statsStorage, listenerFrequency));
        uiServer.attach(statsStorage);
    }

}
