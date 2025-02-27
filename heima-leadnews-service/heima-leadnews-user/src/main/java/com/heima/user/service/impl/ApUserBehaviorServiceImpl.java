package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heima.api.wemedia.IWmUserClient;
import com.heima.common.exception.CustomException;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFan;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserBehaviorService;
import com.heima.utils.common.WmThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 16:00
 */
@Service
public class ApUserBehaviorServiceImpl implements ApUserBehaviorService {

    @Autowired
    private ApUserFanMapper apUserFanMapper;

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    @Autowired
    private ApUserMapper apUserMapper;

    @Autowired
    private IWmUserClient wmUserClient;

    @Override
    public ResponseResult userFollow(BehaviorDto behaviorDto) {
        // 参数校验
        if (behaviorDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取用户Id
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断用户是否登录
        if (userId == null) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 查询作者信息
        Integer apUserId = null;
        ResponseResult result = wmUserClient.getWmUserInfo((int) behaviorDto.getAuthorId());
        if (result.getCode() == 200 && result.getData() != null) {
            WmUser wmUser = JSON.parseObject((String) result.getData(), WmUser.class);
            apUserId = wmUser.getApUserId();
        }
        ApUser apUser = apUserMapper.getById(apUserId);

        // 查询用户信息
        ApUser userInfo = apUserMapper.getById(userId);

        // 关注
        if (behaviorDto.getOperation() == 0) {

            // 封装关注对象
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(userId);
            apUserFollow.setFollowId(apUser.getId());
            apUserFollow.setFollowName(apUser.getName());
            apUserFollow.setLevel((short) 0);
            apUserFollow.setIsNotice((short) 1);
            apUserFollow.setCreatedTime(LocalDateTime.now());

            // 新增关注数据
            apUserFollowMapper.insert(apUserFollow);

            // 封装粉丝对象
            ApUserFan apUserFan = new ApUserFan();
            apUserFan.setUserId(apUser.getId());
            apUserFan.setFansId(userId);
            apUserFan.setFanName(userInfo.getName());
            apUserFan.setLevel((short) 0);
            apUserFan.setCreatedTime(LocalDateTime.now());
            apUserFan.setIsDisplay((short) 0);
            apUserFan.setIsShieldLetter((short) 0);
            apUserFan.setIsShieldComment((short) 0);

            // 新增粉丝数据
            apUserFanMapper.insert(apUserFan);

        } else if (behaviorDto.getOperation() == 1) {
            // 取消关注
            // 删除关注表数据
            QueryWrapper<ApUserFollow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("follow_id", apUser.getId());
            apUserFollowMapper.delete(queryWrapper);
            // 删除粉丝表数据
            QueryWrapper<ApUserFan> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("fans_id", userId).eq("user_id", apUser.getId());
            apUserFanMapper.delete(queryWrapper1);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

}
