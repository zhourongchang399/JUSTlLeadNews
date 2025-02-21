package com.heima.article.service;

import freemarker.template.TemplateException;

import java.io.IOException;

public interface ApArticleFreemakerService {

    public void ganerateStaricFile(Long articleId) throws TemplateException, IOException;
}
