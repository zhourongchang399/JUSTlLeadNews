package com.heima.schedule.mapper;

import com.heima.model.schedule.pojos.Taskinfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskInfoMapper {
    void insert(Taskinfo taskInfo);

    void deleteById(Long taskId);
}
