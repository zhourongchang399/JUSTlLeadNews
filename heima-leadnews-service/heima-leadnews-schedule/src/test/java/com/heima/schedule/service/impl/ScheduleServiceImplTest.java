package com.heima.schedule.service.impl;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
class ScheduleServiceImplTest {

    @Autowired
    private ScheduleService scheduleService;

    @Test
    void addTask() {
        Task task = new Task();
        task.setTaskType(0);
        task.setParameters("10分钟后".getBytes(StandardCharsets.UTF_8));
        task.setPriority(1);
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(10);
        task.setExecuteTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        scheduleService.addTask(task);
    }
}