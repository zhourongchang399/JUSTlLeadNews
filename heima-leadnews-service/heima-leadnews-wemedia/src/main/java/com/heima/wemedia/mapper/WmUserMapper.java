package com.heima.wemedia.mapper;

//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.pojos.WmUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WmUserMapper{
    WmUser getByCondition(WmUser wmUser);

    void insert(WmUser wmUser);
}