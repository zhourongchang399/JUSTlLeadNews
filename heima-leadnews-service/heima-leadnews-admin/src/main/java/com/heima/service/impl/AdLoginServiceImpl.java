package com.heima.service.impl;

import com.heima.common.exception.CustomException;
import com.heima.mapper.AdUserMapper;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.service.AdLoginService;
import com.heima.utils.common.MD5Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 15:48
 */
@Service
public class AdLoginServiceImpl implements AdLoginService {

    @Autowired
    private AdUserMapper adUserMapper;

    @Override
    public ResponseResult adminLogin(AdUserDto adUserDto) {
        // 参数校验
        if (adUserDto == null || adUserDto.getName() == null || adUserDto.getPassword() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 参数拷贝
        AdUser adUser = new AdUser();
        BeanUtils.copyProperties(adUserDto, adUser);

        // 查询该用户在数据库中是否存在
        AdUser selectedUser = adUserMapper.selectOne(adUser);
        if (selectedUser == null) {
            // 不存在
            throw new CustomException(AppHttpCodeEnum.NOT_EXIST_USER);
        }

        // 存在则判断用户密码是否正确
        // password加盐并进行md5加密
        String passwordMd5 = MD5Utils.encodeWithSalt(adUserDto.getPassword(), selectedUser.getSalt());

        // 与数据库中密码进行比对
        if (!passwordMd5.equals(selectedUser.getPassword())) {
            // 密码错误
            throw new CustomException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 密码正确，更新最近一次登录时间
        selectedUser.setLoginTime(new Date());
        adUserMapper.update(selectedUser);

        return ResponseResult.okResult(selectedUser);
    }

}
