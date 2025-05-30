package com.heima.wemedia.service;

//import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;

public interface WmUserService{

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

    void insert(WmUser wmUser);

    ResponseResult getWmUserInfo(int id);

}