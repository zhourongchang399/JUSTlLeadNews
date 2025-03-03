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

    /**
     * @author: Zc
     * @description: 获取申请用户信息
     * @date: 2025/3/3 21:41
     * @param null
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody AuthDto authDto) {
        return apUserAuthService.list(authDto);
    }

    /**
     * @author: Zc
     * @description: 通过申请用户
     * @date: 2025/3/3 21:42
     * @param null
     * @return
     */
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto authDto) {
        return apUserAuthService.authFial(authDto);
    }

    /**
     * @author: Zc
     * @description: 驳回申请用户
     * @date: 2025/3/3 21:42
     * @param null
     * @return
     */
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto authDto) {
        return apUserAuthService.authPass(authDto);
    }

}
