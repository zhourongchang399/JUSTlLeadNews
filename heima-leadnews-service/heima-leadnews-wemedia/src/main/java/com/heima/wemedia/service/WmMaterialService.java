package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService {
    public ResponseResult uploadMaterial(MultipartFile file);
}
