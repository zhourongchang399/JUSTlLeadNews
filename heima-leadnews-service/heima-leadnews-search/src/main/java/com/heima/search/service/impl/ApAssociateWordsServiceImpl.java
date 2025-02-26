package com.heima.search.service.impl;

import com.heima.common.constants.SearchConstats;
import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.pojos.ApAssociateWords;
import com.heima.search.service.ApAssociateWordsService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 12:21
 */
@Service
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    private static final Logger log = LoggerFactory.getLogger(ApAssociateWordsServiceImpl.class);
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final String SUGGEST_FIELD = "associateWords";

    private static final String SUGGEST_NAME = "searchWord_suggestion";

    @Override
    public ResponseResult findAssociate(UserSearchDto userSearchDto) throws IOException {
        // 参数校验
        if (userSearchDto == null && userSearchDto.getSearchWords() != null && !userSearchDto.getSearchWords().isEmpty()) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 获取联想词
        List<ApAssociateWords> apAssociateWordsList = getApAssociateWords(userSearchDto);

        // 返回结果
        return ResponseResult.okResult(apAssociateWordsList);
    }

    private List<ApAssociateWords> getApAssociateWords(UserSearchDto userSearchDto) throws IOException {
        // 构建查询请求
        SearchRequest searchRequest = new SearchRequest(SearchConstats.ASSOCIATION_INDEX);
        SuggestBuilder suggestBuilder = new SuggestBuilder();

        // 创建建议查询
        CompletionSuggestionBuilder suggestionBuilder = new CompletionSuggestionBuilder(SUGGEST_FIELD)
                .prefix(userSearchDto.getSearchWords())
                .size(10);  // 设置返回建议的数量
        suggestBuilder.addSuggestion(SUGGEST_NAME, suggestionBuilder);

        // 设置建议查询到请求中
        searchRequest.source().suggest(suggestBuilder);

        // 执行查询并获取响应
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 处理建议结果
        List<ApAssociateWords> apAssociateWordsList = new ArrayList<>();
        if (response.getSuggest() != null) {
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion = response.getSuggest().getSuggestion(SUGGEST_NAME);
            if (suggestion != null) {
                for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : suggestion.getEntries()) {
                    for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                        // 封装结果
                        ApAssociateWords apAssociateWords = new ApAssociateWords();
                        apAssociateWords.setAssociateWords(option.getText().toString());
                        apAssociateWordsList.add(apAssociateWords);
                    }
                }
            } else {
                System.out.println("No suggestions found");
            }
        }
        return apAssociateWordsList;
    }

}
