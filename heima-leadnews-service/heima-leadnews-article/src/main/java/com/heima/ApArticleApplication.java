package com.heima;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.article.mapper")
@EnableFeignClients(basePackages = "com.heima.api")
@EnableAsync
public class ApArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApArticleApplication.class, args);
    }

}
