package com.heima.search.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 15:13
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class EsConfig {

    private static final Logger log = LoggerFactory.getLogger(EsConfig.class);
    private String host;
    private Integer port;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        log.info("ElasticSearch装载成功：{}", host + port);
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    }

}
