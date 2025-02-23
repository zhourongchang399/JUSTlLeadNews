package com.heima.schedule.api;

import com.heima.api.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/23 18:13
 */
@RestController
public class ScheduleClient implements IScheduleClient {

    private static final Logger log = LoggerFactory.getLogger(ScheduleClient.class);
    @Autowired
    private ScheduleService scheduleService;

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task) {
        log.info("add task: {}", task);
        scheduleService.addTask(task);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return 取消结果
     */
    @GetMapping("/api/v1/task/cancel/{taskId}")
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId) {
        scheduleService.cancelTask(taskId);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @GetMapping("/api/v1/task/poll/{type}/{priority}")
    public ResponseResult pull(@PathVariable("type") int type, @PathVariable("priority") int priority) {
        log.info("pull task type: {}, priority: {}", type, priority);
        List<Task> tasks = scheduleService.pullTask(type, priority);
        log.info("pull tasks: {}", tasks);
        return ResponseResult.okResult(tasks);
    }

}
