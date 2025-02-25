package com.heima.search.service;

import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.article.pojos.ApUserSearch;
import com.heima.model.article.vos.ApArticleSearchVo;
import com.heima.model.common.dtos.ResponseResult;

import java.io.IOException;
import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 14:49
 */
public interface ApSearchService {
    void getMappingInfo(String mapping) throws IOException;

    void initMapping(String mapping) throws IOException;

    ResponseResult search(UserSearchDto dto) throws IOException;

    void insertArticle(ApArticleSearchVo apArticleSearchVo) throws IOException;
}
