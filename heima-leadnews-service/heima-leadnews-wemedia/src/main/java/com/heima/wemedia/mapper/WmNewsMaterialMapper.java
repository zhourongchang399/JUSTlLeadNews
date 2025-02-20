package com.heima.wemedia.mapper;

import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WmNewsMaterialMapper {

    void addNewsMaterial(@Param("wmNewsMaterials") List<WmNewsMaterial> wmNewsMaterials);

    void deleteByNewsIdAndType(Integer newsId, Short type);
}
