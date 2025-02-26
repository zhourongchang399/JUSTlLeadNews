package com.heima.model.admin.dtos;

import lombok.Data;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 22:39
 */
@Data
public class AuthDto {

    private int id;

    private String msg;

    private int page;

    private int size;

    private Short status;

}
