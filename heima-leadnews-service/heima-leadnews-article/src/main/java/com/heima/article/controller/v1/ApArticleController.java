package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private HotArticleService hotArticleService;

    /**
     * @author: Zc
     * @description: 频道首页加载文章
     * @date: 2025/3/3 21:37
     * @param null
     * @return
     */
    @PostMapping("/load")
    @ApiOperation("加载文章")
    public ResponseResult loadArticle(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.pageApArticleWithHot(ArticleConstants.LOADTYPE_LOAD_MORE, articleHomeDto, true);
    }

    /**
     * @author: Zc
     * @description: 上拉加载更多文章
     * @date: 2025/3/3 21:37
     * @param null
     * @return
     */
    @PostMapping("/loadmore")
    @ApiOperation("加载更多文章")
    public ResponseResult loadMoreArticle(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.pageApArticle(ArticleConstants.LOADTYPE_LOAD_MORE, articleHomeDto);
    }

    /**
     * @author: Zc
     * @description: 下拉加载新文章
     * @date: 2025/3/3 21:37
     * @param null
     * @return
     */
    @PostMapping("/loadnew")
    @ApiOperation("加载新文章")
    public ResponseResult loadNewArticle(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.pageApArticle(ArticleConstants.LOADTYPE_LOAD_NEW, articleHomeDto);
    }

    /**
     * @author: Zc
     * @description: 加载文章的行为数据
     * @date: 2025/3/3 21:38
     * @param null
     * @return
     */
    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody BehaviorDto behaviorDto) {
        return apArticleService.loadArticleBehavior(behaviorDto);
    }

    /**
     * @author: Zc
     * @description: 获取热门文章数据
     * @date: 2025/3/3 21:38
     * @param null
     * @return
     */
    @GetMapping("/getHotArticle")
    public ResponseResult getHotArticle() {
        return hotArticleService.searchFiveDayHotArticle();
    }

}
