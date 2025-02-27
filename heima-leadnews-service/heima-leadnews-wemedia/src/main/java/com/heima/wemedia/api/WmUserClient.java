package com.heima.wemedia.api;

import com.heima.api.wemedia.IWmUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 16:46
 */
@RestController
public class WmUserClient implements IWmUserClient {

    @Autowired
    private WmUserService wmUserService;

    @Override
    @GetMapping("/wmUser/{id}")
    public ResponseResult getWmUserInfo(@PathVariable int id) {
        return wmUserService.getWmUserInfo(id);
    }

}
