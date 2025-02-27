package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 15:28
 */

@RestController
@RequestMapping("/api/v1/article")
@Api(tags = "加载文章")
public class ApArticleController {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/load")
    @ApiOperation("加载文章")
    public ResponseResult loadArticle(@RequestBody ArticleHomeDto articleHomeDto) {
        return ResponseResult.okResult(apArticleService.pageApArticle(ArticleConstants.LOADTYPE_LOAD_MORE, articleHomeDto));
    }

    @PostMapping("/loadmore")
    @ApiOperation("加载更多文章")
    public ResponseResult loadMoreArticle(@RequestBody ArticleHomeDto articleHomeDto) {
        return ResponseResult.okResult(apArticleService.pageApArticle(ArticleConstants.LOADTYPE_LOAD_MORE, articleHomeDto));
    }

    @PostMapping("/loadnew")
    @ApiOperation("加载新文章")
    public ResponseResult loadNewArticle(@RequestBody ArticleHomeDto articleHomeDto) {
        return ResponseResult.okResult(apArticleService.pageApArticle(ArticleConstants.LOADTYPE_LOAD_NEW, articleHomeDto));
    }

    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody BehaviorDto behaviorDto) {
        return apArticleService.loadArticleBehavior(behaviorDto);
    }

}
