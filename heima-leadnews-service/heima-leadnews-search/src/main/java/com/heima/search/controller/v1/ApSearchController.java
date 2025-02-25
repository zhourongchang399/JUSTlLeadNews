package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.search.service.ApSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
