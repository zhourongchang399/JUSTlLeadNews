package com.heima.mapper;

import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdUserMapper {
    AdUser selectOne(@Param("adUser") AdUser adUser);

    void update(@Param("adUser") AdUser selectedUser);
}
