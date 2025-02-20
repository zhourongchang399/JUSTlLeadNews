package com.heima.api.article;

import com.heima.api.article.fallback.IArticleClientFallBack;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/20 16:23
 */
@FeignClient(value = "leadnews-article", fallback = IArticleClientFallBack.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto articleDto);

}
