package com.heima.model.article.vos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

import java.util.Date;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 16:20
 */
@Data
public class ApArticleSearchVo {

    // 文章id
    private Long id;
    // 文章标题
    private String title;
    // 文章发布时间
    private Date publishTime;
    // 文章布局
    private Integer layout;
    // 封面
    private String images;
    // 作者id
    private Long authorId;
    // 作者名词
    private String authorName;
    //静态url
    private String staticUrl;
    //文章内容
    private String content;

}
