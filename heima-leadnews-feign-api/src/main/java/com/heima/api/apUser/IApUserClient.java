package com.heima.api.apUser;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "leadnews-user")
public interface IApUserClient {

    @GetMapping("/behavior")
    ResponseResult behavior(@RequestParam long authorId, @RequestParam long userId);

}
