package com.heima.user.service;

import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 16:00
 */
public interface ApUserBehaviorService {
    ResponseResult userFollow(BehaviorDto behaviorDto);
}
