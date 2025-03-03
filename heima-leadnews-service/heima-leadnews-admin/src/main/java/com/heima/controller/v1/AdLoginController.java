package com.heima.controller.v1;

import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.service.AdLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 16:10
 */
@RestController
@RequestMapping("/login")
public class AdLoginController {

    @Autowired
    private AdLoginService adLoginService;


    /**
     * @author: Zc
     * @description: 管理端用户登录
     * @date: 2025/3/3 21:43
     * @param null
     * @return
     */
    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdUserDto adUserDto) {
        return adLoginService.adminLogin(adUserDto);
    }
}
