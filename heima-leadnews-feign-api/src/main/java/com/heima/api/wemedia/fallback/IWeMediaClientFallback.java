package com.heima.api.wemedia.fallback;

import com.heima.api.wemedia.IWeMediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 16:45
 */
@Component
public class IWeMediaClientFallback implements IWeMediaClient {

    @Override
    public ResponseResult getWmUserInfo(int id) {
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult getWmChannel() {
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
