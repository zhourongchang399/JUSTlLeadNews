package com.heima.user.service;

import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApUserAuthService {
    ResponseResult list(AuthDto authDto);
}
