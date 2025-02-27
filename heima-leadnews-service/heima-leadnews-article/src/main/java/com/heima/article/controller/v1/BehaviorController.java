package com.heima.wemedia.controller.v1;

import com.heima.article.service.ApBehaviorService;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 20:39
 */
@RestController
@RequestMapping("/api/v1")
public class BehaviorController {

    @Autowired
    private ApBehaviorService apBehaviorService;

    @PostMapping("/collection_behavior")
    public ResponseResult collectionBehavior(@RequestBody BehaviorDto behaviorDto) {
        return apBehaviorService.collectionBehavior(behaviorDto);
    }

}