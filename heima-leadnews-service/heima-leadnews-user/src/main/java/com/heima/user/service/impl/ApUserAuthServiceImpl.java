package com.heima.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.admin.pojos.ApUserRealname;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmNewsVo;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 22:40
 */
@Service
public class ApUserAuthServiceImpl implements ApUserAuthService {

    @Autowired
    private ApUserRealnameMapper apUserRealnameMapper;

    @Override
    public ResponseResult list(AuthDto authDto) {
        // 参数校验
        if (authDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 设置size的上下限
        if (authDto.getSize() > 20) {
            authDto.setSize(20);
        } else if (authDto.getSize() < 5) {
            authDto.setSize(5);
        }

        // 设置page的下限
        if (authDto.getPage() <= 0) {
            authDto.setPage(1);
        }

        // 封装对象
        ApUserRealname apUserRealname = new ApUserRealname();
        BeanUtils.copyProperties(authDto, apUserRealname);

        // 开始分页
        PageHelper.startPage(authDto.getPage(), authDto.getSize());

        // 开始查询
        Page<ApUserRealname> apUserRealnamePage = apUserRealnameMapper.list(apUserRealname);

        // 封装结果
        PageResponseResult pageResponseResult = new PageResponseResult();
        pageResponseResult.setCurrentPage(apUserRealnamePage.getPageNum());
        pageResponseResult.setTotal((int) apUserRealnamePage.getTotal());
        pageResponseResult.setData(apUserRealnamePage.getResult());
        pageResponseResult.setSize(apUserRealnamePage.getPageSize());

        // 返回结果
        return pageResponseResult;
    }
}
