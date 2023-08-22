package cn.felix.apigateway.config;

import cn.felix.apigateway.filter.CustomGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置（配置路由、过滤器）
 */
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, CustomGatewayFilter filter) {
        //用路由前缀区分路由来源是前端还是接口管理平台
        return builder.routes()
                .route(r ->
                        r.path("/dragon_api/**")
                                .filters(f -> f.filter(filter))
                                //.uri("lb://api-interface")
                                .uri("http://localhost:8123")
                )
                .build();
    }



}