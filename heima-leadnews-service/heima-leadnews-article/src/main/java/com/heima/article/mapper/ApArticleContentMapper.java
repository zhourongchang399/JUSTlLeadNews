package com.heima.article.mapper;

//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.pojos.ApArticleContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 23:49
 */

@Mapper
public interface ApArticleContentMapper{
    void update(ApArticleContent apArticleContent);

    void insert(ApArticleContent apArticleContent);

    ApArticleContent getByArticleId(Long articleId);
}
