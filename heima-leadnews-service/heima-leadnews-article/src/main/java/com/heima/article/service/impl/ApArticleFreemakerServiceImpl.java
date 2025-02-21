package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.aliyunOSS.service.AliOssService;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleFreemakerService;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/21 21:41
 */
@Service
public class ApArticleFreemakerServiceImpl implements ApArticleFreemakerService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private AliOssService aliOssService;

    // 异步生成静态文件
    @Async
    @Override
    @Transactional
    public void ganerateStaricFile(Long articleId) throws TemplateException, IOException {
        //1.获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.getByArticleId(articleId);
        if(apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            //2.文章内容通过freemarker生成html文件
            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate("article.ftl");

            Map<String, Object> params = new HashMap<>();
            params.put("content", JSONArray.parseArray(apArticleContent.getContent()));

            template.process(params, out);

            //3.把html文件上传到aliyun中
            String path = aliOssService.upload(out.toString().getBytes(), apArticleContent.getArticleId() + ".html");

            //4.修改ap_article表，保存static_url字段
            ApArticle article = new ApArticle();
            article.setId(apArticleContent.getArticleId());
            article.setStaticUrl(path);
            apArticleMapper.updateById(article);

            System.out.println("static_path:" + path);
        }
    }

}
