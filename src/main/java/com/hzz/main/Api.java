package com.hzz.main;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzz.model.Account;
import com.hzz.model.Balance;
import com.hzz.model.Price;
import com.hzz.utils.HmacUtils;
import com.hzz.utils.NumberUtils;
import com.hzz.utils.SslUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
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
    private String secret_key = "";  //账户私密key，用于个人签名，所有账户相关敏感查询或操作都需要将参数加密做一个签名一起附带
    private String api_key = ""; //api接口key，用于发送请求时添加到http(s)头进行简单验证，大部分需要
    private Long currentTime = 0L;

    public static void main(String[] args) {
        Api api = new Api();
        System.out.println(api.getExchangeInfo());
    }

    private String requestApi(String url, Map rQuery, boolean bSign) {
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
            boolean isGetMethod = false;
            boolean isPostMethod = false;
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
            Connection connection = Jsoup.connect(url).headers(header).ignoreContentType(true).ignoreHttpErrors(true).timeout(10000);
            if (!isGetMethod) {
                if(queryString!=null&&!queryString.equals(""))
                    connection.data(queryString);
                if(isPostMethod)
                    connection.method(Connection.Method.POST);
                else
                    connection.method(Connection.Method.DELETE);
            } else {
                connection.method(Connection.Method.GET);
            }
            return   connection.execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取账户信息
    public Account getAccountInfo() {
        String url_api = "https://api.binance.com/api/v3/account";
        String query_string = "recvWindow=500000";
        Map map = new HashMap();
        map.put("method", "GET");
        map.put("query_string", query_string);
        map.put("b_add_time", true);
        String result =requestApi(url_api, map, true);
        Gson gson = new Gson();
        if(result!=null) {
            Account account = gson.fromJson(result, Account.class);
            return account;
        }
        return null;
    }

    //获取所有闲置钱币
    public Map<String,Balance> getAllMoneyFree(){
        Map<String,Balance>newBalances=new HashMap<String, Balance>();
        Account account=getAccountInfo();
        if(account!=null){
           List<Balance>oldBalances=account.getBalances();
           for(int i=0;i<oldBalances.size();i++){
               Balance balance=oldBalances.get(i);
               if(NumberUtils.isEquals(balance.getFree(),0.0)){
                   newBalances.put(balance.getAsset(),balance);
               }
           }
           return  newBalances;
        }
        return null;
    }

   //获取某种币的余额
    public Balance getMoneyFree(String name){
        Double free=0.0;
        Map<String,Balance> balanceMap=getAllMoneyFree();
        return balanceMap.get(name);
    }

    /**
     * 获取目前数字币价格信息,当symbol为""，返回全部
     * @param symbol
     * @return
     */
    public List<Price> getMoneyPrice(String symbol){
        String url_api="https://api.binance.com/api/v3/ticker/price";
        String query_string="";
        if(!symbol.equals("")){
            query_string+="symbol="+symbol;
        }
        Map map = new HashMap();
        map.put("method", "GET");
        map.put("query_string", query_string);
        map.put("b_add_time", false);
        String result=requestApi(url_api,map,false);
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
        return  priceList;
    }

    //尝试请求交易
    public String tryRequestOrder(String query_string,String method){
        String url_api="https://api.binance.com/api/v3/order";
        Map map = new HashMap();
        map.put("method", method);
        map.put("query_string", query_string);
        map.put("b_add_time", true);
        String result =requestApi(url_api, map, true);
        return  result;
    }

    //检查交易状态
    public  Map checkOrderStrtus(String symbol,String orderId,String origClientOrderId){
       String query_string="symbol="+symbol+"&orderId="+orderId+"&origClientOrderId="+origClientOrderId+
               "&recvWindow=500000";
       String result=tryRequestOrder(query_string,"GET");
        return  jsonStr2Map(result);
    }

    public Map  cancelOrder(String symbol,String orderId,String origClientOrderId){
        String query_string="symbol="+symbol+"&orderId="+orderId+"&origClientOrderId="+origClientOrderId+
                "&recvWindow=500000";
        String result=tryRequestOrder(query_string,"DELETE");
        return  jsonStr2Map(result);
    }

    public Map openOrders(String symbol){
        String query_string ="recvWindow=500000";
        if(!symbol.equals("")){
            query_string+="&symbol="+symbol;
        }
        String url_api="https://api.binance.com/api/v3/openOrders";
        Map map = new HashMap();
        map.put("method", "GET");
        map.put("query_string", query_string);
        map.put("b_add_time", true);
        String result=requestApi(url_api,map,true);
        return  jsonStr2Map(result);
    }


    /**
     *
     * @param symbol  币名 xxxBTC 如ETHBTC
     * @param quantity 数量
     * @param price 价格
     * @return
     */
    public Map buy(String symbol,Double quantity,Double price){
        String query_string="side=BUY&symbol="+symbol+"&quantity="+quantity+"&price="+price +
                "type=LIMIT&timeInForce=GTC&recvWindow=500000";
        String result=tryRequestOrder(query_string,"POST");
        return  jsonStr2Map(result);

    }

    private Map jsonStr2Map(String jsonStr){
        Gson gson = new Gson();
        Map<String, Object> data = new HashMap<String, Object>();
        data = gson.fromJson(jsonStr, data.getClass());
        return  data;
    }

    /**
     *
     * @param symbol  币名 xxxBTC 如ETHBTC
     * @param quantity 数量
     * @param price 价格
     * @return
     */
    public Map sell(String symbol,Double quantity,Double price){
        String query_string="side=SELL&symbol="+symbol+"&quantity="+quantity+"&price="+price +
                "type=LIMIT&timeInForce=GTC&recvWindow=500000";
        String result=tryRequestOrder(query_string,"POST");
        return  jsonStr2Map(result);
    }

    //获取服务器时间戳（毫秒）
    public Map getServerTime(){
        Long time_server=0L;
        String url_api="https://api.binance.com/api/v1/time";
        Map map = new HashMap();
        map.put("method", "GET");
        map.put("query_string", "");
        map.put("b_add_time", false);
        String result=requestApi(url_api,map,false);
        return  jsonStr2Map(result);
    }

    public Map  getExchangeInfo(){
        String url_api="https://api.binance.com/api/v1/exchangeInfo";
        Map map = new HashMap();
        map.put("method", "GET");
        map.put("query_string", "");
        map.put("b_add_time", false);
        String result=requestApi(url_api,map,false);
        return  jsonStr2Map(result);
    }

    public Map getOrderBook(String symbol,int limit){
        String url_api="https://api.binance.com/api/v1/depth";
        String quert_string="symbol="+symbol+"&limit="+limit;
        Map map = new HashMap();
        map.put("method", "GET");
        map.put("query_string", quert_string);
        map.put("b_add_time", false);
        String result=requestApi(url_api,map,false);
        return  jsonStr2Map(result);
    }


}
