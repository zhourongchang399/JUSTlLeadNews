package com.heima.model.behavior.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/27 13:50
 */
@Data
public class BehaviorDto {

    private long articleId;

    private long authorId;

    private int operation;

    private Short type;

    private long count;

    private long entryId;

    private LocalDateTime publishedTime;

}
