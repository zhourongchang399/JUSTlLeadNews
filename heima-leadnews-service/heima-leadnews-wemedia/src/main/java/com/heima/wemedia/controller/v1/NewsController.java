package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/14 0:54
 */
@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult listNewsByCondition(@RequestBody WmNewsPageReqDto wmNewsPageReqDto){
        return wmNewsService.listNewsByConditions(wmNewsPageReqDto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto wmNewsDto) {
        return wmNewsService.submitNews(wmNewsDto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult getOneNews(@PathVariable("id") Integer id){
        return wmNewsService.getOneNews(id);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUpNews(@RequestBody WmNewsDto wmNewsDto){
        return wmNewsService.downOrUp(wmNewsDto);
    }

    @PostMapping("/list_vo")
    public ResponseResult listVo(@RequestBody NewsAuthDto newsAuthDto){
        return wmNewsService.lsitVo(newsAuthDto);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult getOneVo(@PathVariable("id") Integer id){
        return wmNewsService.getOneVo(id);
    }

    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDto newsAuthDto){
        return wmNewsService.authFail(newsAuthDto);
    }

    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDto newsAuthDto){
        return wmNewsService.authPass(newsAuthDto);
    }

}
