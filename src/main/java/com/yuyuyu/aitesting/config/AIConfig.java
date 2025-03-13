package com.yuyuyu.aitesting.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AIConfig {
    /**
     * api key需要从配置文件中读取
     */
    private String apiKey;

    /**
     * 初始化客户端
     * @return
     */
    @Bean
    public ClientV4 getClient(){
        return new ClientV4.Builder(apiKey)
                .networkConfig(30,60,60,60, TimeUnit.SECONDS)  //响应超时
                .build();
    }
}
