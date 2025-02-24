package com.heima.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/24 21:32
 */
@Component
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendAsync(String topic, String message) {
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(topic, message);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
                log.info("消息发送成功：{}", stringStringSendResult.getRecordMetadata());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("消息发送失败：{}", throwable);
            }
        });

    }

}
