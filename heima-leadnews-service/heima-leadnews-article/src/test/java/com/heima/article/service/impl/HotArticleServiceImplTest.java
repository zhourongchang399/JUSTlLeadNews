package com.heima.article.service.impl;

import com.heima.ApArticleApplication;
import com.heima.article.service.HotArticleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ApArticleApplication.class)
@RunWith(SpringRunner.class)
class HotArticleServiceImplTest {

    @Autowired
    private HotArticleService hotArticleService;

    @Test
    void searchFiveDayHotArticle() {
        hotArticleService.searchFiveDayHotArticle();
    }

}