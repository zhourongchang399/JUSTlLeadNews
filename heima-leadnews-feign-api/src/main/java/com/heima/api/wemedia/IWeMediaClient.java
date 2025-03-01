package com.heima.api.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "leadnews-wemedia")
public interface IWeMediaClient {

    @GetMapping("/wmUser/{id}")
    ResponseResult getWmUserInfo(@PathVariable int id);

    @GetMapping("/wmChannel/getAllChannel")
    public ResponseResult getWmChannel();

}
