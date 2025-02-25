package com.heima.search.service;

import com.heima.model.article.dtos.HistorySearchDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApUserSearchService {

    /**
     * 保存用户搜索历史记录
     * @param keyword
     * @param userId
     */
    public void insert(String keyword,Integer userId);

    ResponseResult loadSearchHistory();

    ResponseResult deleteSearchHistory(HistorySearchDto historySearchDto);
}
