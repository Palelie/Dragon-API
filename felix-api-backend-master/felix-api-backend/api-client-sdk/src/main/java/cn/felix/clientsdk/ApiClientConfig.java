package cn.felix.clientsdk;

import cn.felix.clientsdk.client.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Felix API 客户端配置类
 * @author felix
 */
@Data
@Configuration
@ConfigurationProperties("felix.client")
@ComponentScan
public class ApiClientConfig {

    private String accessKey;

    private String secretKey;

    /**
     * 此处方法取名无所谓的，不影响任何地方
     *
     * @return
     */
    @Bean
    public ApiClient getApiClient() {
        return new ApiClient(accessKey, secretKey);
    }
}
