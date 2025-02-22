package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

import java.util.List;

public interface ScheduleService {

    public void addTask(Task task);

    public void cancelTask(Long taskId);

    public List<Task> pullTask(Integer type, Integer priority);
}
