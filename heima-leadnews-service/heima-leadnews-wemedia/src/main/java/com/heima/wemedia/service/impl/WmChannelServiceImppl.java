package com.heima.wemedia.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/14 0:42
 */
@Service
public class WmChannelServiceImppl implements WmChannelService {

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Override
    public ResponseResult getAllChannels() {
        List<WmChannel> wmChannelList = wmChannelMapper.findAll();
        return ResponseResult.okResult(wmChannelList);
    }
}
