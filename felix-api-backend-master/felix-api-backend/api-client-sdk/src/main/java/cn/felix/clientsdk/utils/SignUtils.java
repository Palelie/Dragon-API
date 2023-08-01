package cn.felix.clientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名生成类
 */
public class SignUtils {

    public static final String SALT = "felix";
    public static  <T> String getSign(String secretKey,String nonce,String timestamp,String body){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        return md5.digestHex(SALT + secretKey + nonce + timestamp + body);
    }
}
