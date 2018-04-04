package com.hzz.test.DL4J.predict;
import com.hzz.model.Price;
import com.hzz.service.PriceService;
import com.hzz.utils.MathUtils;
import com.hzz.utils.NumberUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * huangzz
 */
public class BitconDataIterator implements DataSetIterator {

    //批数
    private int batchNum;
    //每组训练数据长度(DailyData的个数)
    private int exampleLength;
    //数据集
    private List<SecondData> dataList;
    //存放剩余数据组的index信息
    private List<Integer> dataRecord;
    //最大价格数
    private double maxNum=0;
    /**
     * 构造方法
     * */
    public BitconDataIterator(){
        dataRecord = new ArrayList<>();
    }

    /**
     * 加载数据并初始化
     * */
    public boolean loadData(List<SecondData> list,int start,int end, int batchNum, int exampleLength){
        this.batchNum = batchNum;
        this.exampleLength = exampleLength;
        //加载数据库指定symbol数据
        getDataList(list,start,end);
        //重置训练批次列表
        resetDataRecord();
        return true;
    }

    /**
     * 重置训练批次列表
     * */
    private void resetDataRecord(){
        dataRecord.clear();
        int total = dataList.size()/exampleLength+1;
        for( int i=0; i<total; i++ ){
            dataRecord.add(i * exampleLength);
        }
    }

    /**
     * 获取指定历史价格信息，记录最大价格
     * */
    public List<SecondData> getDataList(List<SecondData> list,int start,int end){
        dataList = new ArrayList<>();
        SecondData data;
        for(int i=start;i<=end;i++){
            data=list.get(i);
            dataList.add(data);
            if(MathUtils.compareDouble(maxNum,data.getPrice())<0)
                maxNum=data.getPrice();
        }
        return  dataList;
    }





    public double getMaxNum(){
        return maxNum;
    }

    public void reset(){
        resetDataRecord();
    }

    public boolean hasNext(){
        return dataRecord.size() > 0;
    }

    public DataSet next(){
        return next(batchNum);
    }

    /**
     * 获得接下来一次的训练数据集
     * */
    public DataSet next(int num){
        if( dataRecord.size() <= 0 ) {
            throw new NoSuchElementException();
        }
        int actualBatchSize = Math.min(num, dataRecord.size());
        int actualLength = Math.min(exampleLength,dataList.size()-dataRecord.get(0)-1);
        INDArray input = Nd4j.create(new int[]{actualBatchSize,1,actualLength}, 'f');
        INDArray label = Nd4j.create(new int[]{actualBatchSize,1,actualLength}, 'f');
        SecondData nextData = null,curData = null;
        //获取每批次的训练数据和标签数据
        for(int i=0;i<actualBatchSize;i++){
            int index = dataRecord.remove(0);
            int endIndex = Math.min(index+exampleLength,dataList.size()-1);
            curData = dataList.get(index);
            for(int j=index;j<endIndex;j++){
                //获取数据信息
                nextData = dataList.get(j+1);
                //构造训练向量
                int c = endIndex-j-1;
                input.putScalar(new int[]{i, 0, c}, curData.getPrice()/maxNum);
                curData = nextData;
            }
            if(dataRecord.size()<=0) {
                break;
            }
        }

        return new DataSet(input, label);
    }

    public int batch() {
        return batchNum;
    }

    public int cursor() {
        return totalExamples() - dataRecord.size();
    }

    public int numExamples() {
        return totalExamples();
    }

    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        return null;
    }

    public int totalExamples() {
        return (dataList.size()) / exampleLength;
    }

    public int inputColumns() {
        return dataList.size();
    }

    public int totalOutcomes() {
        return 1;
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
    public List<String> getLabels() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}