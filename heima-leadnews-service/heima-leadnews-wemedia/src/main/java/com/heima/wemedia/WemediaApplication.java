package com.heima.wemedia;

//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient // 启动nacos服务发现
@MapperScan("com.heima.wemedia.mapper") // 修改mapper扫描路径
@EnableFeignClients(basePackages = "com.heima.api") // 启动openFeign
@EnableAsync // 启动异步
@EnableScheduling // 启动定时任务
public class WemediaApplication {

    public static void main(String[] args) {
        SpringApplication. run(WemediaApplication.class,args);
    }

}
