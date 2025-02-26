package com.heima.service;

import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AdLoginService {

    public ResponseResult adminLogin(AdUserDto adUserDto);
}
