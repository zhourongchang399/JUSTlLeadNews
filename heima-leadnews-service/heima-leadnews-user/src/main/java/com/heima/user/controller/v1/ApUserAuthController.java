package com.heima.user.controller.v1;

import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 22:33
 */

@RestController
@RequestMapping("/api/v1/auth")
public class ApUserAuthController {

    @Autowired
    private ApUserAuthService apUserAuthService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody AuthDto authDto) {
        return apUserAuthService.list(authDto);
    }

    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto authDto) {
        return apUserAuthService.authFial(authDto);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto authDto) {
        return apUserAuthService.authPass(authDto);
    }

}
