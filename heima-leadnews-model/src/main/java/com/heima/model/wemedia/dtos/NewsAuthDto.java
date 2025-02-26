package com.heima.model.wemedia.dtos;

import lombok.Data;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 20:54
 */
@Data
public class NewsAuthDto {

    private int id;

    private String msg;

    private int page;

    private int size;

    private Short status;

    private String title;

}
