package com.heima.model.behavior.pojos;

import lombok.Data;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 14:28
 */
@Data
public class Behavior {

    // 点赞
    private int like;

    // 不喜欢
    private int unlike;

    // 阅读次数
    private int count;

}
