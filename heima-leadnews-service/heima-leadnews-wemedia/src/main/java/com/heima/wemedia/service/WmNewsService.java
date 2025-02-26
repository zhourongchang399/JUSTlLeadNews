package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;

public interface WmNewsService {
    ResponseResult listNewsByConditions(WmNewsPageReqDto wmNewsPageReqDto);

    ResponseResult submitNews(WmNewsDto wmNewsDto);

    ResponseResult getOneNews(Integer id);

    ResponseResult downOrUp(WmNewsDto wmNewsDto);

    ResponseResult lsitVo(NewsAuthDto newsAuthDto);
}
