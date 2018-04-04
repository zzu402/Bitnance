package com.hzz.test.DL4J.predict;
import com.hzz.model.Price;
import com.hzz.service.PriceService;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/4
 */
public class PredictTest {
    private static PriceService priceService=new PriceService();
    private static final int IN_NUM = 1;
    private static final int OUT_NUM = 1;
    private static final int Epochs = 300;
    private static List<SecondData>list=readDataFromDB("BTCUSDT");
    public static MultiLayerNetwork getNetModel(int nIn, int nOut){
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(140)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .learningRate(0.0015)
                .list()
                .layer(0, new GravesLSTM.Builder().activation("tanh").nIn(1).nOut(10)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation("identity").nIn(10).nOut(1).build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(20));

        return net;
    }

    public static void train(MultiLayerNetwork net,BitconDataIterator iterator){
        //迭代训练
        for(int i=0;i<Epochs;i++) {
            DataSet dataSet = null;
            while (iterator.hasNext()) {
                dataSet = iterator.next();
                net.fit(dataSet);
            }
            iterator.reset();
            System.out.println();
            System.out.println("=================>完成第"+i+"次完整训练");

            System.out.println("预测结果：");
            INDArray output = net.rnnTimeStep(dataSet.getFeatures());
            System.out.print(list.get(1000).getPrice()+output.getDouble(0)*list.get(1000).getPrice() +"---"+list.get(1001).getPrice());
            System.out.println();
            net.rnnClearPreviousState();
        }
    }
    public static List<SecondData> readDataFromDB(String symbol){
        List<SecondData> dataList = new ArrayList<>();
        List<Price>prices=priceService.getPrice(symbol,0,0);
        SecondData data;
        for(int i =0;i<prices.size();i++){
            double price=Double.valueOf(prices.get(i).getPrice());
            data=new SecondData();
            data.setPrice(price);
            dataList.add(data);
        }
        return dataList;
    }

    public static void main(String[] args) {
        int batchSize = 1;
        int exampleLength = 50;
        //初始化深度神经网络
        BitconDataIterator iterator = new BitconDataIterator();

        iterator.loadData(list,0,1000,batchSize,exampleLength);
        MultiLayerNetwork net = getNetModel(IN_NUM,OUT_NUM);
        train(net, iterator);
    }







}
