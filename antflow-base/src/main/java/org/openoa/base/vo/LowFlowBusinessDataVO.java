package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 低代码流程业务数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowFlowBusinessDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流程主表ID
     */
    private Long mainId;

    /**
     * 流程配置ID
     */
    private Long confId;

    /**
     * 表单编码
     */
    private String formCode;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 字段信息列表
     */
    private List<FieldInfo> fields;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 字段ID
         */
        private String fieldId;

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 字段标签(中文注释)
         */
        private String fieldLabel;

        /**
         * 字段类型
         */
        private Integer fieldType;

        /**
         * 字段值
         */
        private Object fieldValue;
    }
}
