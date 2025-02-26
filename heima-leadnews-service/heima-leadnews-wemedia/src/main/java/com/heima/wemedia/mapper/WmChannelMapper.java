package com.heima.wemedia.mapper;

import com.github.pagehelper.Page;
import com.heima.model.wemedia.pojos.WmChannel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WmChannelMapper {
    List<WmChannel> findAll();

    WmChannel getById(Integer channelId);

    Page<WmChannel> list(WmChannel wmChannel);

    WmChannel selectOne(WmChannel wmChannel);

    void insert(WmChannel wmChannel);

    void delete(Long id);
}
