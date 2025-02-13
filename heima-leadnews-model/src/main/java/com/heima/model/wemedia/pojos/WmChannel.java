package com.heima.model.wemedia.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 频道信息表
 * </p>
 *
 * @author itheima
 */
@Data
public class WmChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 频道名称
     */
    private String name;

    /**
     * 频道描述
     */
    private String description;

    /**
     * 是否默认频道
     * 1：默认     true
     * 0：非默认   false
     */
    private Boolean isDefault;

    /**
     * 是否启用
     * 1：启用   true
     * 0：禁用   false
     */
    private Boolean status;

    /**
     * 默认排序
     */
    private Integer ord;

    /**
     * 创建时间
     */
    private Date createdTime;

}