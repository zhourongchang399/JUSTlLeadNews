package com.heima.wemedia.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.aliyunOSS.service.AliOssService;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
@Slf4j
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
    final static Integer FIRST_PAGE = 1;
    final static Integer PAGE_SIZE = 20;

    @Override
    @Transactional
    public ResponseResult uploadMaterial(MultipartFile file) {
        // 校验参数
        if (file == null || file.isEmpty() || WmThreadLocalUtil.getCurrentId() == null) {
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

    @Override
    @Transactional
    public ResponseResult getMaterialList(WmMaterialDto wmMaterialDto) {
        // 校验参数
        if (wmMaterialDto.getPage() == null || wmMaterialDto.getPage() < 1) {
            wmMaterialDto.setPage(FIRST_PAGE);
        }
        if (wmMaterialDto.getSize() == null || wmMaterialDto.getSize() < 1) {
            wmMaterialDto.setSize(PAGE_SIZE);
        }

        // 获取当前用户
        Integer id = WmThreadLocalUtil.getCurrentId();
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }

        // 开始分页
        PageHelper.startPage(wmMaterialDto.getPage(), wmMaterialDto.getSize());

        // 查询数据库
        if (wmMaterialDto.getIsCollection() == NOT_COLLECTION) {
            wmMaterialDto.setIsCollection(null);
        }
        Page<WmMaterial> wmMaterialPage = wmMaterialMapper.listQuery(id, wmMaterialDto);

        // 输出查询结果的总数
        log.info("total: {}", wmMaterialPage.getTotal());

        // 封装分页结果
        PageResponseResult pageResponseResult = new PageResponseResult(wmMaterialDto.getPage(), wmMaterialDto.getSize(), (int) wmMaterialPage.getTotal());
        pageResponseResult.setData(wmMaterialPage.getResult()); // 返回分页数据
        pageResponseResult.setCode(AppHttpCodeEnum.SUCCESS.getCode());
        // 返回成功结果
        return pageResponseResult;

    }

    @Override
    @Transactional
    public ResponseResult cancelCollect(Integer id) {
        // 参数校验
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 更新素材信息
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setIsCollection(WemediaConstants.CANCEL_COLLECT_MATERIAL);
        wmMaterial.setId(id);
        wmMaterialMapper.updateMaterial(wmMaterial);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult collectMaterial(Integer id) {
        // 参数校验
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 更新素材信息
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setIsCollection(WemediaConstants.COLLECT_MATERIAL);
        wmMaterial.setId(id);
        wmMaterialMapper.updateMaterial(wmMaterial);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    @Transactional
    public ResponseResult deletePicture(Integer id) {
        // 参数校验
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 删除数据
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setId(id);
        wmMaterialMapper.deleteMaterial(wmMaterial);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
