package com.heima.user.controller.v1;

import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 15:59
 */
@RestController
@RequestMapping("/api/v1/user")
public class ApUserBehaviorController {

    @Autowired
    private ApUserBehaviorService apUserBehaviorService;

    @PostMapping("/user_follow")
    public ResponseResult userFollow(@RequestBody BehaviorDto behaviorDto) {
        return apUserBehaviorService.userFollow(behaviorDto);
    }

}
