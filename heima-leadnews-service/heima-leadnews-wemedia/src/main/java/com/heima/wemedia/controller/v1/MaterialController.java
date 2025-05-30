package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/13 15:44
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/material")
public class MaterialController {

    @Autowired
    WmMaterialService wmMaterialService;

    @PostMapping("/upload_picture")
    public ResponseResult uploadMaterial(@RequestParam("multipartFile") MultipartFile file) {
        return wmMaterialService.uploadMaterial(file);
    }

    @PostMapping("/list")
    public ResponseResult getMaterialList(@RequestBody WmMaterialDto wmMaterialDto) {
        return wmMaterialService.getMaterialList(wmMaterialDto);
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable Integer id) {
        return wmMaterialService.cancelCollect(id);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collectMaterial(@PathVariable Integer id) {
        return wmMaterialService.collectMaterial(id);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult delPicture(@PathVariable Integer id) {
        return wmMaterialService.deletePicture(id);
    }

}
