package com.heima.wemedia.mapper;

import com.github.pagehelper.Page;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WmMaterialMapper {

    public void addMaterial(WmMaterial material);

    Page<WmMaterial> listQuery(@Param("userId") long id, @Param("dto") WmMaterialDto wmMaterialDto);

    Integer selectOne(@Param("url") String s);
}
