package com.heima.wemedia.service.impl;

import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.utils.common.MySensitiveWorkUtil;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.apache.yetus.audience.InterfaceAudience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/21 15:29
 */
@Service
public class WmSensitiveServiceImpl implements WmSensitiveService {

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Override
    public Map<String, Integer> textCheck(String text) {
        Map<String, Integer> resultMap = null;

        // 参数校验
        if (text == null || "".equals(text)) {
            return resultMap;
        }

        // 获取违禁词字典
        List<WmSensitive> wmSensitiveList = wmSensitiveMapper.findAll();
        List<String> sensitiveList = wmSensitiveList.stream().map(s -> s.getSensitives()).collect(Collectors.toList());

        // 初始化字典
        MySensitiveWorkUtil.initDictionary(sensitiveList);

        // 匹配违禁词
        resultMap = MySensitiveWorkUtil.matchDictionary(text);

        // 返回结果
        return resultMap;
    }

}
