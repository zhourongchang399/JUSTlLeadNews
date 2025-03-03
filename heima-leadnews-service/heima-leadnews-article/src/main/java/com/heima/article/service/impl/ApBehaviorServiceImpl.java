package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.service.ApBehaviorService;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.kafka.KafkaService;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.utils.common.WmThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 20:41
 */
@Service
public class ApBehaviorServiceImpl implements ApBehaviorService {

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    @Autowired
    private KafkaService kafkaService;

    /**
     * @author: Zc
     * @description: 文章收藏
     * @date: 2025/3/3 21:36
     * @param null
     * @return
     */
    @Override
    @Transactional
    public ResponseResult collectionBehavior(BehaviorDto behaviorDto) {
        // 参数校验
        if (behaviorDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取用户Id
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断当前用户是否登录
        if (userId == null) {
            return ResponseResult.okResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 封装消息对象
        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setArticleId(behaviorDto.getArticleId());
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.COLLECTION);

        // 收藏
        if (behaviorDto.getOperation() == 0) {
            // 封装对象
            ApCollection apCollection = new ApCollection();
            apCollection.setType(behaviorDto.getType());
            apCollection.setUserId(userId);
            apCollection.setEntryId(behaviorDto.getEntryId());
            apCollection.setPublishedTime(behaviorDto.getPublishedTime());
            apCollection.setCollectionTime(LocalDateTime.now());
            // 新增收藏对象
            apCollectionMapper.insert(apCollection);
            // 收藏数量
            updateArticleMess.setAdd(1);
        } else if (behaviorDto.getOperation() == 1) {
            // 取消收藏
            QueryWrapper<ApCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("entry_id", behaviorDto.getEntryId());
            // 删除收藏对象
            apCollectionMapper.delete(queryWrapper);
            // 收藏数量
            updateArticleMess.setAdd(-1);
        }

        // 发送消息到kafka以实时计算分数
        kafkaService.sendAsync(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC, JSON.toJSONString(updateArticleMess));

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}
