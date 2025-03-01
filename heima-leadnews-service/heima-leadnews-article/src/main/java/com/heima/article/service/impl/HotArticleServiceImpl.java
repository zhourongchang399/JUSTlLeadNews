package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.api.wemedia.IWeMediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.ApHotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：Zc
 * @description：热门文章服务
 * @date ：2025/3/1 17:15
 */
@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private IWeMediaClient weMediaClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @author: Zc
     * @description: 计算热门文章并存入redis中
     * @date: 2025/3/1 17:19
     * @param null
     * @return
     */
    @Override
    public ResponseResult searchFiveDayHotArticle() {
        // 获取近30天的全部文章的行为信息
        Date date = new Date(Instant.now().minusSeconds(3600 * 24 * 30).toEpochMilli());
        List<ApArticle> apArticles = apArticleMapper.findAll(date);
        log.info(String.valueOf(apArticles.size()));

        // 获取全部频道信息
        ResponseResult responseResult = weMediaClient.getWmChannel();
        String result = String.valueOf(responseResult.getData());
        List<WmChannel> wmChannelList = null;
        if (responseResult.getCode() == 200 && responseResult.getData() != null) {
            wmChannelList = JSON.parseArray(result, WmChannel.class);
            log.info(String.valueOf(wmChannelList.size()));
        }

        // 计算每个文章的分数
        List<ApHotArticleVo> apHotArticleVos = new ArrayList<>();
        for (ApArticle apArticle : apArticles) {
            ApHotArticleVo apHotArticleVo = new ApHotArticleVo();
            BeanUtils.copyProperties(apArticle, apHotArticleVo);
            apHotArticleVo.setScore(computeScoreForArticle(apArticle));
            apHotArticleVos.add(apHotArticleVo);
        }

        // 计算每个频道的热门文章（前30）
        for (WmChannel channel : wmChannelList) {
            List<ApHotArticleVo> hotArticle4channel = apHotArticleVos.stream()
                    .filter(a -> a.getChannelId() == channel.getId())
                    .sorted(Comparator.comparing(ApHotArticleVo::getScore).reversed())
                    .limit(30)
                    .collect(Collectors.toList());

            // 存入redis中
            if (hotArticle4channel != null && hotArticle4channel.size() > 0) {
                saveHotArticlePreChannelToRedis(hotArticle4channel, hotArticle4channel.get(0).getChannelName());
            }
        }

        // 计算推荐频道的热门文章（全部文章的前30）
        List<ApHotArticleVo> hotArticle4All = apHotArticleVos.stream()
                .sorted(Comparator.comparing(ApHotArticleVo::getScore).reversed())
                .limit(30)
                .collect(Collectors.toList());

        // 存入redis中
        saveHotArticlePreChannelToRedis(hotArticle4All, ArticleConstants.DEFAULT_TAG);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * @author: Zc
     * @description: 将热门文章数据存入redis中
     * @date: 2025/3/1 17:48
     * @param null
     * @return
     */
    private void saveHotArticlePreChannelToRedis(List<ApHotArticleVo> hotArticle4channel, String channel) {
        // 参数校验
        if (hotArticle4channel == null || hotArticle4channel.size() == 0) {
            return;
        }

        // 将热门文章数据存入redis中
        String string = JSON.toJSONString(hotArticle4channel);
        redisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channel, string);
    }

    /**
     * @author: Zc
     * @description: 计算文章的分数
     * @date: 2025/3/1 17:49
     * @param null
     * @return
     */
    public long computeScoreForArticle(ApArticle apArticle) {
        long score = 0;

        if (apArticle.getLikes() != null) {
            score += (long) apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getComment() != null) {
            score += (long) apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getViews() != null) {
            score += (long) apArticle.getViews() * ArticleConstants.HOT_ARTICLE_VIEW_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            score += (long) apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return score;

    }

}
