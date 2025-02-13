package com.heima.wemedia.mapper;

import com.heima.model.wemedia.pojos.WmMaterial;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WmMaterialMapper {

    public void addMaterial(WmMaterial material);
}
