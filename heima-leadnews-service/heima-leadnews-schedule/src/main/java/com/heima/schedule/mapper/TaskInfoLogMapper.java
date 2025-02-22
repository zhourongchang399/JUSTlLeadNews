package com.heima.schedule.mapper;

import com.heima.model.schedule.pojos.TaskinfoLogs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskInfoLogMapper {
    void insert(TaskinfoLogs taskinfoLogs);

    TaskinfoLogs getById(Long taskId);

    void update(TaskinfoLogs taskinfoLogs);
}
