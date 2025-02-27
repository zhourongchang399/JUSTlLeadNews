package com.heima.article.config;

import com.heima.article.interceptor.ArticleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/13 16:44
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ArticleInterceptor()).addPathPatterns("/**");
    }

}
