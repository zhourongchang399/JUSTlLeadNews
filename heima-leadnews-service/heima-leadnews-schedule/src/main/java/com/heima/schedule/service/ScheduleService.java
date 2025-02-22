package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

public interface ScheduleService {

    public void addTask(Task task);

    public void cancelTask(Long taskId);

}
