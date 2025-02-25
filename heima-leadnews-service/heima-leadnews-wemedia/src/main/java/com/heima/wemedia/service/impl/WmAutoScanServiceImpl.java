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
import com.heima.utils.common.OCRUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmAutoScanService;
import com.heima.wemedia.service.WmSensitiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/20 14:55
 */
@Service
@Async
public class WmAutoScanServiceImpl implements WmAutoScanService {

    private static final Logger log = LoggerFactory.getLogger(WmAutoScanServiceImpl.class);
    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmSensitiveService wmSensitiveService;

    @Autowired
    IArticleClient iArticleClient;

    @Autowired
    OCRUtil ocrUtil;

    @Override
    @Transactional
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

        log.info("执行文章自动审核:{}", id);

        // 提取文章图文信息
        List<List<String>> textAndImages = getTextAndImages(wmNews);

        Short status = WemediaConstants.WM_NEWS_PUBLISHED;
        String reason = WemediaConstants.CHECK_SUCCEED;

        // 文章审核
        // 获取文字文本内容
        StringJoiner stringJoiner = new StringJoiner(",");
        textAndImages.get(0).stream().forEach(stringJoiner::add);
        // 通过OCR获取图片内文字
        List<String> scannedText = ocrImages(textAndImages.get(1));
        scannedText.stream().forEach(stringJoiner::add);

        // 文章文字审核
        Map<String, Integer> resultMap = wmSensitiveService.textCheck(stringJoiner.toString());
        if (!resultMap.isEmpty()) {
            status = WemediaConstants.WM_NEWS_FAIL_CHECK;
            reason = WemediaConstants.CHECK_FAIL_SENSITIVE + resultMap.toString();
        }

        // 调用APP端保存审核通过的文章
        Long articleId = null;
        if (status.equals(WemediaConstants.WM_NEWS_PUBLISHED)) {
            articleId = saveApArticle(wmNews);
        }

        // 更新文章审核信息
        updateNewsCheckInfo(id, status, reason, articleId);

    }

    private List<String> ocrImages(List<String> imagesList) {
        List<String> imagesText = new ArrayList<>();
        for (String imageUrl : imagesList) {
            log.info("url:{}", imageUrl);
            try {
                URL url = new URL(imageUrl);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                buffer.flush();
                byte[] imageBytes = buffer.toByteArray();

                BufferedImage imageFile = ImageIO.read(new ByteArrayInputStream(imageBytes));

                if (imageFile != null) {
                    String scannedText = ocrUtil.scanImage(imageFile);
                    imagesText.add(scannedText);
                } else {
                    System.err.println("Failed to read the image.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imagesText;
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

    @Transactional
    public Long saveApArticle(WmNews wmNews) {
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
        if (responseResult != null && responseResult.getCode().equals(200)) {
            log.info("调用app端接口保存文章成功");
            // 发布成功，修改文章状态
            wmNews.setStatus(WemediaConstants.WM_NEWS_PUBLISHED);
            wmNewsMapper.updateNews(wmNews);
        } else {
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
