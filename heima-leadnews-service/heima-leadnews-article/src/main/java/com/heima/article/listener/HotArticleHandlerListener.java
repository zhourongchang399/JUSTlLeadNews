package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.mess.UpdateArticleMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.jsoup.helper.StringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/3/3 21:51
 */

@Slf4j
@Configuration
public class HotArticleHandlerListener {

    @Bean
    public KStream<String, String> hotArticleStream(StreamsBuilder builder) {
        // 监听消息
        KStream<String,String> stream = builder.stream(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC);

        log.info("hotArticleStream: 接收到消息");

        // 聚合消息
        stream.map((key, value) -> {
            // 将消息转为对象
            UpdateArticleMess updateArticleMess = JSON.parseObject(String.valueOf(value), UpdateArticleMess.class);
            return new KeyValue<>(updateArticleMess.getArticleId().toString(), updateArticleMess.getType() + ":" + updateArticleMess.getAdd());
        })
                // 根据文章Id分组
                .groupBy((key,value)->key)
                // 时间窗口
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                //聚合
                .aggregate(new Initializer<Map<UpdateArticleMess.UpdateArticleType, Integer>>() {
                                @Override
                                public Map<UpdateArticleMess.UpdateArticleType, Integer> apply() {
                                    // 初始化map对象
                                    Map<UpdateArticleMess.UpdateArticleType, Integer> map = new HashMap<>();
                                    map.put(UpdateArticleMess.UpdateArticleType.COLLECTION, 0);
                                    map.put(UpdateArticleMess.UpdateArticleType.VIEWS, 0);
                                    map.put(UpdateArticleMess.UpdateArticleType.LIKES, 0);
                                    map.put(UpdateArticleMess.UpdateArticleType.COMMENT, 0);
                                    return map;
                                }
                            },
                        new Aggregator<String, String, Map<UpdateArticleMess.UpdateArticleType, Integer>>() {
                            @Override
                            public Map<UpdateArticleMess.UpdateArticleType, Integer> apply(String key, String value, Map<UpdateArticleMess.UpdateArticleType, Integer> aggValue) {
                                if (StringUtil.isBlank(value)) {
                                    return aggValue;
                                }
                                // 累加每条消息的value
                                String[] splitValue = value.split(":");
                                switch (UpdateArticleMess.UpdateArticleType.valueOf(splitValue[0])) {
                                    case COLLECTION:
                                        aggValue.replace(UpdateArticleMess.UpdateArticleType.COLLECTION, aggValue.get(UpdateArticleMess.UpdateArticleType.COLLECTION) + Integer.parseInt(splitValue[1]));
                                        break;
                                    case VIEWS:
                                        aggValue.replace(UpdateArticleMess.UpdateArticleType.VIEWS, aggValue.get(UpdateArticleMess.UpdateArticleType.VIEWS) + Integer.parseInt(splitValue[1]));
                                        break;
                                    case LIKES:
                                        aggValue.replace(UpdateArticleMess.UpdateArticleType.LIKES, aggValue.get(UpdateArticleMess.UpdateArticleType.LIKES) + Integer.parseInt(splitValue[1]));
                                        break;
                                    case COMMENT:
                                        aggValue.replace(UpdateArticleMess.UpdateArticleType.COMMENT, aggValue.get(UpdateArticleMess.UpdateArticleType.COMMENT) + Integer.parseInt(splitValue[1]));
                                        break;
                                }
                                log.info(aggValue.toString());
                                return aggValue;
                            }
                        },
                        Materialized.as("hot-atricle-stream-count-001"))
                .toStream()
                .map((key,value)->{
                    return new KeyValue<>(key.key().toString(),formatObj(key.key().toString(),value));
                })
                .to(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC);

        log.info("hotArticleStream: 消息处理结束");

        return stream;

    }

    private String formatObj(String key, Map<UpdateArticleMess.UpdateArticleType, Integer> value) {
        ArticleVisitStreamMess articleVisitStreamMess = new ArticleVisitStreamMess();
        articleVisitStreamMess.setArticleId(Long.valueOf(key));
        articleVisitStreamMess.setView(value.get(UpdateArticleMess.UpdateArticleType.VIEWS));
        articleVisitStreamMess.setLike(value.get(UpdateArticleMess.UpdateArticleType.LIKES));
        articleVisitStreamMess.setLike(value.get(UpdateArticleMess.UpdateArticleType.COLLECTION));
        articleVisitStreamMess.setLike(value.get(UpdateArticleMess.UpdateArticleType.COMMENT));
        System.out.println("文章的id:"+key);
        System.out.println("当前时间窗口内的消息处理结果：" + JSON.toJSONString(articleVisitStreamMess));
        return JSON.toJSONString(articleVisitStreamMess);
    }

}
