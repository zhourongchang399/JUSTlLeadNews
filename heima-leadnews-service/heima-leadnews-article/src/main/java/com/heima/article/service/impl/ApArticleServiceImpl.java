package com.heima.article.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.aliyunOSS.service.AliOssService;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

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

    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {
        // 参数校验
        if (articleDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 判断是否存在
        Long articleId = null;
        if (articleDto.getId() == null) {
            // 保存文章信息
            articleId = saveArticleInfo(articleDto);
            // 保存文章内容
            saveArticleContent(articleDto, articleId);
            // 保存文章配置
            saveArticleConfig(articleId);
        } else {
            // 修改文章信息
            modifyArticle(articleDto);
            // 修改文章内容
            modifyArticleContent(articleDto);
        }

        // 操作成功，返回文章ID
        return ResponseResult.okResult(articleId);
    }

    private void modifyArticle(ArticleDto articleDto) {
        // 参数拷贝
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto, apArticle);
        // 修改文章信息
        apArticleMapper.updateById(apArticle);
    }

    private void saveArticleConfig(Long articleId) {
        // 设置文章默认配置
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setIsComment(ArticleConstants.DEFAULT_IS_COMMENT);
        apArticleConfig.setIsForward(ArticleConstants.DEFAULT_IS_FORWARD);
        apArticleConfig.setIsDown(ArticleConstants.DEFAULT_NOT_IS_DOWN);
        apArticleConfig.setIsDelete(ArticleConstants.DEFAULT_NOT_IS_DELETE);
        apArticleConfig.setArticleId(articleId);
        // 保存文章配置
        apArticleConfigMapper.insert(apArticleConfig);
    }

    private void modifyArticleContent(ArticleDto articleDto) {
        // 参数拷贝
        ApArticleContent apArticleContent = new ApArticleContent();
        BeanUtils.copyProperties(articleDto, apArticleContent);
        apArticleContent.setArticleId(articleDto.getId());
        // 保存文章内容
        apArticleContentMapper.update(apArticleContent);
    }

    private void saveArticleContent(ArticleDto articleDto, Long articleId) {
        // 参数拷贝
        ApArticleContent apArticleContent = new ApArticleContent();
        BeanUtils.copyProperties(articleDto, apArticleContent);
        apArticleContent.setArticleId(articleId);
        // 保存文章内容
        apArticleContentMapper.insert(apArticleContent);
    }

    private Long saveArticleInfo(ArticleDto articleDto) {
        // 参数拷贝
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto, apArticle);
        // 保存文章信息
        apArticleMapper.insertArticle(apArticle);
        return apArticle.getId();
    }

}
