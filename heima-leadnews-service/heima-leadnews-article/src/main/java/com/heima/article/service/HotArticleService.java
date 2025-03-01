package com.heima.article.service;

import com.heima.model.article.vos.ApHotArticleVo;
import com.heima.model.common.dtos.ResponseResult;

import java.util.List;

public interface HotArticleService {

    public ResponseResult searchFiveDayHotArticle();

}
