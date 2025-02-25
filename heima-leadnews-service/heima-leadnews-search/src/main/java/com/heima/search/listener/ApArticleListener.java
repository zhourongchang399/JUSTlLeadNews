package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.KafkaConstants;
import com.heima.model.article.vos.ApArticleSearchVo;
import com.heima.search.service.ApSearchService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 19:57
 */
@Component
public class ApArticleListener {

    @Autowired
    private ApSearchService apArticleService;

    @KafkaListener(topics = KafkaConstants.UPDATE_ELASTIC_SEARCH_APP_ARTICLE_TOPIC)
    public void updateElasticSearchAppArticle(ConsumerRecord<String, String> record) throws IOException {
        // 参数校验
        if (record == null) {
            return;
        }

        // 获取文章信息
        ApArticleSearchVo searchVo = JSON.parseObject(record.value(), ApArticleSearchVo.class);

        // 调用服务以新增doc
        apArticleService.insertArticle(searchVo);

    }

}
