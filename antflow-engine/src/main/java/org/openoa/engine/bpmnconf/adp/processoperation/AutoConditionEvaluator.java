package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 自动条件评估器(静态工具)。
 * 从 AbstractFormOperationAdaptor 抽取, 供自动节点/条件审批节点/用户自动审批设置复用。
 * 条件格式为前端原始格式 BpmnNodeConditionsConfVueVo (conditionList 二级列表 + groupRelation)。
 */
@Slf4j
public final class AutoConditionEvaluator {

    private AutoConditionEvaluator() {
    }

    /**
     * 评估条件列表。
     *
     * @param conditionList 条件组列表
     * @param groupRelation 组间关系 false=且 true=或
     * @param formFields    表单字段值 map
     * @return 评估结果; 无条件时返回 null
     */
    public static Boolean evaluate(List<List<BpmnNodeConditionsConfVueVo>> conditionList,
                                   Boolean groupRelation, Map<String, Object> formFields) {
        if (CollectionUtils.isEmpty(conditionList)) {
            return null;
        }
        boolean isOrBetweenGroups = Boolean.TRUE.equals(groupRelation);
        boolean overallResult = !isOrBetweenGroups; // AND starts true, OR starts false

        for (List<BpmnNodeConditionsConfVueVo> group : conditionList) {
            if (CollectionUtils.isEmpty(group)) {
                continue;
            }
            boolean groupResult = evaluateConditionGroup(group, formFields);

            if (isOrBetweenGroups) {
                overallResult = overallResult || groupResult;
                if (overallResult) break; // OR: first true wins
            } else {
                overallResult = overallResult && groupResult;
                if (!overallResult) break; // AND: first false wins
            }
        }
        return overallResult;
    }

    /**
     * Evaluate a single condition group.
     */
    private static boolean evaluateConditionGroup(List<BpmnNodeConditionsConfVueVo> group,
                                                  Map<String, Object> formFields) {
        Boolean condRelation = group.get(0).getCondRelation();
        boolean isOrWithinGroup = Boolean.TRUE.equals(condRelation);
        boolean groupResult = !isOrWithinGroup;

        for (BpmnNodeConditionsConfVueVo item : group) {
            boolean itemResult = evaluateSingleCondition(item, formFields);
            if (isOrWithinGroup) {
                groupResult = groupResult || itemResult;
                if (groupResult) break;
            } else {
                groupResult = groupResult && itemResult;
                if (!groupResult) break;
            }
        }
        return groupResult;
    }

    /**
     * Evaluate a single condition item against form fields.
     */
    private static boolean evaluateSingleCondition(BpmnNodeConditionsConfVueVo item,
                                                   Map<String, Object> formFields) {
        String fieldName = item.getColumnDbname();
        if (!StringUtils.hasText(fieldName)) {
            return false;
        }
        Object formValue = formFields.get(fieldName);
        String formValueStr = formValue != null ? formValue.toString() : "";
        String targetValue = item.getZdy1() != null ? item.getZdy1() : "";

        String fieldTypeName = item.getFieldTypeName();
        Integer optType = item.getOptType();

        // Switch type: compare boolean-like values
        if ("switch".equals(fieldTypeName)) {
            return "1".equals(formValueStr) == "1".equals(targetValue);
        }

        // Select / Radio: equality check
        if ("select".equals(fieldTypeName) || "radio".equals(fieldTypeName)) {
            return targetValue.equals(formValueStr);
        }

        // Checkbox: check if form value collection contains the target element
        if ("checkbox".equals(fieldTypeName)) {
            if (!StringUtils.hasText(formValueStr) || !StringUtils.hasText(targetValue)) {
                return false;
            }
            return Arrays.asList(formValueStr.split(",")).contains(targetValue);
        }

        // Numeric / Date / Time comparisons using optType
        try {
            if ("number".equals(fieldTypeName) || "date".equals(fieldTypeName) || "time".equals(fieldTypeName)) {
                return compareNumeric(formValueStr, targetValue, optType, item.getZdy2(), item.getOpt1(), item.getOpt2());
            }
        } catch (NumberFormatException e) {
            log.debug("Numeric comparison failed for field {}: {}", fieldName, e.getMessage());
        }

        // Default: string equality
        return targetValue.equals(formValueStr);
    }

    /**
     * Numeric comparison supporting: >=, >, <=, <, ==, between.
     * optType: 1=>=, 2=>, 3=<=, 4=<, 5===, 6=between(zdy1 < x < zdy2)
     */
    private static boolean compareNumeric(String formValueStr, String targetValue, Integer optType,
                                          String zdy2, String opt1, String opt2) {
        if (!StringUtils.hasText(formValueStr) || !StringUtils.hasText(targetValue)) {
            return false;
        }
        double formVal = Double.parseDouble(formValueStr);
        double target = Double.parseDouble(targetValue);

        if (optType == null) return formVal == target;

        switch (optType) {
            case 1: return formVal >= target;
            case 2: return formVal > target;
            case 3: return formVal <= target;
            case 4: return formVal < target;
            case 5: return formVal == target;
            case 6:
            case 7:
            case 8:
            case 9:
                // Between: zdy1 opt1 x opt2 zdy2
                if (!StringUtils.hasText(zdy2)) return false;
                double target2 = Double.parseDouble(zdy2);
                boolean leftBound = "<".equals(opt1) ? formVal > target : formVal >= target;
                boolean rightBound = "<".equals(opt2) ? formVal < target2 : formVal <= target2;
                return leftBound && rightBound;
            default:
                return formVal == target;
        }
    }
}
