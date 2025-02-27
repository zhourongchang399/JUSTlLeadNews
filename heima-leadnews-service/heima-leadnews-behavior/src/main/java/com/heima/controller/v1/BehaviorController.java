package com.heima.controller.v1;

import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.service.BehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 14:17
 */
@RestController
@RequestMapping("/behavior/api/v1")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult likesBehavior(@RequestBody BehaviorDto behaviorDto) {
        return behaviorService.likesBehavior(behaviorDto);
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult unLikesBehavior(@RequestBody BehaviorDto behaviorDto) {
        return behaviorService.unLikesBehavior(behaviorDto);
    }

    @PostMapping("/read_behavior")
    public ResponseResult readBehavior(@RequestBody BehaviorDto behaviorDto) {
        return behaviorService.readBehavior(behaviorDto);
    }

}
