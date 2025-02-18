package com.heima.wemedia.mapper;

import com.github.pagehelper.Page;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WmNewsMapper {
    Page<WmNews> listByConditions(@Param("userId") Integer id, @Param("dto") WmNewsPageReqDto wmNewsPageReqDto);

    Integer saveNews(WmNews wmNews);

    void delectNews(@Param("id") Integer id);

    void addNewsMaterial(@Param("wmNewsMaterials") List<WmNewsMaterial> wmNewsMaterials);

    void updateNews(WmNews needToUpdateNews);
}
