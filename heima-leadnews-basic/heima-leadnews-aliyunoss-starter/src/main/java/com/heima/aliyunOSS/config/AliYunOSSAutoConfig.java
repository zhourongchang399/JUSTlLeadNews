package com.heima.aliyunOSS.config;

import com.heima.aliyunOSS.service.AliOssService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 22:56
 */
@Configuration
@EnableConfigurationProperties(AliYunOSSArgConfig.class)
public class AliYunOSSAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public AliOssService aliOssService(AliYunOSSArgConfig aliYunOSSArgConfig) {
        return new AliOssService(aliYunOSSArgConfig);
    }
}
