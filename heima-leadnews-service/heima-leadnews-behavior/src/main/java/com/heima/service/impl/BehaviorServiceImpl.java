package com.heima.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.kafka.KafkaService;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.behavior.pojos.Behavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.service.BehaviorService;
import com.heima.utils.common.WmThreadLocalUtil;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 14:20
 */
@Service
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public ResponseResult likesBehavior(BehaviorDto behaviorDto) {
        // 参数校验
        if (behaviorDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取用户Id
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断是否登录
        if (userId == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 获取当前文章，当前用户的行为数据
        // 0 文章 1 动态 2 评论
        String name = behaviorDto.getType() + ":" + behaviorDto.getArticleId();
        String key = userId.toString();
        String behavior = (String) redisTemplate.opsForHash().get(name, key);

        // 判断行为数据是否为空
        if (behavior != null) {
            Behavior BehaviorObject = JSON.parseObject(behavior, Behavior.class);
            BehaviorObject.setLike(behaviorDto.getOperation() == BehaviorConstants.LIKE ? BehaviorConstants.LIKE : BehaviorConstants.CHANEL_LIKE);
            // 删除目标数据
            redisTemplate.opsForHash().delete(name, key);
            // 新增行为数据
            redisTemplate.opsForHash().put(name, key, JSON.toJSONString(BehaviorObject));
        }

        // 封装消息对象
        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setArticleId(behaviorDto.getArticleId());
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.LIKES);
        updateArticleMess.setAdd(behaviorDto.getOperation() == BehaviorConstants.LIKE ? 1 : -1);

        // 发送消息到kafka以实时计算分数
        kafkaService.sendAsync(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC,JSON.toJSONString(updateArticleMess));

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult readBehavior(BehaviorDto behaviorDto) {
        // 参数校验
        if (behaviorDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取用户Id
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断是否登录
        if (userId == null) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 获取当前文章，当前用户的行为数据
        String name = "0:" + behaviorDto.getArticleId();
        String key = userId.toString();
        String behavior = (String) redisTemplate.opsForHash().get(name, key);

        // 判断行为数据是否为空
        Behavior behaviorObject = null;
        if (behavior != null) {
            behaviorObject = JSON.parseObject(behavior, Behavior.class);
            behaviorObject.setCount(behaviorObject.getCount() + 1);
            // 删除目标数据
            redisTemplate.opsForHash().delete(name, key);
        } else {
            // 首次阅读,初始化对象
            behaviorObject = new Behavior();
            behaviorObject.setCount(1);
            behaviorObject.setLike(0);
            behaviorObject.setUnlike(0);
        }

        // 新增行为数据
        redisTemplate.opsForHash().put(name, key, JSON.toJSONString(behaviorObject));

        // 封装消息对象
        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setArticleId(behaviorDto.getArticleId());
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.VIEWS);
        updateArticleMess.setAdd(1);

        // 发送消息到kafka以实时计算分数
        kafkaService.sendAsync(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC, JSON.toJSONString(updateArticleMess));

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult unLikesBehavior(BehaviorDto behaviorDto) {
        // 参数校验
        if (behaviorDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取用户Id
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断是否登录
        if (userId == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 获取当前文章，当前用户的行为数据
        // 0 文章 1 动态 2 评论
        String name = "0:" + behaviorDto.getArticleId();
        String key = userId.toString();
        String behavior = (String) redisTemplate.opsForHash().get(name, key);

        // 判断行为数据是否为空
        if (behavior != null) {
            Behavior BehaviorObject = JSON.parseObject(behavior, Behavior.class);
            BehaviorObject.setUnlike(behaviorDto.getType() == BehaviorConstants.IS_UNLIKE ? 1 : 0);
            // 删除目标数据
            redisTemplate.opsForHash().delete(name, key);
            // 新增行为数据
            redisTemplate.opsForHash().put(name, key, JSON.toJSONString(BehaviorObject));
        }

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
