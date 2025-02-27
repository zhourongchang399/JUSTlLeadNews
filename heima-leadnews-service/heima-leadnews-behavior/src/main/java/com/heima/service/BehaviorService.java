package com.heima.service;

import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 14:20
 */
public interface BehaviorService {
    ResponseResult likesBehavior(BehaviorDto behaviorDto);

    ResponseResult readBehavior(BehaviorDto behaviorDto);

    ResponseResult unLikesBehavior(BehaviorDto behaviorDto);
}
