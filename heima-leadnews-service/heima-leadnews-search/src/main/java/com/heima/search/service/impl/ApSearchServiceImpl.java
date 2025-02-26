package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.api.article.IArticleClient;
import com.heima.common.constants.SearchConstats;
import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.article.vos.ApArticleSearchVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.search.service.ApSearchService;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.common.WmThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 14:49
 */
@Service
@Slf4j
public class ApSearchServiceImpl implements ApSearchService {

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;

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
            putMappingRequest.source(SearchConstats.MAPPING_TEMPLATE, XContentType.JSON);
            AcknowledgedResponse putMappingResponse = restHighLevelClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
        } else {
            // 清空文档
            DeleteRequest deleteRequest = new DeleteRequest(mapping, "_all");
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        }

        if (mapping.equals(SearchConstats.ARTICLE_INDEX)) {
            loadIndex(mapping);
        }

    }

    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {
        // 参数校验
        if (dto == null || dto.getSearchWords() == null || dto.getSearchWords().equals("")) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 获取当前用户的userId
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断是否登录
        if (userId != null && dto.getFromIndex() == 0) {
            // 更新搜索词历史记录
            log.info("userId:{}, searchWord:{}", userId, dto.getSearchWords());
            apUserSearchService.insert(dto.getSearchWords(), userId);
        }

        // 设置检索条件
        SearchRequest searchRequest = new SearchRequest(SearchConstats.ARTICLE_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", dto.getSearchWords()))
                .should(QueryBuilders.matchQuery("content", dto.getSearchWords()));
        sourceBuilder.query(boolQuery);

        // 设置页码和大小
        sourceBuilder.from(dto.getPageNum());
        sourceBuilder.size(dto.getPageSize());

        // 设置排序
        sourceBuilder.sort("publishTime", SortOrder.DESC);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags("<em>").postTags("</em>"); // 设置title高亮
        highlightBuilder.field("content").preTags("<em>").postTags("</em>"); // 设置content高亮
        sourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(sourceBuilder);

        // 执行查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 封装结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Map> apArticleSearchVoList = Arrays.stream(hits)
                .map(h -> {
                    Map<String, Object> map = JSON.parseObject(h.getSourceAsString(), Map.class);
                    //处理高亮
                    if(h.getHighlightFields() != null && h.getHighlightFields().size() > 0 && h.getHighlightFields().get("title") != null){
                        Text[] titles = h.getHighlightFields().get("title").getFragments();
                        String title = StringUtils.join(titles);
                        //高亮标题
                        map.put("h_title",title);
                    }else {
                        //原始标题
                        map.put("h_title", map.get("title"));
                    }
                    return map;
                })
                .collect(Collectors.toList());

        // 返回结果
        return ResponseResult.okResult(apArticleSearchVoList);
    }

    @Override
    public void insertArticle(ApArticleSearchVo searchVo) throws IOException {
        // 构建请求
        IndexRequest indexRequest = new IndexRequest(SearchConstats.ARTICLE_INDEX);
        indexRequest.id(String.valueOf(searchVo.getId()))
                .source(JSON.toJSONString(searchVo), XContentType.JSON);

        // 向ES发送请求
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
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

}
