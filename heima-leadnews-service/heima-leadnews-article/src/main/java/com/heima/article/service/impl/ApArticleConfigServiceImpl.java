package com.heima.article.service.impl;

import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.pojos.ApArticleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/24 21:51
 */
@Service
public class ApArticleConfigServiceImpl implements ApArticleConfigService {

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Override
    public void updateArticleConfig(ApArticleConfig apArticleConfig) {
        if (apArticleConfig != null && apArticleConfig.getArticleId() != null) {
            apArticleConfigMapper.update(apArticleConfig);
        }
    }

}
