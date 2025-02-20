package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.api.article.IArticleClient;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmAutoScanService;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    IArticleClient iArticleClient;

    @Override
    public void scanNews(Integer id) {
        // 参数校验
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 判断文章是否存在
        WmNews wmNews = wmNewsMapper.getById(id);
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.NOT_EXIST_NEWS);
        }

        // 提取文章图文信息
        List<List<String>> textAndImages = getTextAndImages(wmNews);

        // TODO 文章文本内容审核
        Short status = WemediaConstants.WM_NEWS_PASS_CHECK;
        String reason = WemediaConstants.CHECK_SUCCEED;

        // TODO 文章图片内容审核

        // 调用APP端保存审核通过的文章
        Long articleId = null;
        if (true) {
            articleId = saveApArticle(wmNews);
        }

        // 更新文章审核信息
        updateNewsCheckInfo(id, status, reason, articleId);

    }

    private void updateNewsCheckInfo(Integer id, Short status, String reason, Long articleId) {
        WmNews needToUpadte = new WmNews();
        needToUpadte.setId(id);
        needToUpadte.setStatus(status);
        needToUpadte.setReason(reason);
        // 设置回调的文章ID
        needToUpadte.setArticleId(articleId);
        wmNewsMapper.updateNews(needToUpadte);
    }

    private Long saveApArticle(WmNews wmNews) {
        // 参数拷贝
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);

        // 布局
        articleDto.setLayout(wmNews.getType());

        // 作者
        WmUser user = wmUserMapper.getByCondition(new WmUser(wmNews.getUserId()));
        articleDto.setAuthorId(Long.valueOf(user.getId()));
        articleDto.setAuthorName(user.getName());

        // 频道
        WmChannel wmChannel = wmChannelMapper.getById(wmNews.getChannelId());
        articleDto.setChannelId(wmChannel.getId());
        articleDto.setChannelName(wmChannel.getName());

        // 向APP端发起请求
        ResponseResult responseResult = iArticleClient.saveArticle(articleDto);
        if (!responseResult.getCode().equals(200)) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        // 回调文章ID
        return (Long) responseResult.getData();
    }

    private List<List<String>> getTextAndImages(WmNews wmNews) {
        List<Map> contentList = JSON.parseArray(wmNews.getContent(), Map.class);
        // 提取文章图文内容
        List<String> newsTextContentList = new ArrayList<>();
        List<String> newsImagesContentList = new ArrayList<>();
        for (Map map : contentList) {
            if (map.get("type").equals("text")) {
                // 提取文章文本内容
                String newsTextContent = map.get("value").toString();
                newsTextContentList.add(newsTextContent);
            } else if (map.get("type").equals("image")) {
                // 提取文章图片内容
                String newsTextContent = map.get("value").toString();
                newsImagesContentList.add(newsTextContent);
            }
        }
        // 提取封面图片内容
        String images = wmNews.getImages();
        List<String> newsCoverImages = Arrays.asList(images.split(","));
        List<String> newsImagesList = Stream.concat(newsImagesContentList.stream(), newsCoverImages.stream()).collect(Collectors.toList());

        List<List<String>> resultList = new ArrayList<>();
        resultList.add(newsTextContentList);
        resultList.add(newsImagesList);
        return resultList;
    }

}
