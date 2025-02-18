package com.heima.model.wemedia.pojos;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 自媒体图文引用素材信息表
 * </p>
 *
 * @author itheima
 */
@Data
public class WmNewsMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 素材ID
     */
    private Integer materialId;

    /**
     * 图文ID
     */
    private Integer newsId;

    /**
     * 引用类型
     0 内容引用
     1 主图引用
     */
    private Short type;

    /**
     * 引用排序
     */
    private Short ord;

}