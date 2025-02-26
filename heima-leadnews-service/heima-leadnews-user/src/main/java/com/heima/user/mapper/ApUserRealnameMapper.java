package com.heima.user.mapper;

import com.github.pagehelper.Page;
import com.heima.model.admin.pojos.ApUserRealname;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 22:43
 */
@Mapper
public interface ApUserRealnameMapper {

    Page<ApUserRealname> list(ApUserRealname apUserRealname);

    ApUserRealname getById(int id);

    void update(ApUserRealname apUserRealname);
}
