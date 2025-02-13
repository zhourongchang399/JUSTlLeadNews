package com.heima.wemedia.mapper;

import com.github.pagehelper.Page;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WmNewsMapper {
    Page<WmNews> listByConditions(@Param("userId") Long id, @Param("dto") WmNewsPageReqDto wmNewsPageReqDto);
}
