package org.openoa.base.vo;

import lombok.Data;

/**
 * @Author TylerZhou
 * @Date 2024/7/27 19:30
 * @Version 1.0
 */
@Data
public class FieldAttributeInfoVO {
    private String fieldName;
    /**
     * {@link org.openoa.base.constant.enums.FieldValueTypeEnum}
     */
    private String fieldType;
    private String fieldLabel;
    private String fieldValue;
    private Integer choiceMaxValue;
    private Boolean isMultiChoice=false;
    private Integer sort;
    private Object value;
}
