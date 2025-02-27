package com.heima.article.service;

import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApBehaviorService {
    ResponseResult collectionBehavior(BehaviorDto behaviorDto);
}
