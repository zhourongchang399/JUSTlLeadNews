package com.heima.search.controller.v1;

import com.heima.model.article.dtos.HistorySearchDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.service.ApUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 21:55
 */
@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController {

    @Autowired
    private ApUserSearchService apUserSearchService;

    @PostMapping("/load")
    public ResponseResult loadSearchHistory() {
        return apUserSearchService.loadSearchHistory();
    }

    @PostMapping("/del")
    public ResponseResult deleteSearchHistory(@RequestBody HistorySearchDto historySearchDto) {
        return apUserSearchService.deleteSearchHistory(historySearchDto);
    }

}
