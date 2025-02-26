package com.heima.wemedia.controller.v1;

import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 16:48
 */
@RestController
@RequestMapping("/api/v1/sensitive")
public class SensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody SensitiveDto sensitiveDto) {
        return wmSensitiveService.list(sensitiveDto);
    }

    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.save(wmSensitive);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable Long id) {
        return wmSensitiveService.delete(id);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.update(wmSensitive);
    }

}
