package com.heima.wemedia.mapper;

import com.github.pagehelper.Page;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WmMaterialMapper {

    public void addMaterial(WmMaterial material);

    Page<WmMaterial> listQuery(WmMaterialDto wmMaterialDto);
}
