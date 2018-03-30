package com.hzz.utils;

import java.math.BigDecimal;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/30
 */
public class MathUtils {




    public static Integer compareDouble(Double d1,Double d2){
        BigDecimal data1=new BigDecimal(d1);
        BigDecimal data2=new BigDecimal(d2);
        return  data1.compareTo(data2);
    }



    /*
        找到一组数据的最低点位置
     */
    public static Integer findMinPosition(Double[]doubles){
        if(doubles==null||doubles.length<=0)
            return -1;
        int position=-1;
        double min=100000000000.0;
        for(int i=0;i<doubles.length;i++){
            if(compareDouble(min,doubles[i])>0){
                position=i;
                min=doubles[i];
            }

        }
        return position;
    }

    /*
    找到一组数据的最高点位置
 */
    public static Integer findMaxPosition(Double[]doubles){
        if(doubles==null||doubles.length<=0)
            return -1;
        int position=-1;
        double max=-100000000000.0;
        for(int i=0;i<doubles.length;i++){
            if(compareDouble(max,doubles[i])<0){
                position=i;
                max=doubles[i];
            }

        }
        return position;
    }

    public static void main(String[]args){
        Double[]doubles=new Double[]{1.0,2.0,-1.0,3.0,4.0,5.0};
        System.out.println(getKsub(doubles));
        System.out.println(findMaxPosition(doubles));
        System.out.println(findMiddle(doubles));
        System.out.println(findMiddlePosition(doubles));
        System.out.println(findMinPosition(doubles));

    }


    /*
        计算一组数据的斜率（因为x固定，所以，直接求y的差值）
     */
    public static Double[] getKsub(Double[]doubles){
        if(doubles==null||doubles.length<=0)
            return null;
        Double[]K=new Double[doubles.length-1];
        for(int i=0;i<doubles.length-1;i++){
            K[i]=doubles[i+1]-doubles[i];
        }
        return  K;
    }

    public  static Integer findMiddlePosition(Double[] doubles){
        if(doubles==null||doubles.length<1)
            return -1;
        Double doubleNum=findMiddle(doubles);
        Integer position=-1;
        for(int i=0;i<doubles.length;i++){
            if (compareDouble(doubleNum,doubles[i])==0){
                position=i;
            }
        }
        return  position;
    }



    /*
        找出一组数的中间值
     */
    public static Double findMiddle(Double[] doubles){
        if(doubles==null||doubles.length<=0)
            return -1.0;
        Double[]newDoubles=doubles.clone();
        quickSort(newDoubles);
        if(doubles.length%2==0)
            return newDoubles[doubles.length/2-1];
        else
            return newDoubles[doubles.length/2];
    }

    /*
        快排
     */
    public static void quickSort(Double[]doubles){
        if(doubles.length>0) {
            quickSort(doubles, 0 , doubles.length-1);
        }
    }
    private static void quickSort(Double[] a, int low, int high) {
        //1,找到递归算法的出口
        if( low > high) {
            return;
        }
        //2, 存
        int i = low;
        int j = high;
        //3,key
        Double key = a[ low ];
        //4，完成一趟排序
        while( i< j) {
            //4.1 ，从右往左找到第一个小于key的数
            while(i<j && compareDouble(a[j],key)>0){
                j--;
            }
            // 4.2 从左往右找到第一个大于key的数
            while( i<j && compareDouble(a[i],key)<=0) {
                i++;
            }
            //4.3 交换
            if(i<j) {
                Double p = a[i];
                a[i] = a[j];
                a[j] = p;
            }
        }
        // 4.4，调整key的位置
        Double p = a[i];
        a[i] = a[low];
        a[low] = p;
        //5, 对key左边的数快排
        quickSort(a, low, i-1 );
        //6, 对key右边的数快排
        quickSort(a, i+1, high);
    }


}
