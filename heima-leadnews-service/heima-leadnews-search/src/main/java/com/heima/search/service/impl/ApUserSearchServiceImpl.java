package com.heima.search.service.impl;

import com.heima.model.article.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 21:58
 */
@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {

    private static final Integer MAX_SIZE = 10;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insert(String keyword, Integer userId) {
        // 参数校验
        if (keyword == null || keyword.length() == 0 || userId == null) {
            return;
        }

        // 查询该keyword在mongodb中是否存在
        Query query = new Query();
        query.addCriteria(Criteria.where("keyword").is(keyword));
        query.addCriteria(Criteria.where("userId").is(userId));
        ApUserSearch one = mongoTemplate.findOne(query, ApUserSearch.class);
        if (one != null) {
            // 存在则更新创建时间
            Query queryUpdate = new Query();
            queryUpdate.addCriteria(Criteria.where("userId").is(userId));
            Update update = new Update();
            update.set("createTime", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, ApUserSearch.class);
        } else {
            // 不存在则判断搜索词历史是否超过上限，超过则覆盖最旧的记录
            long count = mongoTemplate.count(new Query(Criteria.where("userId").is(userId)), ApUserSearch.class);
            // 构造对象
            ApUserSearch apUserSearch = new ApUserSearch();
            apUserSearch.setUserId(userId);
            apUserSearch.setKeyword(keyword);
            apUserSearch.setCreatedTime(new Date());
            if (count >= MAX_SIZE) {
                Query query1 = new Query(Criteria.where("userId").is(userId));
                query1.with(Sort.by(Sort.Direction.ASC,"createdTime"));
                List<ApUserSearch> apUserSearches = mongoTemplate.find(query1, ApUserSearch.class);
                mongoTemplate.findAndReplace(new Query(Criteria.where("userId").is(apUserSearches.get(0))), apUserSearch);
            } else {
                // 否则新增数据
                mongoTemplate.insert(apUserSearch);
            }
        }

    }

}
