package com.heima.article.mapper;

import com.heima.model.article.pojos.ApArticleConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApArticleConfigMapper {
    void insert(ApArticleConfig apArticleConfig);
}
