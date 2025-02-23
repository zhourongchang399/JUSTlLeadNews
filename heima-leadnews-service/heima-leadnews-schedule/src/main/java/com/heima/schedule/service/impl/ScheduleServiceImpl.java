package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ScheduleConstants;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskInfoLogMapper;
import com.heima.schedule.mapper.TaskInfoMapper;
import com.heima.schedule.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/22 14:58
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TaskInfoLogMapper taskInfoLogMapper;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    @Transactional
    public void addTask(Task task) {
        // 参数校验
        if (task == null) {
            throw new IllegalArgumentException("task is null");
        }

        // 添加任务到DB
        long taskId = addToDB(task);
        task.setTaskId(taskId);

        // 添加任务到Redis
        addToRedis(task);

    }

    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        // 删除DB中的taskInfo并更新tasklog
        TaskinfoLogs taskinfoLogs = deleteAndUpdateTask(taskId, ScheduleConstants.CANCELLED_STATUS);

        // 删除Redis中的task
        deleteFromRedis(taskinfoLogs);
    }

    @Override
    public List<Task> pullTask(Integer type, Integer priority) {
        List<Task> tasks = null;
        String key = type + "_" + priority;
        // 从list中取出对应类型和优先级的队列
        List<String> range = redisTemplate.opsForList().range(ScheduleConstants.CURRENT + key, 0, -1);
        for (String s : range) {
            Task task = JSON.parseObject(s, Task.class);
            tasks.add(task);
            // 从taskInfo中删除对应的task并更新taskInfoLog
            deleteAndUpdateTask(task.getTaskId(), ScheduleConstants.EXECUTED_STATUS);
        }
        return tasks;
    }

    private void deleteFromRedis(TaskinfoLogs taskinfoLogs) {
        // 构造时间参数
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime executeTime = LocalDateTime.ofInstant(taskinfoLogs.getExecuteTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime nextExecuteTime = executeTime.plusMinutes(5);
        // 构造key
        String key = taskinfoLogs.getTaskType() + "_" + taskinfoLogs.getPriority();
        // 构造Task
        Task task = new Task();
        BeanUtils.copyProperties(taskinfoLogs, task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().toInstant().toEpochMilli());
        // 如果执行时间 lt 当前时间 则从list中删除
        if (executeTime.isBefore(currentTime)) {
            redisTemplate
                    .opsForList()
                    .remove(ScheduleConstants.CURRENT + key, 0, JSON.toJSONString(task));
        } else if (executeTime.isAfter(currentTime) && executeTime.isBefore(nextExecuteTime)) {
            // 否则如果执行时间 lt 当前时间 + 预设时间 则从zset中删除
            redisTemplate
                    .opsForZSet()
                    .remove(ScheduleConstants.FUTURE + key,JSON.toJSONString(task));
        }
    }

    private TaskinfoLogs deleteAndUpdateTask(Long taskId, Integer status) {
       //  删除DB中的taskInfo
        taskInfoMapper.deleteById(taskId);

        // 查询taskInfoLog根据Id
        TaskinfoLogs taskinfoLogs = taskInfoLogMapper.getById(taskId);
        taskinfoLogs.setStatus(status);
        // 更新taskInfolog
        taskInfoLogMapper.update(taskinfoLogs);

        return taskinfoLogs;
    }

    private void addToRedis(Task task) {

        // 获取当前，执行和预设时间
        Instant instant = Instant.ofEpochMilli(task.getExecuteTime());
        LocalDateTime executeTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime nextExecuteTime = currentTime.plusMinutes(5);

        // 构造key
        StringJoiner stringJoiner = new StringJoiner("_");
        stringJoiner.add(String.valueOf(task.getTaskType()));
        stringJoiner.add(String.valueOf(task.getPriority()));

        // 如果执行时间 lt 当前时间则存入redis的list队列中
        if (executeTime.isBefore(currentTime)) {
            redisTemplate
                    .opsForList()
                    .leftPush(ScheduleConstants.CURRENT + stringJoiner.toString(), JSON.toJSONString(task));
        } else if (executeTime.isAfter(currentTime) && executeTime.isBefore(nextExecuteTime)){
            // 如果执行时间 lt 当前时间 + 预设时间则存入redis的zset中
            redisTemplate
                    .opsForZSet()
                    .add(ScheduleConstants.FUTURE + stringJoiner.toString(), JSON.toJSONString(task), task.getExecuteTime());
        }

    }

    private long addToDB(Task task) {

        // 参数拷贝
        Taskinfo taskInfo = new Taskinfo();
        BeanUtils.copyProperties(task, taskInfo);
        taskInfo.setExecuteTime(new Date(task.getExecuteTime()));
        // 保存任务信息
        taskInfoMapper.insert(taskInfo);

        // 参数拷贝
        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        BeanUtils.copyProperties(task, taskinfoLogs);
        taskinfoLogs.setVersion(ScheduleConstants.INIT_VERSION);
        taskinfoLogs.setStatus(ScheduleConstants.INIT_VERSION);
        taskinfoLogs.setExecuteTime(new Date(task.getExecuteTime()));
        taskinfoLogs.setTaskId(taskInfo.getTaskId());
        // 保存任务日志信息
        taskInfoLogMapper.insert(taskinfoLogs);

        return taskInfo.getTaskId();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateFromDBToRedis() throws IOException {
        log.info("执行数据库定时更新到redis任务！！！");
        // 匹配Redis中的keys
        Set<String> scanKeys = scanKeys(ScheduleConstants.CURRENT + "*");
        Set<String> scanKeysFuture = scanKeys(ScheduleConstants.FUTURE + "*");
        scanKeys.addAll(scanKeysFuture);
        log.info("match keys:{}", scanKeys);

        // 清除Redis中的数据
        if (!scanKeys.isEmpty()) {
            redisTemplate.delete(scanKeys);
        }

        // 从DB中查询当前时间后五分钟内需要执行的task
        List<Taskinfo> taskinfos = taskInfoMapper.listByTime(LocalDateTime.now().plusMinutes(5));

        // 向Redis中插入准备执行的task
        if (taskinfos.size() > 0) {
            for (Taskinfo taskinfo : taskinfos) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().toInstant().toEpochMilli());
                addToRedis(task);
            }
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateFromFutureToCurrent() throws IOException {
        log.info("执行从ZSet更新到List的定时任务！！！");
        // 从ZSet中获取预执行任务的key
        Set<String> scanKeys = scanKeys(ScheduleConstants.FUTURE + "*");

        if (!scanKeys.isEmpty()) {
            for (String key : scanKeys) {
                // 从ZSet中获取执行时间在当前时间之前的任务
                Set<String> paperToExecTask  = redisTemplate.opsForZSet().rangeByScore(key, 0, LocalDateTime
                        .now()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli());

                // 通过pipeline执行插入和移除操作
                if (!paperToExecTask.isEmpty()) {
                    log.info("Processing key: {}, tasks to move: {}", key, paperToExecTask.size());
                    execPipeline(key, paperToExecTask);
                }
            }
        }

    }

    private void execPipeline(String key, Set<String> paperToExecTask) {
        try {
            redisTemplate.executePipelined((RedisCallback<?>) redisConnection -> {
                for (String task : paperToExecTask) {
                    // 更新到List中
                    String current_key = ScheduleConstants.CURRENT + key.split(ScheduleConstants.FUTURE)[1];
                    redisConnection.lPush(current_key.getBytes(StandardCharsets.UTF_8), task.getBytes(StandardCharsets.UTF_8));
                    // 从ZSet中移除
                    Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
                    byte[] taskBytes = jsonSerializer.serialize(task);
                    redisConnection.zRem(key.getBytes(StandardCharsets.UTF_8), taskBytes);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Pipeline execution failed for key: {}", key, e);
            // 可以根据需要重试或抛出异常
        }
    }

    private Set<String> scanKeys(String pattern) throws IOException {
        Set<String> keys = new HashSet<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).build();
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        Cursor<byte[]> cursor = connection.scan(scanOptions);

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            keys.add(key);
        }

        return keys;
    }

}
