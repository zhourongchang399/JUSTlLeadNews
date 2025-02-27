package com.heima.user.api;

import com.heima.api.apUser.IApUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 22:11
 */
@RestController
public class ApUserClient implements IApUserClient {

    @Autowired
    private ApUserBehaviorService apUserBehaviorService;

    @Override
    @GetMapping("/behavior")
    public ResponseResult behavior(@RequestParam long authorId, @RequestParam long userId) {
        return apUserBehaviorService.getFollowAndFansInfo(authorId, userId);
    }

}
