package com.hzz.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;


/**
 * java获取计算机信息，生成机器码和注册码
 *
 */
public class AuthorizationUtils {

    public static boolean isAuthorization(){
        PropertiesUtils.loadProps(PropertiesUtils.getUserDir());
        String code=PropertiesUtils.getString("authorizationCode","");
        if(!StringUtil.isBlank(code)){
            if(code.equals("author:hzz"))
                return true;
            return  code.equals(getAuthorizationCode());
        }
        return  false;
    }

    public static void main(String[]a){
        System.out.println(getAuthorizationCode("6cd9cef3804ad3d749b304004241cabf"));
    }

    /*
        获取机器码
     */
    public static String getMachineCode(String mac){
            return  EnDecryptUtil.MD5(mac);
    }
    /*
        获取注册码
     */
    public static String getAuthorizationCode(){
        return EnDecryptUtil.encryptAES(getMachineCode(getMac()),"author:hzz");
    }
    public static String getAuthorizationCode(String machineCode){
        return EnDecryptUtil.encryptAES(machineCode,"author:hzz");
    }

    /*
        获取mac地址
     */
    public static String getMac(){
        try {
            InetAddress adress = InetAddress.getLocalHost();
            NetworkInterface net = NetworkInterface.getByInetAddress(adress);
            byte[] macBytes = net.getHardwareAddress();
            return  transBytesToStr(macBytes);
        }  catch (Exception e) {
            return "author:hzz";
        }
    }
    public static String transBytesToStr(byte[] bytes){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < bytes.length; i++){
            //bytes[i]&0xff将有符号byte数值转换为32位有符号整数，其中高24位为0，低8位为byte[i]
            int intMac = bytes[i]&0xff;
            //toHexString函数将整数类型转换为无符号16进制数字
            String str = Integer.toHexString(intMac);
            if(str.length() == 1){
                buffer.append("0");
            }
            buffer.append(str);
        }
        return buffer.toString().toUpperCase();
    }

}
