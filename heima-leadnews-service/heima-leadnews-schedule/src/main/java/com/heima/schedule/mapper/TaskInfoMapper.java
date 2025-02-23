package com.heima.schedule.mapper;

import com.heima.model.schedule.pojos.Taskinfo;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskInfoMapper {
    void insert(Taskinfo taskInfo);

    void deleteById(Long taskId);

    List<Taskinfo> listByTime(LocalDateTime time);
}
