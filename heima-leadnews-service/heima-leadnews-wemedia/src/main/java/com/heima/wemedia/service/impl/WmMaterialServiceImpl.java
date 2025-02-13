package com.heima.wemedia.service.impl;

import com.heima.aliyunOSS.service.AliOssService;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/13 15:53
 */
@Service
public class WmMaterialServiceImpl implements WmMaterialService {

    @Autowired
    AliOssService aliOssService;

    @Autowired
    WmMaterialMapper wmMaterialMapper;

    final static short COLLECTION = 1;
    final static short NOT_COLLECTION = 0;
    final static short PICTURE = 0;
    final static short VIDEO = 1;
    final static String ROUTE = "weMedia/picture/";

    @Override
    public ResponseResult uploadMaterial(MultipartFile file) {
        // 校验参数
        if (file.isEmpty() || WmThreadLocalUtil.getCurrentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 上传文件
        String url = null;
        try {
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString();
            // 获取文件类型
            String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            // 上传文件到AliYunOSS
            url = aliOssService.upload(file.getBytes(), ROUTE + fileName + fileType);
        } catch (IOException e) {
            // 抛出异常
            throw new CustomException(AppHttpCodeEnum.UPLOAD_FILE_ERROR);
        }

        // 将新的素材信息存入DB
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setUserId(WmThreadLocalUtil.getCurrentId());
        wmMaterial.setUrl(url);
        wmMaterial.setType(PICTURE);
        wmMaterial.setIsCollection(NOT_COLLECTION);
        wmMaterialMapper.addMaterial(wmMaterial);

        // 返回结果
        return ResponseResult.okResult(wmMaterial);
    }
}
