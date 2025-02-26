package com.heima.wemedia.service;


import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmAutoScanService {
    void scanNews(Integer id);

    Long saveApArticle(WmNews wmNews);
}
