package com.hzz.main;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzz.model.*;
import com.hzz.utils.HmacUtils;
import com.hzz.utils.NumberUtils;
import com.hzz.utils.SslUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class Api {
    //global key
    private static String secret_key = "";  //账户私密key，用于个人签名，所有账户相关敏感查询或操作都需要将参数加密做一个签名一起附带
    private static String api_key = ""; //api接口key，用于发送请求时添加到http(s)头进行简单验证，大部分需要
    private Long currentTime = 0L;
    private Integer recvWindow=150000;
    private Integer limit=500;
    private Double rateFee =  0.001; //手续费比率
    private static Logger logger= LoggerFactory.getLogger(Api.class);

    public static void main(String[] args) {
        Api api = new Api();
//        System.out.println("------账户信息--------");
//        System.out.println(api.getAccountInfo());
//        System.out.println("------账户金币信息--------");
//        System.out.println(api.getAllMoneyFree());
//        System.out.println("------账户某币信息--------");
//        System.out.println(api.getMoneyFree("BTC"));
//        System.out.println("------币市价格信息--------");
//        System.out.println(api.getMoneyPrice(""));
        System.out.println("------币市某币价格信息--------");
        System.out.println(api.getMoneyPrice("BTCUSDT"));
//        System.out.println("------汇兑信息--------");
//        System.out.println(api.getExchangeInfo());
//        System.out.println("------出售--------");
//        System.out.println(api.sell("TRXBTC",1842.0,"0.00000416"));
//        System.out.println("------买进--------");
//        System.out.println(api.buy("TRXBTC",1843.0,"0.00000407"));
//        System.out.println("------打开待处理订单--------");
//        System.out.println(api.openMyOrders("TRXBTC"));
        System.out.println("------获取我的成交交易--------");
        System.out.println(api.getMyTrades("TRXBTC","",10));
        System.out.println("------获取我的订单--------");
        System.out.println(api.getMyOrders("TRXBTC","",10));
        System.out.println("------获取历史成交交易--------");
        System.out.println(api.getHistoricalTrades("TRXBTC",10,""));
        System.out.println("------获取最近成交交易--------");
        System.out.println(api.getRecentTrades("TRXBTC",10));
    }

    public static void setSecret_key(String secret_key) {
        Api.secret_key = secret_key;
    }

    public static void setApi_key(String api_key) {
        Api.api_key = api_key;
    }

    public static String getSecret_key() {
        return secret_key;
    }

    public static String getApi_key() {
        return api_key;
    }

    private Map jsonStr2Map(String jsonStr){
        System.out.println(jsonStr);
        if(jsonStr==null)
            return null;
        Gson gson = new Gson();
        Map<String, Object> data = new HashMap<String, Object>();
        data = gson.fromJson(jsonStr, data.getClass());
        return  data;
    }
    private Map createRQuery(String method,String query_string ,Boolean b_add_time ){
        Map map = new HashMap();
        map.put("method", method);
        map.put("query_string", query_string);
        map.put("b_add_time", b_add_time);
        return map;
    }
    private String requestApi(String url, Map rQuery, Boolean bSign) {
        logger.info("request API start...");
        try {
            SslUtils.ignoreSsl();
            String queryString = (String) rQuery.get("query_string");
            Boolean addTimeFlag = (Boolean) rQuery.get("b_add_time");
            if (addTimeFlag) {
                currentTime = System.currentTimeMillis();
                queryString += "&timestamp=" + currentTime;
            }
            if (bSign) {
                String signature = HmacUtils.getSignature(queryString, secret_key);
                queryString += "&signature=" + signature;
            }
            String queryString2 = (String) rQuery.get("query_string");
            String method = (String) rQuery.get("method");
            Boolean isGetMethod = false;
            Boolean isPostMethod = false;
            if (method.equals("GET") ) {
                isGetMethod = true;
                if(!queryString2.equals(""))
                    url += "?" + queryString;
            }
            if(method.equals("POST")){
                isPostMethod=true;
            }
            Map<String,String> header=new HashMap<String, String>();
            header.put("X-MBX-APIKEY",api_key);
            Connection connection = Jsoup.connect(url).headers(header).ignoreContentType(true).ignoreHttpErrors(true).timeout(30000);
            if (!isGetMethod) {
                if(queryString!=null&&!queryString.equals("")) {
                    String[] querys=queryString.split("&");
                    for(int i=0;i<querys.length;i++){
                        String[] datas=querys[i].split("=");
                        connection.data(datas[0],datas[1]);
                    }
                }
                if(isPostMethod)
                    connection.method(Connection.Method.POST);
                else
                    connection.method(Connection.Method.DELETE);
            } else {
                connection.method(Connection.Method.GET);
            }
            String body=connection.execute().body();
            logger.info("request API end...");
            return  body;
        } catch (Exception e) {
            logger.error("request API error...",e);
            return null;
        }
    }
    //获取账户信息
    public Account getAccountInfo() {
        logger.info("get account start...");
        String url_api = "https://api.binance.com/api/v3/account";
        String query_string = "recvWindow="+recvWindow;
        String result =requestApi(url_api, createRQuery("GET",query_string,true), true);
        try {
            Gson gson = new Gson();
            if(result!=null) {
                Account account = gson.fromJson(result, Account.class);
                logger.info("get account end...");
                return account;
            }
        }catch (Exception e){
            logger.error("get account error...",e);
             return null;
        }
        logger.error("no get any account info...");
        return null;
    }
    //获取所有闲置钱币
    public Map<String,Balance> getAllMoneyFree(){
        logger.info("get all free money start ...");
        Map<String,Balance>newBalances=new HashMap<String, Balance>();
        Account account=getAccountInfo();
        if(account!=null){
           List<Balance>oldBalances=account.getBalances();
           if(oldBalances==null||oldBalances.isEmpty()){
               logger.info("no get any free money ...");
               return null;
           }
           for(int i=0;i<oldBalances.size();i++){
               Balance balance=oldBalances.get(i);
               if(NumberUtils.isEquals(NumberUtils.valueOf(balance.getFree()),0.0)){
                   newBalances.put(balance.getAsset(),balance);
               }
           }
           logger.info("get all free money end...");
           return  newBalances;
        }
        logger.info("no get any free money ...");
        return null;
    }
   //获取某种币的余额
    public Balance getMoneyFree(String name){
        logger.info("get free money  start...");
        Map<String,Balance> balanceMap=getAllMoneyFree();
        if(balanceMap==null) {
            logger.info("get free money failure...");
            return null;
        }
        logger.info("get free money end...");
        return balanceMap.get(name);
    }
    /**
     * 获取目前数字币价格信息,当symbol为""，返回全部
     * @param symbol
     * @return
     */
    public List<Price> getMoneyPrice(String symbol){
        logger.info("get money price start ...");
        String url_api="https://api.binance.com/api/v3/ticker/price";
        String query_string="";
        if(!symbol.equals("")){
            query_string+="symbol="+symbol;
        }
        try {
            String result=requestApi(url_api,createRQuery("GET",query_string,false),false);
            List<Price>priceList=null;
            Gson gson = new Gson();
            if(result!=null) {
                if(symbol.equals(""))
                    priceList=gson.fromJson(result, new TypeToken<List<Price>>() {
                    }.getType());
                else {
                    Price price=gson.fromJson(result,Price.class);
                    priceList=new ArrayList<Price>();
                    priceList.add(price);
                }
            }
            logger.info("get money price end ...");
            return  priceList;
        }catch (Exception e){
            logger.error("get money price error ...",e);
            return null;
        }

    }
    //尝试请求交易
    public String tryRequestOrder(String query_string,String method){
        logger.info("try to request order ...");
        String url_api="https://api.binance.com/api/v3/order";
        String result =requestApi(url_api, createRQuery(method,query_string,true), true);
        return  result;
    }
    //检查交易状态
    public  Map checkOrderStatus(String symbol,String orderId,String origClientOrderId){
       String query_string="symbol="+symbol+"&orderId="+orderId+"&origClientOrderId="+origClientOrderId+
               "&recvWindow="+recvWindow;
       String result=tryRequestOrder(query_string,"GET");
        return  jsonStr2Map(result);
    }
    public Map  cancelOrder(String symbol,String orderId,String origClientOrderId){
        String query_string="symbol="+symbol+"&orderId="+orderId+"&origClientOrderId="+origClientOrderId+
                "&recvWindow="+recvWindow;
        String result=tryRequestOrder(query_string,"DELETE");
        return  jsonStr2Map(result);
    }
    //打开当前待处理订单
    public Map openMyOrders(String symbol){
        String query_string ="recvWindow="+recvWindow;
        if(!symbol.equals("")){
            query_string+="&symbol="+symbol;
        }
        String url_api="https://api.binance.com/api/v3/openOrders";
        String result=requestApi(url_api,createRQuery("GET",query_string,true),true);
        return  jsonStr2Map(result);
    }
    /**
     *
     * @param symbol  币名 xxxBTC 如ETHBTC
     * @param quantity 数量
     * @param price 价格
     * @return
     */
    public SellOrBuyInfo buy(String symbol,Double quantity,String price){
        logger.info("try to buy start ...");
        String query_string="side=BUY&symbol="+symbol+"&quantity="+quantity+"&price="+price +
                "&type=LIMIT&timeInForce=GTC&recvWindow="+recvWindow;
        try {
            String result=tryRequestOrder(query_string,"POST");
            Gson gson = new Gson();
            if(result!=null) {
                SellOrBuyInfo sellOrBuyInfo = gson.fromJson(result, SellOrBuyInfo.class);
                logger.info("try to buy end ...");
                return sellOrBuyInfo;
            }
        }catch (Exception e){
            logger.error("try to buy error ...",e);
            return null;
        }
        logger.info("try to buy failure...");
        return null;

    }
    /**
     *
     * @param symbol  币名 xxxBTC 如ETHBTC
     * @param quantity 数量
     * @param price 价格
     * @return
     */
    public SellOrBuyInfo sell(String symbol, Double quantity, String price){
        logger.info("try to sell start ...");
        String query_string="symbol="+symbol+"&side=SELL&type=LIMIT&timeInForce=GTC&quantity="+quantity+"&price="+price +
                "&recvWindow="+recvWindow;
        String result=tryRequestOrder(query_string,"POST");
        try {
            Gson gson = new Gson();
            if(result!=null) {
                SellOrBuyInfo sellOrBuyInfo = gson.fromJson(result, SellOrBuyInfo.class);
                logger.info("try to sell end ...");
                return sellOrBuyInfo;
            }
        }catch (Exception e){
            logger.error("try to sell error...",e);
            return null;
        }
        logger.info("try to sell failure...");
        return null;
    }
    //获取服务器时间戳（毫秒）
    public Map getServerTime(){
        String url_api="https://api.binance.com/api/v1/time";
        String result=requestApi(url_api,createRQuery("GET","",false),false);
        return  jsonStr2Map(result);
    }
    //获取汇兑信息
    public ExchangeInfo getExchangeInfo(){
        String url_api="https://api.binance.com/api/v1/exchangeInfo";
        String result=requestApi(url_api,createRQuery("GET","",false),false);
        Gson gson = new Gson();
        if(result!=null) {
            ExchangeInfo exchangeInfo = gson.fromJson(result, ExchangeInfo.class);
            return exchangeInfo;
        }
        return null;
    }
    //获取（预定？）订单
    public Map getOrderBook(String symbol,int limit){
        String url_api="https://api.binance.com/api/v1/depth";
        String quert_string="symbol="+symbol+"&limit="+limit;
        String result=requestApi(url_api,createRQuery("GET",quert_string,false),false);
        return  jsonStr2Map(result);
    }
    //获取历史订单
    public List<Order> getMyOrders(String symbol,String orderId,Integer limit){
        logger.info("try to get my order start ...");

        String query_string="symbol="+symbol+"&recvWindow="+recvWindow+"&limit="+limit;
        if(!orderId.equals("")){
            query_string+="&orderId="+orderId;
        }
        try {
            String url_api="https://api.binance.com/api/v3/allOrders";
            String result=requestApi(url_api,createRQuery("GET",query_string,true),true);
            Gson gson = new Gson();
            List<Order>orders=null;
            if(result!=null) {
                orders=gson.fromJson(result, new TypeToken<List<Order>>() {
                }.getType());
                logger.info("try to get my order end ...");
                return  orders;
            }
        }catch (Exception e){
            logger.error("try to get my order error ...",e);
            return null;
        }
        logger.info("try to get my order failure ...");
        return null;
    }
    //获取我的历史交易
    public List<MyTrade> getMyTrades(String symbol,String formId,Integer limit){
        logger.info("try to get my trade start ...");
        String query_string="symbol="+symbol+"&recvWindow="+recvWindow+"&limit="+limit;
        if(!formId.equals("")){
            query_string+="&formId="+formId;
        }
        try {
            String url_api="https://api.binance.com/api/v3/myTrades";
            String result=requestApi(url_api,createRQuery("GET",query_string,true),true);
            Gson gson = new Gson();
            List<MyTrade>trades=null;
            if(result!=null) {
                trades=gson.fromJson(result, new TypeToken<List<MyTrade>>() {
                }.getType());
                logger.info("try to get my trade end ...");
                return  trades;
            }
        }catch (Exception e){
            logger.error("try to get my trade error ...",e);
            return  null;
        }
        logger.info("try to get my trade failure ...");
        return null;
    }
    //获取最新成交交易
    public List<Trade> getRecentTrades(String symbol,Integer limit){
        logger.info("try to get my recent trade start ...");
        String query_string="symbol="+symbol+"&limit="+limit;
        try {
            String url_api="https://api.binance.com/api/v1/trades";
            String result=requestApi(url_api,createRQuery("GET",query_string,false),false);
            Gson gson = new Gson();
            List<Trade>trades=null;
            if(result!=null) {
                trades=gson.fromJson(result, new TypeToken<List<Trade>>() {
                }.getType());
                logger.info("try to get my recent trade end ...");
                return  trades;
            }
        }catch (Exception e){
            logger.error("try to get my recent trade error ...",e);
            return null;
        }
        logger.info("try to get my recent trade failure ...");
        return null;
    }

    //获取历史成交交易
    public List<Trade> getHistoricalTrades(String symbol, Integer limit, String formId){
        logger.info("try to get  historical trade start ...");
        String query_string="symbol="+symbol+"&limit="+limit;
        if(!formId.equals("")){
            query_string+="&formId="+formId;
        }
        try {
            String url_api="https://api.binance.com/api/v1/historicalTrades";
            String result=requestApi(url_api,createRQuery("GET",query_string,false),false);
            Gson gson = new Gson();
            List<Trade>trades=null;
            if(result!=null) {
                trades=gson.fromJson(result, new TypeToken<List<Trade>>() {
                }.getType());
                logger.info("try to get  historical trade end ...");
                return  trades;
            }
        }catch (Exception e){
            logger.error("try to get  historical trade error ...",e);
            return null;
        }
        logger.info("try to get  historical trade failure ...");
        return null;
    }


}
