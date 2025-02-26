package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmChannel;

public interface WmChannelService {
    ResponseResult getAllChannels();

    ResponseResult list(WmChannelDto wmChannelDto);

    ResponseResult save(WmChannel wmChannel);

    ResponseResult delete(Long id);

    ResponseResult update(WmChannel wmChannel);
}
