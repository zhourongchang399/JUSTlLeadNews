package com.heima.wemedia.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.utils.common.MySensitiveWorkUtil;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.apache.yetus.audience.InterfaceAudience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/21 15:29
 */
@Service
public class WmSensitiveServiceImpl implements WmSensitiveService {

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Override
    @Transactional
    public Map<String, Integer> textCheck(String text) {
        Map<String, Integer> resultMap = null;

        // 参数校验
        if (text == null || "".equals(text)) {
            return resultMap;
        }

        // 获取违禁词字典
        List<WmSensitive> wmSensitiveList = wmSensitiveMapper.findAll();
        List<String> sensitiveList = wmSensitiveList.stream().map(s -> s.getSensitives()).collect(Collectors.toList());

        // 初始化字典
        MySensitiveWorkUtil.initDictionary(sensitiveList);

        // 匹配违禁词
        resultMap = MySensitiveWorkUtil.matchDictionary(text);

        // 返回结果
        return resultMap;
    }

    @Override
    public ResponseResult list(SensitiveDto sensitiveDto) {
        // 参数校验
        if (sensitiveDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 设置分页大小上下限
        if (sensitiveDto.getSize() > 20) {
            sensitiveDto.setSize(20);
        } else if (sensitiveDto.getSize() < 1) {
            sensitiveDto.setSize(5);
        }

        // 封装查询对象
        WmSensitive wmSensitive = new WmSensitive();
        if (sensitiveDto.getName() != null && sensitiveDto.getName().length() > 0) {
            wmSensitive.setSensitives(sensitiveDto.getName());
        }

        // 开始分页
        PageHelper.startPage(sensitiveDto.getPage(), sensitiveDto.getSize());
        // 执行查询
        Page<WmSensitive> wmSensitivePage = wmSensitiveMapper.list(wmSensitive);

        // 封装结果
        PageResponseResult pageResponseResult = new PageResponseResult();
        pageResponseResult.setData(wmSensitivePage.getResult());
        pageResponseResult.setCurrentPage(wmSensitivePage.getPageNum());
        pageResponseResult.setTotal((int) wmSensitivePage.getTotal());
        pageResponseResult.setSize(wmSensitivePage.getPageSize());

        // 返回结果
        return pageResponseResult;
    }

    @Override
    @Transactional
    public ResponseResult save(WmSensitive wmSensitive) {
        // 参数校验
        if (wmSensitive == null || wmSensitive.getSensitives() == null || wmSensitive.getSensitives().isEmpty()) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询是否存在
        WmSensitive selectedWmSensitive = wmSensitiveMapper.selectOne(wmSensitive);
        if (selectedWmSensitive != null) {
            // 存在，则直接返回结果
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }

        // 不存在，则新增敏感词
        wmSensitive.setId(null);
        wmSensitive.setCreatedTime(new Date());
        wmSensitiveMapper.insert(wmSensitive);

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
        WmSensitive wmSensitive = new WmSensitive();
        wmSensitive.setId(Integer.valueOf(id.intValue()));
        WmSensitive selectedOne = wmSensitiveMapper.selectOne(wmSensitive);

        // 不存在，则返回
        if (selectedOne == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 存在，则删除数据
        wmSensitiveMapper.delete(id);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult update(WmSensitive wmSensitive) {
        // 参数校验
        if (wmSensitive == null || wmSensitive.getId() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询该数据是否存在
        WmSensitive findedOne = new WmSensitive();
        findedOne.setSensitives(wmSensitive.getSensitives());
        WmSensitive selectedOne = wmSensitiveMapper.selectOne(findedOne);

        // 存在，则返回
        if (selectedOne != null) {
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }

        // 不存在，则更新结果
        wmSensitive.setCreatedTime(new Date());
        wmSensitiveMapper.update(wmSensitive);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
