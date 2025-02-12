package com.heima.article.mapper;

import com.github.pagehelper.Page;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApArticleMapper {

    Page<ApArticle> pageQueryApArticle(@Param("type") Short type,@Param("articleHomeDto") ArticleHomeDto articleHomeDto);
}
