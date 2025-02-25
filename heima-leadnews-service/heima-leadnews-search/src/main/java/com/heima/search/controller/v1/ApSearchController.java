package com.heima.search.controller.v1;

import com.alibaba.fastjson.JSON;
import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.article.pojos.ApUserSearch;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.search.service.ApSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 14:49
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
public class ApSearchController {

    @Autowired
    private ApSearchService apSearchService;

    @GetMapping("/{mapping}")
    public ResponseResult getMappingInfo(@PathVariable("mapping") String mapping) throws IOException {
        apSearchService.getMappingInfo(mapping);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @GetMapping("/init/{mapping}")
    public ResponseResult initMapping(@PathVariable("mapping") String mapping) throws IOException {
        apSearchService.initMapping(mapping);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto dto) throws IOException {
        return ResponseResult.okResult(JSON.toJSONString(apSearchService.search(dto)));
    }

}
