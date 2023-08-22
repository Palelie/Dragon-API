//package cn.felix.apigateway.filter;
//
//import cn.felix.apicommon.common.ErrorCode;
//import cn.felix.apicommon.model.entity.InterfaceInfo;
//import cn.felix.apicommon.model.entity.User;
//import cn.felix.apicommon.model.entity.UserInterfaceInfo;
//import cn.felix.apicommon.service.InnerInterfaceInfoService;
//import cn.felix.apicommon.service.InnerUserInterfaceInfoService;
//import cn.felix.apicommon.service.InnerUserService;
//import cn.felix.apigateway.exception.BusinessException;
//import cn.felix.clientsdk.utils.SignUtils;
//import cn.hutool.json.JSONUtil;
//import jodd.util.StringUtil;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.reactivestreams.Publisher;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.Resource;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.nio.CharBuffer;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicReference;
//
///**
// * @author felix
// */
//@Component
//@Slf4j
//@Data
//public class CustomGlobalFilter implements GlobalFilter, Ordered {
//
//    public static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");
//    private static final String DYE_DATA_HEADER = "X-Dye-Data";
//    private static final String DYE_DATA_VALUE = "felix";
//
//    @DubboReference
//    private InnerUserService innerUserService;
//    @DubboReference
//    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
//    @DubboReference
//    private InnerInterfaceInfoService innerInterfaceInfoService;
//    @Resource
//    private RedissonClient redissonClient;
//    @Resource
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        // 1. 请求日志
//        ServerHttpRequest request = exchange.getRequest();
//        String IP_ADDRESS = Objects.requireNonNull(request.getLocalAddress()).getHostString();
//        String path = request.getPath().value();
//        log.info("请求唯一标识：{}", request.getId());
//        log.info("请求路径：{}", path);
//        log.info("请求参数：{}", request.getQueryParams());
//        log.info("请求来源地址：{}", IP_ADDRESS);
//        log.info("请求目标地址：{}", request.getRemoteAddress());
//
//        ServerHttpResponse response = exchange.getResponse();
//
//        // 2. 黑白名单
////        if (!IP_WHITE_LIST.contains(IP_ADDRESS)) {
////            return handleNoAuth(response);
////        }
//        // 3. 用户鉴权 （判断 accessKey 和 secretKey 是否合法）
//        HttpHeaders headers = request.getHeaders();
//        String accessKey = headers.getFirst("accessKey");
//        String timestamp = headers.getFirst("timestamp");
//        String nonce = headers.getFirst("nonce");
//        String sign = headers.getFirst("sign");
//        String method = headers.getFirst("method");
//        String body = null;
//        try {
//            body = URLDecoder.decode(headers.getFirst("body"), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "参数转换错误！");
//        }
//        //获取请求数据body
//        String requestParamStr = "";
//        //if (request.getMethod() == HttpMethod.POST) {
//        if ("GET".equals(method)) {
//            //3.2.1 如果是GET请求，直接从请求头中获取参数
//            requestParamStr = body;
//        } else {
//            //3.2.2 如果是POST请求，从请求体中获取json数据
//            AtomicReference<String> requestBody = new AtomicReference<>("");
//            RecorderServerHttpRequestDecorator requestDecorator = new RecorderServerHttpRequestDecorator(request);
//            Flux<DataBuffer> fluxBody = requestDecorator.getBody();
//            fluxBody.subscribe(buffer -> {
//                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
//                requestBody.set(charBuffer.toString());
//            });
//            //获取body参数
////            JSONObject requestParams = JSONObject.parseObject(requestBody.get());
//            requestParamStr = requestBody.get();
//            if (StringUtil.isNotBlank(requestParamStr) && !JSONUtil.isTypeJSON(requestParamStr)) {
//                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "在线调用仅支持json参数");
//            }
//        }
//
//        if (StringUtil.isEmpty(nonce)
//                || StringUtil.isEmpty(sign)
//                || StringUtil.isEmpty(timestamp)
//                || StringUtil.isEmpty(method)) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求头参数不完整！");
//        }
//
//        // 通过 accessKey 查询是否存在该用户
//        User invokeUser = innerUserService.getInvokeUser(accessKey);
//        if (invokeUser == null) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "accessKey 不合法！");
//        }
//        // 判断随机数是否存在，防止重放攻击
//        String existNonce = (String) redisTemplate.opsForValue().get(nonce);
//        if (StringUtil.isNotBlank(existNonce)) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求重复！");
//        }
//        // 时间戳 和 当前时间不能超过 5 分钟 (300000毫秒)
//        long currentTimeMillis = System.currentTimeMillis() / 1000;
//        long difference = currentTimeMillis - Long.parseLong(timestamp);
//        if (Math.abs(difference) > 300000) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求超时！");
//        }
//        // 校验签名
//        // 应该通过 accessKey 查询数据库中的 secretKey 生成 sign 和前端传递的 sign 对比
//        String serverSign = SignUtils.getSign(invokeUser.getSecretKey(), nonce, timestamp, requestParamStr);
//        if (!sign.equals(serverSign)) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "签名错误！");
//        }
//        // 4. 请求的模拟接口是否存在？
//        // 从数据库中查询接口是否存在，以及方法是否匹配（还有请求参数是否正确）
//        InterfaceInfo interfaceInfo = null;
//        try {
//            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
//        } catch (Exception e) {
//            log.error("getInvokeInterface error", e);
//        }
//        if (interfaceInfo == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口不存在！");
//        }
//
//        // 5. 请求转发，调用模拟接口
//        // 6. 响应日志
//        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
//    }
//
//    @Override
//    public int getOrder() {
//        return -1;
//    }
//
//    /**
//     * 获取已经缓存了的body数据
//     */
//    public class RecorderServerHttpRequestDecorator extends ServerHttpRequestDecorator {
//        private final List<DataBuffer> dataBuffers = new ArrayList<>();
//
//        public RecorderServerHttpRequestDecorator(ServerHttpRequest delegate) {
//            super(delegate);
//            super.getBody().map(dataBuffer -> {
//                dataBuffers.add(dataBuffer);
//                return dataBuffer;
//            }).subscribe();
//        }
//
//        @Override
//        public Flux<DataBuffer> getBody() {
//            return copy();
//        }
//
//        private Flux<DataBuffer> copy() {
//            return Flux.fromIterable(dataBuffers)
//                    .map(buf -> buf.factory().wrap(buf.asByteBuffer()));
//        }
//    }
//
//    /**
//     * 响应没权限
//     *
//     * @param response
//     * @return
//     */
//    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
//        response.setStatusCode(HttpStatus.FORBIDDEN);
//        response.setRawStatusCode(HttpStatus.FORBIDDEN.value());
//        return response.setComplete();
//    }
//
//    /**
//     * 处理响应
//     *
//     * @param exchange
//     * @param chain
//     * @return
//     */
//    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
//        try {
//            ServerHttpResponse originalResponse = exchange.getResponse();
//            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
//
//            HttpStatus statusCode = originalResponse.getStatusCode();
//
//            if (statusCode == HttpStatus.OK) {
//                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
//                    @Override
//                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                        if (body instanceof Flux) {
//                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
//                            return super.writeWith(
//                                    fluxBody.map(dataBuffer -> {
//                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
//                                        invokeCount(exchange.getRequest(), exchange.getResponse(), interfaceInfoId, userId);
//
//                                        byte[] content = new byte[dataBuffer.readableByteCount()];
//                                        dataBuffer.read(content);
//                                        DataBufferUtils.release(dataBuffer);//释放掉内存
//                                        // 构建日志
//                                        StringBuilder sb2 = new StringBuilder(200);
//                                        List<Object> rspArgs = new ArrayList<>();
//                                        rspArgs.add(originalResponse.getStatusCode());
//                                        String data = new String(content, StandardCharsets.UTF_8); //data
//                                        sb2.append(data);
//                                        // 打印日志
//                                        log.info("响应结果：" + data);
//                                        return bufferFactory.wrap(content);
//                                    })
//                            );
//                        } else {
//                            // 8. 调用失败，返回规范的错误码
//                            log.error("<--- {} 响应code异常", getStatusCode());
//                        }
//                        return super.writeWith(body);
//                    }
//                };
//
//                // 流量染色，只有染色数据才能被调用
//                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
//                        .header(DYE_DATA_HEADER, DYE_DATA_VALUE)
//                        .build();
//
//                ServerWebExchange serverWebExchange = exchange.mutate()
//                        .request(modifiedRequest)
//                        .response(decoratedResponse)
//                        .build();
//                return chain.filter(serverWebExchange);
//            }
//            //降级处理返回数据
//            return chain.filter(exchange);
//        } catch (Exception e) {
//            log.error("网关处理异常响应.\n" + e);
//            return chain.filter(exchange);
//        }
//    }
//
//    private void invokeCount(ServerHttpRequest request, ServerHttpResponse response, Long interfaceInfoId, Long userId) {
//        if (response.getStatusCode() == HttpStatus.OK) {
//            //加锁
//            String lock = String.valueOf(interfaceInfoId + ":" + userId).intern();
//            RLock rLock = redissonClient.getLock(lock);
//            if (rLock.tryLock()) {
//                try {
//                    String nonce = request.getHeaders().getFirst("nonce");
//                    if (StringUtil.isEmpty(nonce)) {
//                        throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求异常");
//                    }
//                    UserInterfaceInfo userInterfaceInfo = innerUserInterfaceInfoService.hasLeftNum(interfaceInfoId, userId);
//                    // 接口未绑定用户
//                    if (userInterfaceInfo == null) {
//                        Boolean save = innerUserInterfaceInfoService.addDefaultUserInterfaceInfo(interfaceInfoId, userId);
//                        if (save == null || !save) {
//                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口绑定用户失败！");
//                        }
//                    }
//                    if (userInterfaceInfo != null && userInterfaceInfo.getLeftNum() <= 0) {
//                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用次数已用完！");
//                    }
//                    redisTemplate.opsForValue().set(nonce, 1, 5, TimeUnit.MINUTES);
//                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
//                } finally {
//                    rLock.unlock();
//                }
//            }
//        }
//    }
//}