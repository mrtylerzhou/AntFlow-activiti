package org.openoa.base.vo;

import lombok.Data;

@Data
public class LFFieldControlVO {
    /**
     * 节点ID
     */
    private Long nodeId;
    /**
     * 表单数据ID
     */
    private Long formdataId;

    /**
     * 字段名
     */

    private String fieldName;


    /**
     * 是否可写（0不限制，1限制,即1为可见）
     */
    private Integer isVisible;

    /**
     * 是否可读（即是否限制，0不限制，1限制,即1为只读）
     */
    private Integer isReadonly;

}
