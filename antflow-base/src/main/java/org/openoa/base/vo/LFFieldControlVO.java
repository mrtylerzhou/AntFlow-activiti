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
     * 字段id
     */
    private String fieldId;
    /**
     * 字段名
     */

    private String fieldName;

    /**
     * 字段权限
     */
   private String perm;


}
