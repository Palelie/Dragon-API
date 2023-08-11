package cn.felix.apiinterface.controller;

import cn.felix.clientsdk.model.User;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一接口调用处理
 * @author felix
 */
@RestController("/dragon_api")
public class DragonApiController {

    @PostMapping("/**")
    public String invokePostAPI(@RequestBody Object object, HttpServletRequest request) {
        String host = request.getHeader("remote-host");
        String url = request.getHeader("url");
        String body = JSONUtil.toJsonStr(object);
        HttpResponse httpResponse = HttpRequest.post(host + url)
                .body(body)
                .execute();
        return httpResponse.body();
    }
    @GetMapping("/**")
    public String invokeGetAPI(HttpServletRequest request) {
        String host = request.getHeader("remote-host");
        String url = request.getHeader("url");

        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.get(host + url + "?" + body)
                .execute();
        return httpResponse.body();
    }


}
