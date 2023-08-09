package cn.felix.clientsdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static cn.felix.clientsdk.utils.SignUtils.getSign;

/**
 * API 调用
 */
public class ApiClient {

    public static String GATEWAY_HOST;


    private String accessKey;

    private String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void setGatewayHost(String gatewayHost) {
        GATEWAY_HOST = gatewayHost;
    }


    private Map<String, String> getHeaderMap(String body, String method) throws UnsupportedEncodingException {
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        String nonce = RandomUtil.randomNumbers(10);
        map.put("nonce", nonce);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        map.put("timestamp", timestamp);
        map.put("sign", getSign(secretKey,nonce,timestamp,body));
        map.put("method", method);
        map.put("body",method.equals("GET") ? URLEncoder.encode(body,"utf-8") : "");
        return map;
    }

    public String invokeInterface(String params, String url, String method) throws UnsupportedEncodingException {
        HttpResponse httpResponse = null;
        if ("GET".equals(method)){
            httpResponse = HttpRequest.get(GATEWAY_HOST + url)
                            .header("Accept-Charset", CharsetUtil.UTF_8)
                            .addHeaders(getHeaderMap(params,method))
                            .execute();

        }else {
            httpResponse = HttpRequest.post(GATEWAY_HOST + url)
                    .header("Accept-Charset", CharsetUtil.UTF_8)
                    .addHeaders(getHeaderMap(params, method))
                    .body(params)
                    .execute();
        }
        return JSONUtil.formatJsonStr(httpResponse.body());
    }

}
