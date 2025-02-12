package com.heima.aliyunOSS.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 22:53
 */
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliYunOSSArgConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
