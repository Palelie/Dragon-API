package cn.felix.apicommon.utils;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLUtil {

    public static String decode(String body, String charset) {
        try {
            body = URLDecoder.decode(body, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    public static String encode(String body, String charset) {
        try {
            body = URLEncoder.encode(body, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

}
