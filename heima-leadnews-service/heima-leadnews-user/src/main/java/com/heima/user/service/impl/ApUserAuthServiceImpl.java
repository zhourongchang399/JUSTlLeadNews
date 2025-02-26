package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.constants.KafkaConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.kafka.KafkaService;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.admin.pojos.ApUserRealname;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vos.WmNewsVo;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 22:40
 */
@Service
public class ApUserAuthServiceImpl implements ApUserAuthService {

    @Autowired
    private ApUserRealnameMapper apUserRealnameMapper;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    public ResponseResult list(AuthDto authDto) {
        // 参数校验
        if (authDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 设置size的上下限
        if (authDto.getSize() > 20) {
            authDto.setSize(20);
        } else if (authDto.getSize() < 5) {
            authDto.setSize(5);
        }

        // 设置page的下限
        if (authDto.getPage() <= 0) {
            authDto.setPage(1);
        }

        // 封装对象
        ApUserRealname apUserRealname = new ApUserRealname();
        BeanUtils.copyProperties(authDto, apUserRealname);

        // 开始分页
        PageHelper.startPage(authDto.getPage(), authDto.getSize());

        // 开始查询
        Page<ApUserRealname> apUserRealnamePage = apUserRealnameMapper.list(apUserRealname);

        // 封装结果
        PageResponseResult pageResponseResult = new PageResponseResult();
        pageResponseResult.setCurrentPage(apUserRealnamePage.getPageNum());
        pageResponseResult.setTotal((int) apUserRealnamePage.getTotal());
        pageResponseResult.setData(apUserRealnamePage.getResult());
        pageResponseResult.setSize(apUserRealnamePage.getPageSize());

        // 返回结果
        return pageResponseResult;
    }

    @Override
    public ResponseResult authFial(AuthDto authDto) {
        // 参数校验
        if (authDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询是否存在
        ApUserRealname apUserRealname = apUserRealnameMapper.getById(authDto.getId());
        if (apUserRealname == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 封装对象
        apUserRealname.setStatus((short) 2);
        apUserRealname.setReason("审核失败");
        apUserRealname.setReason(authDto.getMsg());
        apUserRealname.setUpdatedTime(new Date());

        // 执行更新
        apUserRealnameMapper.update(apUserRealname);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult authPass(AuthDto authDto) {
        // 参数校验
        if (authDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询是否存在
        ApUserRealname apUserRealname = apUserRealnameMapper.getById(authDto.getId());
        if (apUserRealname == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 封装对象
        apUserRealname.setStatus((short) 9);
        apUserRealname.setReason("审核成功");
        apUserRealname.setReason(authDto.getMsg());
        apUserRealname.setUpdatedTime(new Date());

        // 执行更新
        apUserRealnameMapper.update(apUserRealname);

        // 查询app端当前用户信息
        ApUser apUser = apUserMapper.getById(apUserRealname.getUserId());

        // 封装对象
        WmUser wmUser = new WmUser();
        wmUser.setApUserId(apUser.getId());
        wmUser.setName(apUser.getName());
        wmUser.setPassword(apUser.getPassword());
        wmUser.setSalt(apUser.getSalt());
        wmUser.setImage(apUserRealname.getLiveImage());
        wmUser.setPhone(apUser.getPhone());
        wmUser.setStatus(1);
        wmUser.setCreatedTime(new Date());

        // 向Kafka发送消息，以通知自媒体端生成该用户
        kafkaService.sendAsync(KafkaConstants.GANERATE_WM_USER, JSON.toJSONString(wmUser));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
