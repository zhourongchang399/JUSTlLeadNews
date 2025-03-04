package com.heima.article.mapper;

import com.github.pagehelper.Page;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.ApArticleSearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface ApArticleMapper {

    Page<ApArticle> pageQueryApArticle(@Param("type") Short type,@Param("articleHomeDto") ArticleHomeDto articleHomeDto);

    void updateById(ApArticle article);

    void insertArticle(ApArticle apArticle);

    List<ApArticleSearchVo> loadArticle(@Param("articleId") Long articleId);

    List<ApArticle> findAll(Date time);

    ApArticle getById(Long articleId);
}
