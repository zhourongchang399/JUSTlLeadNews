package com.heima.search.service.impl;

import com.heima.SearchApplication;
import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.service.ApAssociateWordsService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(classes = SearchApplication.class)
@RunWith(SpringRunner.class)
class ApAssociateWordsServiceImplTest {

    @Autowired
    private ApAssociateWordsService apAssociateWordsService;

    @Test
    void findAssociate() throws IOException {
        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setSearchWords("java");
        ResponseResult responseResult = apAssociateWordsService.findAssociate(userSearchDto);
    }

}