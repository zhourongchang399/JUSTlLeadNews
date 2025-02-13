package com.heima.article.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.aliyunOSS.service.AliOssService;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 15:29
 */
@Service
@Slf4j
public class ApArticleServiceImpl implements ApArticleService {

    private static final int PAGE_MAX_SIZE = 50;
    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private AliOssService aliOssService;

    @Override
    public List<ApArticle> pageApArticle(Short type, ArticleHomeDto articleHomeDto) {
        // 数量校验
        if (articleHomeDto.getSize() == null || articleHomeDto.getSize() == 0) {
            articleHomeDto.setSize(10);
        }
        articleHomeDto.setSize(Math.min(articleHomeDto.getSize(), PAGE_MAX_SIZE));

        // 类型校验
        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 文章频道校验
        if (articleHomeDto.getTag().isEmpty()) {
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 时间校验
        if (articleHomeDto.getMaxBehotTime() == null){
            articleHomeDto.setMaxBehotTime(new Date());
        }
        if (articleHomeDto.getMinBehotTime() == null){
            articleHomeDto.setMinBehotTime(new Date());
        }

        log.info(articleHomeDto.toString());
        PageHelper.startPage(0,articleHomeDto.getSize());
        Page<ApArticle> apArticlePage = apArticleMapper.pageQueryApArticle(type, articleHomeDto);
        log.info("查询到{}条数据！", apArticlePage.getTotal());

        return apArticlePage.getResult();
    }
}
