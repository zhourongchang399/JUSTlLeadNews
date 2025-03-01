package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/3/1 21:55
 */
@Component
@Slf4j
public class HotArticleJob {

    @Autowired
    private HotArticleService hotArticleService;

    /**
     * @author: Zc
     * @description: 热门文章计算分布任务调度
     * @date: 2025/3/1 21:59
     * @param null
     * @return
     */
    @XxlJob("hotArticleJob")
    public void hotArticleJob(){
        log.info("hotArticleJob 开始执行！！！");
        hotArticleService.searchFiveDayHotArticle();
        log.info("hotArticleJob 执行完毕！！！");
    }

}
