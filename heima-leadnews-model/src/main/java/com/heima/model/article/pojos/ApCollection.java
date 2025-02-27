package com.heima.model.article.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 20:53
 */
@TableName("ap_collection")
@Data
public class ApCollection {

    @TableId
    private long id;

    @TableField("entry_id")
    private long entryId;

    @TableField("user_id")
    private long userId;

    @TableField("type")
    private short type;

    @TableField("collection_time")
    private LocalDateTime collectionTime;

    @TableField("published_time")
    private LocalDateTime publishedTime;

}
