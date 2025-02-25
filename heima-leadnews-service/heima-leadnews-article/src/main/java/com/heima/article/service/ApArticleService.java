package com.heima.article.service;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 15:29
 */

public interface ApArticleService {

    public List<ApArticle> pageApArticle(Short type, ArticleHomeDto articleHomeDto);

    ResponseResult saveArticle(ArticleDto articleDto);

    ResponseResult loadArticle();
}
