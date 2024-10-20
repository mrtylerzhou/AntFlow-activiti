package org.openoa.engine.bpmnconf.constant.enus;

import lombok.Getter;
import org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

/**
 * 条件类型枚举
 */
public enum ConditionTypeEnum {
    CONDITION_THIRD_ACCOUNT_TYPE(1, "三方账户", "accountType", 1, Integer.class,
            BpmnNodeConditionsAccountTypeAdp.class, BpmnStartConditionsVo.class, "accountType", ThirdAccountJudge.class),
    CONDITION_BIZ_LEAVE_TIME(2, "请假时长", "leaveHour", 3, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "leaveHour", AskLeaveJudge.class),
    CONDITION_PURCHASE_FEE(3, "采购费用", "planProcurementTotalMoney", 3, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "planProcurementTotalMoney", PurchaseTotalMoneyJudge.class),
    CONDITION_TYPE_OUT_TOTAL_MONEY(4, "支出费用", "outTotalMoney", 2, String.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "outTotalMoney", OutTotalMoneyJudge.class),
    CONDITION_JOB_LEVEL_TYPE(5, "职级列表", "jobLevelVo", 2, BaseIdTranStruVo.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "jobLevelVo", JobLevelJudge.class),
    CONDITION_PURCHASE_TYPE(6, "采购类型", "purchaseType", 1, Integer.class,
            BpmnNodeConditionsPurchaseTypeAdp.class, BpmnStartConditionsVo.class, "purchaseType", PurchaseTypeJudge.class),
    CONDITION_TYPE_MONEY_OPERATOR(7, "总金额运算符枚举", "numberOperator", 2, Integer.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "totalMoneyOperator", MoneyOperatorJudge.class),
    CONDITION_TEMPLATEMARK(36, "条件模板标识", "templateMarks", 1, String.class,
            BpmnTemplateMarkAdp.class, BpmnStartConditionsVo.class, "templateMarks", BpmnTemplateMarkJudge.class),
    CONDITION_THIRD_PARK_AREA(37, "园区面积", "parkArea", 3, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "parkArea", ParkAreaJudge.class),
    CONDITION_TYPE_TOTAL_MONEY(38, "总金额", "totalMoney", 2, String.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "totalMoney", TotalMoneyJudge.class),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    /**
     * 条件字段名称
     */
    @Getter
    private String fieldName;

    /**
     * 条件字段类型（1-列表；2-对象）
     */
    @Getter
    private Integer fieldType;

    /**
     * 条件字段Class
     */
    @Getter
    private Class<?> fieldCls;

    /**
     * 条件字段扩展适配Class
     */
    @Getter
    private Class<? extends BpmnNodeConditionsAdaptor> cls;

    /**
     * 条件比对对象
     */
    @Getter
    private Class<?> alignmentCls;

    /**
     * 条件比对对象字段字段
     */
    @Getter
    private String alignmentFieldName;

    /**
     * 不同条件对应不同的判断类
     */
    @Getter
    private Class<? extends ConditionJudge> conditionJudgeCls;

    ConditionTypeEnum(Integer code, String desc, String fieldName, Integer fieldType, Class<?> fieldCls, Class<? extends BpmnNodeConditionsAdaptor> cls,
                      Class<?> alignmentCls, String alignmentFieldName, Class<? extends ConditionJudge> conditionJudgeCls) {
        this.code = code;
        this.desc = desc;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldCls = fieldCls;
        this.cls = cls;
        this.alignmentCls = alignmentCls;
        this.alignmentFieldName = alignmentFieldName;
        this.conditionJudgeCls = conditionJudgeCls;
    }

    /**
     * 根据Code获得枚举
     *
     * @param code
     * @return
     */
    public static ConditionTypeEnum getEnumByCode(Integer code) {
        for (ConditionTypeEnum conditionTypeEnum : ConditionTypeEnum.values()) {
            if (conditionTypeEnum.getCode().equals(code)) {
                return conditionTypeEnum;
            }
        }
        return null;
    }

    /**
     * 根据条件字段名称获得枚举
     *
     * @param fieldName
     * @return
     */
    public static ConditionTypeEnum getEnumByFieldName(String fieldName) {
        for (ConditionTypeEnum conditionTypeEnum : ConditionTypeEnum.values()) {
            if (conditionTypeEnum.getFieldName().equals(fieldName)) {
                return conditionTypeEnum;
            }
        }
        return null;
    }
}