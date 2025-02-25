package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.api.article.IArticleClient;
import com.heima.model.article.vos.ApArticleSearchVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.service.ApSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 14:49
 */
@Service
@Slf4j
public class ApSearchServiceImpl implements ApSearchService {

    private static final String ARTICLE_INDEX = "app_article_info";

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void getMappingInfo(String mapping) throws IOException {
        GetRequest getRequest = new GetRequest(mapping, "_all");
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        log.info(response.getSourceAsString());
    }

    @Override
    public void initMapping(String mapping) throws IOException {
        // 判断索引是否存在，存在则清空文档，不在则创建
        GetMappingsRequest getMappingsRequest = new GetMappingsRequest().indices(mapping);
        GetMappingsResponse getMappingsResponse = restHighLevelClient.indices().getMapping(getMappingsRequest, RequestOptions.DEFAULT);
        if (getMappingsResponse == null) {
            // 创建mapping
            PutMappingRequest putMappingRequest = new PutMappingRequest(mapping);
            putMappingRequest.source(MAPPING_TEMPLATE, XContentType.JSON);
            AcknowledgedResponse putMappingResponse = restHighLevelClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
        } else {
            // 清空文档
            DeleteRequest deleteRequest = new DeleteRequest(mapping, "_all");
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        }

        if (mapping.equals(ARTICLE_INDEX)) {
            loadIndex(mapping);
        }

    }

    private void loadIndex(String mapping) throws IOException {
        // 查询文章信息
        ResponseResult responseResult = articleClient.loadArticle();
        if (responseResult.getData() == null) {
            return;
        }
        List<ApArticleSearchVo> apArticleSearchVoList = JSON.parseArray(responseResult.getData().toString(), ApArticleSearchVo.class);

        // 初始化索引
        BulkRequest bulkRequest = new BulkRequest();
        for (ApArticleSearchVo apArticleSearchVo : apArticleSearchVoList) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", String.valueOf(apArticleSearchVo.getId()));
            data.put("title", apArticleSearchVo.getTitle());
            data.put("content", apArticleSearchVo.getContent());
            data.put("publishTime", apArticleSearchVo.getPublishTime());
            data.put("images", apArticleSearchVo.getImages());
            data.put("staticUrl", apArticleSearchVo.getStaticUrl());
            data.put("authorId", apArticleSearchVo.getAuthorId());
            data.put("authorName", apArticleSearchVo.getAuthorName());
            data.put("layout", apArticleSearchVo.getLayout());
            IndexRequest indexRequest = new IndexRequest(mapping)
                    .id(String.valueOf(apArticleSearchVo.getId()))
                    .source(data);
            bulkRequest.add(indexRequest);
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private static final String MAPPING_TEMPLATE = "{\n" +
            "    \"mappings\":{\n" +
            "        \"properties\":{\n" +
            "            \"id\":{\n" +
            "                \"type\":\"long\"\n" +
            "            },\n" +
            "            \"publishTime\":{\n" +
            "                \"type\":\"date\"\n" +
            "            },\n" +
            "            \"layout\":{\n" +
            "                \"type\":\"integer\"\n" +
            "            },\n" +
            "            \"images\":{\n" +
            "                \"type\":\"keyword\",\n" +
            "                \"index\": false\n" +
            "            },\n" +
            "            \"staticUrl\":{\n" +
            "                \"type\":\"keyword\",\n" +
            "                \"index\": false\n" +
            "            },\n" +
            "            \"authorId\": {\n" +
            "                \"type\": \"long\"\n" +
            "            },\n" +
            "            \"authorName\": {\n" +
            "                \"type\": \"text\"\n" +
            "            },\n" +
            "            \"title\":{\n" +
            "                \"type\":\"text\",\n" +
            "                \"analyzer\":\"ik_smart\"\n" +
            "            },\n" +
            "            \"content\":{\n" +
            "                \"type\":\"text\",\n" +
            "                \"analyzer\":\"ik_smart\"\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

}
