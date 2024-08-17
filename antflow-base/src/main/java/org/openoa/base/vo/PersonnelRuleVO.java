package org.openoa.base.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/27 20:03
 * @Version 1.0
 */
@Data
public class PersonnelRuleVO {
    private String nodePropertyName;
    private Integer nodeProperty;
    private List<FieldAttributeInfoVO> fieldInfos;
}
