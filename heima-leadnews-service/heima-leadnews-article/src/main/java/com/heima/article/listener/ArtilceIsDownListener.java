package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.WemediaConstants;
import com.heima.model.article.pojos.ApArticleConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/24 21:46
 */
@Component
public class ArtilceIsDownListener {

    private static final Logger log = LoggerFactory.getLogger(ArtilceIsDownListener.class);

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = WemediaConstants.UP_OR_DOWN_ARTICLE_TOPIC)
    public void upOrDownArticle(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (value != null) {
            log.info("监听到文章上下架消息：{}",value);
            // 读取消息中的value
            Map map = JSON.parseObject(value, Map.class);
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId((Long) map.get("articleId"));
            Integer enable = (Integer) map.get("enable");
            boolean isDown = true;
            if(enable.equals(1)){
                isDown = false;
            }
            apArticleConfig.setIsDown(isDown);
            // 修改文章配置
            apArticleConfigService.updateArticleConfig(apArticleConfig);
        }
    }

}
