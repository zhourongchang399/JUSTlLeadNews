package com.heima.wemedia.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.KafkaConstants;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 23:43
 */

@Component
public class WmUserListener {

    @Autowired
    private WmUserService wmUserService;

    @KafkaListener(topics = KafkaConstants.GANERATE_WM_USER)
    public void ganerateWmUser(String message) {
        if (message != null) {
            WmUser wmUser = JSON.parseObject(message, WmUser.class);
            wmUserService.insert(wmUser);
        }
    }

}
