package com.heima.wemedia.mapper;

import com.github.pagehelper.Page;
import com.heima.model.wemedia.pojos.WmSensitive;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WmSensitiveMapper {
    Page<WmSensitive> list(WmSensitive wmSensitive);

    List<WmSensitive> findAll();

    WmSensitive selectOne(WmSensitive wmSensitive);

    void insert(WmSensitive wmSensitive);
}
