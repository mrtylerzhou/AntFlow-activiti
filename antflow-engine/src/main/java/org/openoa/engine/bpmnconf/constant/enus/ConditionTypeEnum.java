package org.openoa.engine.bpmnconf.constant.enus;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import static org.openoa.base.constant.StringConstants.LOWFLOW_CONDITION_CONTAINER_FIELD_NAME;

import java.util.Date;
import java.util.List;

/**
 * 条件类型枚举
 */
public enum ConditionTypeEnum {
    CONDITION_THIRD_ACCOUNT_TYPE(1, "三方账户", "accountType", 1, Integer.class,
            BpmnNodeConditionsAccountTypeAdp.class, BpmnStartConditionsVo.class, "accountType", ThirdAccountJudge.class),
    CONDITION_BIZ_LEAVE_TIME(2, "请假时长", "leaveHour", 2, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "leaveHour", AskLeaveJudge.class),
    CONDITION_PURCHASE_FEE(3, "采购费用", "planProcurementTotalMoney", 3, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "planProcurementTotalMoney", PurchaseTotalMoneyJudge.class),
    CONDITION_TYPE_OUT_TOTAL_MONEY(4, "支出费用", "outTotalMoney", 2, String.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "outTotalMoney", OutTotalMoneyJudge.class),
    CONDITION_JOB_LEVEL_TYPE(5, "职级列表", "jobLevelVo", 2, BaseIdTranStruVo.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "jobLevelVo", JobLevelJudge.class),
    CONDITION_PURCHASE_TYPE(6, "采购类型", "purchaseType", 1, Integer.class,
            BpmnNodeConditionsPurchaseTypeAdp.class, BpmnStartConditionsVo.class, "purchaseType", PurchaseTypeJudge.class),
    CONDITION_TYPE_NUMBER_OPERATOR(7, "数字运算符", "numberOperator", 2, Integer.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "numberOperator", MoneyOperatorJudge.class),

    CONDITION_THIRD_PARK_AREA(37, "园区面积", "parkArea", 3, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "parkArea", ParkAreaJudge.class),
    CONDITION_TYPE_TOTAL_MONEY(38, "总金额", "totalMoney", 2, String.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "totalMoney", TotalMoneyJudge.class),


    //三方工作流条件
    CONDITION_TEMPLATEMARK(9999, "条件模板标识", "templateMarks", 1, String.class,
            BpmnTemplateMarkAdp.class, BpmnStartConditionsVo.class, "templateMarks", BpmnTemplateMarkJudge.class),

    //低(无)代码流程条件
    CONDITION_TYPE_LF_STR_CONDITION(10000,"无代码字符串流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,"lfConditions",LFStringConditionJudge.class),
    CONDITION_TYPE_LF_NUM_CONDITION(10001,"无代码数字流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2,Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,"lfConditions", LFNumberFormatJudge.class),
    CONDITION_TYPE_LF_DATE_CONDITION(10002,"无代码日期流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2, Date.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,LOWFLOW_CONDITION_CONTAINER_FIELD_NAME, LFDateConditionJudge.class),
    CONDITION_TYPE_LF_DATE_TIME_CONDITION(10003,"无代码日期时间流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2,Date.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,LOWFLOW_CONDITION_CONTAINER_FIELD_NAME, LFDateConditionJudge.class),
    CONDITION_TYPE_LF_COLLECTION_CONDITION(10004,"无代码集合流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,1,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,LOWFLOW_CONDITION_CONTAINER_FIELD_NAME, LFCollectionConditionJudge.class),
    ;
static{
    lowFlowCodes=  Lists.newArrayList(
            CONDITION_TYPE_LF_STR_CONDITION.code,
            CONDITION_TYPE_LF_NUM_CONDITION.code,
            CONDITION_TYPE_LF_DATE_CONDITION.code,
            CONDITION_TYPE_LF_DATE_TIME_CONDITION.code,
            CONDITION_TYPE_LF_COLLECTION_CONDITION.code
            );
}
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

    private static final List<Integer> lowFlowCodes;
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

    public static boolean isLowCodeFlow(ConditionTypeEnum conditionTypeEnum){
      return lowFlowCodes.contains(conditionTypeEnum.code);
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