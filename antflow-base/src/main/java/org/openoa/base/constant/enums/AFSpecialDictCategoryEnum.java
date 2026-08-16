package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AFSpecialDictCategoryEnum implements AfEnumBase{
    LOWCODEFLOW(0,"lowcodeflow","低代码流程"),
    USER_DEFINED_RULE_FOR_ASSIGNEE(0,"udr","自定义审批规则"),
    PROCESSLABEL(0,"processlabel","流程标签")
    ;
    private Integer code;
    private String  desc;
    /**
     * 汉字含义(字典管理页面展示用)
     */
    private String label;

    /**
     * 根据 desc 获取汉字含义,未知类型返回 null(列表原样展示)
     */
    public static String getLabelByDesc(String desc){
        if (desc == null) {
            return null;
        }
        for (AFSpecialDictCategoryEnum e : values()) {
            if (desc.equals(e.getDesc())) {
                return e.getLabel();
            }
        }
        return null;
    }

    /**
     * 是否为低代码流程类型(系统自动写入,禁止手动编辑/删除)
     */
    public static boolean isLowCodeFlow(String desc){
        return LOWCODEFLOW.getDesc().equals(desc);
    }
}
