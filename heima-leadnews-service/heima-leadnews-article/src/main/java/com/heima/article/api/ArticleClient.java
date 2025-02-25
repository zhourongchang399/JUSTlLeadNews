package com.heima.article.api;

import com.heima.api.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/20 16:59
 */
@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    ApArticleService apArticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(ArticleDto articleDto) {
        return apArticleService.saveArticle(articleDto);
    }

    @Override
    @GetMapping("/api/v1/article/loadArticle")
    public ResponseResult loadArticle() {
        return apArticleService.loadArticle();
    }

}
