package com.heima.wemedia.mapper;

import com.heima.model.wemedia.pojos.WmSensitive;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WmSensitiveMapper {
    List<WmSensitive> findAll();
}
