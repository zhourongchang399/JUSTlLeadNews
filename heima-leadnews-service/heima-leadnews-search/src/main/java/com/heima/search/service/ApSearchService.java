package com.heima.search.service;

import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.article.pojos.ApUserSearch;
import com.heima.model.article.vos.ApArticleSearchVo;

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

    List<ApArticleSearchVo> search(UserSearchDto dto) throws IOException;
}
