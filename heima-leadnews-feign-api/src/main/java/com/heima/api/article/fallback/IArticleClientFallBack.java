package com.heima.api.article.fallback;

import com.heima.api.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/20 23:15
 */
@Component
public class IArticleClientFallBack implements IArticleClient {

    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {
        return ResponseResult.okResult(AppHttpCodeEnum.FAIL_PUBLISH_NEWS);
    }

    @Override
    public ResponseResult loadArticle() {
        return ResponseResult.okResult(AppHttpCodeEnum.DATA_NOT_EXIST);
    }

}
