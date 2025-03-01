package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/14 0:42
 */
@Service
public class WmChannelServiceImpl implements WmChannelService {

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Override
    public ResponseResult getAllChannels() {
        List<WmChannel> wmChannelList = wmChannelMapper.findAll();
        return ResponseResult.okResult(JSON.toJSONString(wmChannelList));
    }

    @Override
    public ResponseResult list(WmChannelDto wmChannelDto) {
        // 参数校验
        if (wmChannelDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 设置分页大小上下限
        if (wmChannelDto.getSize() > 20) {
            wmChannelDto.setSize(20);
        } else if (wmChannelDto.getSize() < 1) {
            wmChannelDto.setSize(5);
        }

        // 封装查询对象
        WmChannel wmChannel = new WmChannel();
        if (wmChannelDto.getName() != null && wmChannelDto.getName().length() > 0) {
            wmChannel.setName(wmChannelDto.getName());
        }

        // 开始分页
        PageHelper.startPage(wmChannelDto.getPage(), wmChannelDto.getSize());
        // 执行查询
        Page<WmChannel> wmChannelPage = wmChannelMapper.list(wmChannel);

        // 封装结果
        PageResponseResult pageResponseResult = new PageResponseResult();
        pageResponseResult.setData(wmChannelPage.getResult());
        pageResponseResult.setCurrentPage(wmChannelPage.getPageNum());
        pageResponseResult.setTotal((int) wmChannelPage.getTotal());
        pageResponseResult.setSize(wmChannelPage.getPageSize());

        // 返回结果
        return pageResponseResult;
    }

    @Override
    public ResponseResult save(WmChannel wmChannel) {
        // 参数校验
        if (wmChannel == null || wmChannel.getName() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询是否存在
        WmChannel selectedWmChannel = wmChannelMapper.selectOne(wmChannel);
        if (selectedWmChannel != null) {
            // 存在，则直接返回结果
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }

        // 不存在，则新增敏感词
        // 补全对象
        wmChannel.setId(null);
        wmChannel.setCreatedTime(new Date());
        if (wmChannel.getOrd() == null) {
            wmChannel.setOrd(1);
        }
        wmChannelMapper.insert(wmChannel);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult delete(Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询该数据是否存在
        WmChannel wmChannel = new WmChannel();
        wmChannel.setId(Integer.valueOf(id.intValue()));
        WmChannel selectedOne = wmChannelMapper.selectOne(wmChannel);

        // 不存在，则返回
        if (selectedOne == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 存在，则判断是否停用
        if (selectedOne.getStatus()) {
            // 启用
            throw new CustomException(AppHttpCodeEnum.CHANNEL_IS_ON);
        }

        // 停用，则删除数据
        wmChannelMapper.delete(id);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult update(WmChannel wmChannel) {
        // 参数校验
        if (wmChannel == null || wmChannel.getId() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询被修改的频道信息
        WmChannel updatedOne = wmChannelMapper.getById(wmChannel.getId());

        // 如果需要停用频道，则判断该频道是否被引用
        if (wmChannel.getStatus() != null && !wmChannel.getStatus()) {
            Integer count = wmChannelMapper.referenceCount(updatedOne);
            if (count > 0) {
                // 已被引用，不准停用
                throw new CustomException(AppHttpCodeEnum.CHANNEL_IS_REFERNCE);
            }
        }

        // 判断频道是否已经停用
        if (updatedOne.getStatus()) {
            if (wmChannel.getStatus() != null && wmChannel.getStatus()) {
                // 启用同时并未执行停用，则不准编辑
                throw new CustomException(AppHttpCodeEnum.CHANNEL_IS_ON);
            }
        }

        // 判断频道名称是否存在
        WmChannel findedChannel = new WmChannel();
        findedChannel.setName(wmChannel.getName());
        findedChannel.setId(null);
        WmChannel selectedOne = wmChannelMapper.selectOne(findedChannel);
        // 判断频道名称是否已存在
        if (selectedOne != null) {
            // 判断是否是停用或启用操作
            if (wmChannel.getStatus() != null && wmChannel.getStatus() == selectedOne.getStatus()) {
                // 否，则不准编辑
                throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
            }
        }

        // 编辑频道
        wmChannelMapper.update(wmChannel);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}
