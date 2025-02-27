package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 16:15
 */
@Data
@TableName("ap_user_follow")
public class ApUserFollow {

    @TableId("id")
    private long id;

    @TableField("user_id")
    private long userId;

    @TableField("follow_id")
    private Integer followId;

    @TableField("follow_name")
    private String followName;

    private short level;

    @TableField("is_notice")
    private short isNotice;

    @TableField("created_time")
    private LocalDateTime createdTime;

}
