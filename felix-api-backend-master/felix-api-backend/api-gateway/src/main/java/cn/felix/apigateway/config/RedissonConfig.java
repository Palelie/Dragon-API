package cn.felix.apigateway.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        if (StrUtil.isNotBlank(password)){
            config.useSingleServer().setAddress(redisAddress).setPassword(password).setDatabase(2);
        }else {
            config.useSingleServer().setAddress(redisAddress).setDatabase(2);
        }
        // 2. 创建实例
        return Redisson.create(config);
    }
}
