package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseResult uploadMaterial(MultipartFile file) {
        return wmMaterialService.uploadMaterial(file);
    }

}
