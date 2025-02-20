package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmAutoScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/20 14:55
 */
@Service
public class WmAutoScanServiceImpl implements WmAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Override
    public void scanNews(WmNewsDto wmNewsDto) {
        // 参数校验
        if (wmNewsDto == null || wmNewsDto.getId() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 判断文章是否存在
        WmNews wmNews = wmNewsMapper.getById(wmNewsDto.getId());
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.NOT_EXIST_NEWS);
        }

        List<Map> contentList = JSON.parseArray(wmNewsDto.getContent(), Map.class);
        // 提取文章文本内容
        List<String> newsTextContentList = new ArrayList<>();
        for (Map map : contentList) {
            if (map.get("type").equals("text")) {
                String newsTextContent = map.get("value").toString();
                newsTextContentList.add(newsTextContent);
            }
        }

        // TODO 文章文本内容审核

        // 提取文章图片内容
        List<String> newsImagesContentList = new ArrayList<>();
        for (Map map : contentList) {
            if (map.get("type").equals("image")) {
                String newsTextContent = map.get("value").toString();
                newsImagesContentList.add(newsTextContent);
            }
        }
        List<String> newsCoverImages = wmNewsDto.getImages();
        List<String> newsImagesList = Stream.concat(newsImagesContentList.stream(), newsCoverImages.stream()).collect(Collectors.toList());

        // TODO 文章图片内容审核

        // 更新文章审核信息
        WmNews needToUpadte = new WmNews();
        needToUpadte.setId(wmNewsDto.getId());
        needToUpadte.setStatus(WemediaConstants.WM_NEWS_PASS_CHECK);
        wmNewsMapper.updateNews(needToUpadte);

        // TODO 调用APP端保存审核通过的文章

    }

}
