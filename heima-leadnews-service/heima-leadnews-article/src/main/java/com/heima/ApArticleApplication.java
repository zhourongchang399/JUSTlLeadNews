package com.heima;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.article.mapper")
public class ApArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApArticleApplication.class, args);
    }

}
