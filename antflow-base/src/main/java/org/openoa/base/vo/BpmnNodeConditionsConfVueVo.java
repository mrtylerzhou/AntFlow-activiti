package org.openoa.base.vo;

import lombok.Data;

/**
 * @Author TylerZhou
 * @Date 2024/6/20 21:23
 * @Version 0.5
 */
@Data
public class BpmnNodeConditionsConfVueVo {
    private String showType;
    private String columnId;
    private Integer formId;
    private Integer type;
    private String showName;
    private Integer optType;
    private String zdy1;
    private String opt1;
    private String zdy2;
    private String opt2;
    private String columnDbname;
    private String columnType;
    private String fieldTypeName;
    private Boolean multiple;
    private Integer multipleLimit;
    private String fixedDownBoxValue;

}
