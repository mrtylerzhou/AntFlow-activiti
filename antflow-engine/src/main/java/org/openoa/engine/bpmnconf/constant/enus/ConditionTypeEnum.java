package org.openoa.engine.bpmnconf.constant.enus;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import static org.openoa.base.constant.StringConstants.EXPRESSION_FIELD_NAME;
import static org.openoa.base.constant.StringConstants.LOWFLOW_CONDITION_CONTAINER_FIELD_NAME;

import java.util.Date;
import java.util.List;

/**
 * 条件类型枚举
 */
public enum ConditionTypeEnum {
    CONDITION_THIRD_ACCOUNT_TYPE(1, "三方账户", "accountType", 1, Integer.class,
            BpmnNodeConditionsAccountTypeAdp.class, BpmnStartConditionsVo.class, "accountType", ThirdAccountJudge.class),
    //!important 如果自定义条件是数值类型,并且需要使用介于两个数之间对比,则必须在BpmnNodeConditionsConfBaseVo中的字段为字符串类型
    //一般地需要设置两个字段,第一个字段即为枚举里的fieldName字段,这个字段要设置在BusinessdataVo或者其继承类中,第二个设置在BpmnNodeConditionsConfBaseVo中,一个是表单中的字段,一个是设计流程时存储在数据库里的字段
    //执行条件比较时,会取出存储在数据库中的字段和表单中的对比,就这么简单,至于怎么比,需要用户自己实现,即枚举里最后一个字段,ConditionJudge
    //如果用于介于两个数之间条件,需要BpmnNodeConditionsConfBaseVo定义的字段为字符串,并不需要BusinessDataVo或其继承类中的,即表单中的字段也是字符串类型
    CONDITION_BIZ_LEAVE_TIME(2, "请假时长", "leaveHour", 2, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "leaveHour", AskLeaveJudge.class),
    CONDITION_PURCHASE_FEE(3, "采购费用", "planProcurementTotalMoney", 2, Double.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "planProcurementTotalMoney", PurchaseTotalMoneyJudge.class),
    CONDITION_TYPE_OUT_TOTAL_MONEY(4, "支出费用", "outTotalMoney", 2, String.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "outTotalMoney", OutTotalMoneyJudge.class),
    CONDITION_JOB_LEVEL_TYPE(5, "职级列表", "jobLevelVo", 2, BaseIdTranStruVo.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "jobLevelVo", JobLevelJudge.class),
    CONDITION_PURCHASE_TYPE(6, "采购类型", "purchaseType", 1, Integer.class,
            BpmnNodeConditionsPurchaseTypeAdp.class, BpmnStartConditionsVo.class, "purchaseType", PurchaseTypeJudge.class),
    CONDITION_TYPE_NUMBER_OPERATOR(7, "数字运算符", "numberOperator", 2, Integer.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "numberOperator", MoneyOperatorJudge.class),

    CONDITION_THIRD_PARK_AREA(37, "园区面积", "parkArea", 3, String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class, "parkArea", ParkAreaJudge.class),
    CONDITION_TYPE_TOTAL_MONEY(38, "总金额", "totalMoney", 2, String.class,
            BpmnNodeConditionsTotalMoneyAdp.class, BpmnStartConditionsVo.class, "totalMoney", TotalMoneyJudge.class),


    //三方工作流条件
    CONDITION_TEMPLATEMARK(9999, "条件模板标识", "templateMarks", 1, String.class,
            BpmnTemplateMarkAdp.class, BpmnStartConditionsVo.class, "templateMarks", BpmnTemplateMarkJudge.class),

    //低(无)代码流程条件
    CONDITION_TYPE_LF_STR_CONDITION(10000,"无代码字符串流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,"lfConditions",LFStringConditionJudge.class),
    CONDITION_TYPE_LF_NUM_CONDITION(10001,"无代码数字流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,"lfConditions", LFNumberFormatJudge.class),
    CONDITION_TYPE_LF_DATE_CONDITION(10002,"无代码日期流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2, String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,LOWFLOW_CONDITION_CONTAINER_FIELD_NAME, LFDateConditionJudge.class),
    CONDITION_TYPE_LF_DATE_TIME_CONDITION(10003,"无代码日期时间流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,2,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,LOWFLOW_CONDITION_CONTAINER_FIELD_NAME, LFDateTimeConditionJudge.class),
    CONDITION_TYPE_LF_COLLECTION_CONDITION(10004,"无代码集合流程条件",LOWFLOW_CONDITION_CONTAINER_FIELD_NAME,1,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,LOWFLOW_CONDITION_CONTAINER_FIELD_NAME, LFCollectionConditionJudge.class),


    CONDITION_TYPE_JUEL_EXPRESSION(20000,"JUEL表达式",EXPRESSION_FIELD_NAME,2,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,EXPRESSION_FIELD_NAME, JuelExpressionConditionJudge.class),
    CONDITION_TYPE_SPEL_EXPRESSION(20001,"SpEL表达式",EXPRESSION_FIELD_NAME,2,String.class,
            BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,EXPRESSION_FIELD_NAME, SpelExpressionConditionJudge.class),
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