package com.heima.wemedia.service;

import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;

import java.util.List;
import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/21 15:29
 */
public interface WmSensitiveService {

    Map<String, Integer> textCheck(String text);

    ResponseResult list(SensitiveDto sensitiveDto);

    ResponseResult save(WmSensitive wmSensitive);

    ResponseResult delete(Long id);

    ResponseResult update(WmSensitive wmSensitive);
}
