package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 16:12
 */
@Data
@TableName("ap_user_fan")
public class ApUserFan {

    @TableId("id")
    private long id;

    @TableField("user_id")
    private long userId;

    @TableField("fans_id")
    private long fansId;

    @TableField("fans_name")
    private String fanName;

    private short level;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("is_display")
    private short isDisplay;

    @TableField("is_shield_letter")
    private short isShieldLetter;

    @TableField("is_shield_comment")
    private short isShieldComment;

}
