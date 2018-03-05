package com.hzz.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class HmacUtils {
    private static String MAC_NAME = "HmacSHA256";
    public  static String getSignature(String data, String key) throws Exception {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : rawHmac) {
            sb.append(byteToHexString(b));
        }
        return sb.toString();
    }
    private static String byteToHexString(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0f];
        ob[1] = Digit[ib & 0X0F];
        return new String(ob);
    }
}
