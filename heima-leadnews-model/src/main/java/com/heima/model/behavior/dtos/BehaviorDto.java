package com.heima.model.behavior.dtos;

import lombok.Data;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 13:50
 */
@Data
public class BehaviorDto {

    private long articleId;

    private int operation;

    private Short type;

    private long count;

}
