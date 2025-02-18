package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService {
    public ResponseResult uploadMaterial(MultipartFile file);

    ResponseResult getMaterialList(WmMaterialDto wmMaterialDto);

    ResponseResult cancelCollect(Integer id);

    ResponseResult collectMaterial(Integer id);

    ResponseResult deletePicture(Integer id);
}
