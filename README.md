# lstm-vehicle-flow-predict
利用DeepLearning4j框架提供的lstm神经网络实现对车流量预测

数据准备：创建MySQL数据库：jtt_new，执行sql脚本：resources\testdata.sql
模型训练阶段调用：
//模型训练阶段
  initPreTrain(inoutType, arr[0]);
  trainModel();
  testModel();
预测阶段：
//预测数据阶段
  initPrePredict(inoutType, arr[0]);
  predict();
