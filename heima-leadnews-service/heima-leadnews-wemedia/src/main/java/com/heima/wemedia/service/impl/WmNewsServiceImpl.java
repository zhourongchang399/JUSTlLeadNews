package com.heima.wemedia.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/14 0:55
 */
@Service
public class WmNewsServiceImpl implements WmNewsService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    final static Integer DEFAULT_PAGE_SIZE = 20;
    final static Integer DEFAULT_PAGE_NUM = 1;

    @Override
    public ResponseResult listNewsByConditions(WmNewsPageReqDto wmNewsPageReqDto) {
        // 参数校验
        if (wmNewsPageReqDto.getSize() == null || wmNewsPageReqDto.getSize() == 0) {
            wmNewsPageReqDto.setSize(DEFAULT_PAGE_SIZE);
        }
        if (wmNewsPageReqDto.getPage() == null || wmNewsPageReqDto.getPage() <= 0) {
            wmNewsPageReqDto.setPage(DEFAULT_PAGE_NUM);
        }

        // 获取当前用户信息
        Long id = WmThreadLocalUtil.getCurrentId();
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 开始分页
        PageHelper.startPage(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize());

        // 查询数据库
        Page<WmNews> wmNewsPage = wmNewsMapper.listByConditions(id, wmNewsPageReqDto);

        // 整合实体
        ResponseResult responseResult = new PageResponseResult(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize(), (int) wmNewsPage.getTotal());
        responseResult.setData(wmNewsPage.getResult());

        // 返回结果
        return  responseResult;
    }
}
