package com.heima.api.schedule.fallback;

import com.heima.api.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dtos.Task;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/23 18:34
 */
public class IScheduleClientFallBack implements IScheduleClient {
    @Override
    public ResponseResult addTask(Task task) {
        return ResponseResult.okResult(AppHttpCodeEnum.FAIL_PUBLISH_NEWS);
    }

    @Override
    public ResponseResult cancelTask(long taskId) {
        return ResponseResult.okResult(AppHttpCodeEnum.FAIL_PUBLISH_NEWS);
    }

    @Override
    public ResponseResult poll(int type, int priority) {
        return ResponseResult.okResult(AppHttpCodeEnum.FAIL_PUBLISH_NEWS);
    }
}
