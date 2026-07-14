package org.openoa.engine.bpmnconf.adp.processoperation;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeAutoNodeConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 此抽象类主要是为了维护AntFlow内部的数个demo流程,用户创建DIY流程建议按照文档[1.antflow-activiti快速上手指南一]里面的快速开始一个DIY流程里介绍的方法来,即实现FormOperationAdaptor
 * 如果直接继承本类,确定你已经明白了(或者大概明白了)每个方法做什么的
 * @param <T>
 */
@Slf4j
public abstract class AbstractFormOperationAdaptor<T extends BusinessDataVo>  implements FormOperationAdaptor<T>, ActivitiService {



    @Autowired
    private BpmnNodeService bpmnNodeService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @Override
    public void initData(T vo) {

    }


    @Override
    public void queryData(T vo) {

    }

    @Override
    public void submitData(T vo) {

    }

    @Override
    public void consentData(T vo) {

    }

    @Override
    public void backToModifyData(T vo) {

    }

    @Override
    public void cancellationData(T businessDataVo) {

    }

    @Override
    public void onProcessRecover(BusinessDataVo businessData) {

    }

    /**
     * 默认实现: 仅支持低代码流程 (UDLFApplyVo).
     * 从节点配置的 autoNodeConf 中读取条件, 对 lfFields 进行基础评估.
     * 非低代码流程 (DIY) 直接返回 false, 用户必须重写此方法.
     * 如果没有配置条件, 返回 null (无条件执行 automaticAction).
     */
    @Override
    public Boolean automaticCondition(BusinessDataVo vo) {
        // DIY/regular flows: users must override this method
        if (!(vo instanceof UDLFApplyVo)) {
            return false;
        }
        try {
            BpmnNodeAutoNodeConfJson autoNodeConf = loadAutoNodeConf(vo);
            if (autoNodeConf == null || CollectionUtils.isEmpty(autoNodeConf.getConditionList())) {
                return null;
            }
            Map<String, Object> lfFields = ((UDLFApplyVo) vo).getLfFields();
            if (CollectionUtils.isEmpty(lfFields)) {
                return false;
            }
            return evaluateConditions(autoNodeConf, lfFields);
        } catch (AFBizException e) {
            throw e;
        } catch (Exception e) {
            log.warn("automaticCondition evaluation failed, returning null: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Load auto node condition config from DB.
     */
    private BpmnNodeAutoNodeConfJson loadAutoNodeConf(BusinessDataVo vo) {

        String processNumber = vo.getProcessNumber();
        String taskDefKey = vo.getTaskDefKey();
        if (!StringUtils.hasText(processNumber) || !StringUtils.hasText(taskDefKey)) {
            return null;
        }
        // Find active BpmnConf by formCode
        BpmnConfBizService bpmnConfBizService = SpringBeanUtils.getBean(BpmnConfBizService.class);
        BpmnConf bpmnConf =bpmnConfBizService.getBpmnConfByFormCode(vo.getFormCode());
        if (bpmnConf == null) {
           throw  new AFBizException("cant not get bpmnconf by formcode+"+vo.getFormCode());
        }
        String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(vo.getProcessNumber(), vo.getTaskDefKey());
        // Find BpmnNode by confId and nodeId (nodeId is looked up from taskDefKey via getNodeIdByElementId)
        BpmnNode bpmnNode = bpmnNodeService.getOne(
                Wrappers.<BpmnNode>lambdaQuery()
                        .eq(BpmnNode::getConfId, bpmnConf.getId())
                        .eq(BpmnNode::getNodeId, nodeId)
                        .eq(BpmnNode::getIsDel, 0)
        );
        if (bpmnNode == null || !StringUtils.hasText(bpmnNode.getNodeConfigJson())) {
            return null;
        }
        BpmnNodeConfigJson configJson = JsonConfUtil.parseNodeConfig(bpmnNode.getNodeConfigJson());
        if (configJson == null) {
           return null;
        }
        return configJson.getAutoNodeConf();
    }

    /**
     * Evaluate auto node conditions against form fields.
     * <p>
     * NOTE: This is a simplified reimplementation of condition evaluation.
     * The design doc references {@link org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge}
     * implementations (LFStringConditionJudge, LFNumberFormatJudge, etc.), but those operate on
     * BpmnNodeConditionsConfBaseVo (processed format) while auto node stores conditions in
     * BpmnNodeConditionsConfVueVo (raw frontend format). Reusing ConditionJudge directly would
     * require a non-trivial format conversion. Consider refactoring to reuse existing judges
     * if condition semantics need to stay perfectly aligned with condition nodes.
     */
    private Boolean evaluateConditions(BpmnNodeAutoNodeConfJson autoNodeConf, Map<String, Object> formFields) {
        List<List<BpmnNodeConditionsConfVueVo>> conditionList = autoNodeConf.getConditionList();
        if (CollectionUtils.isEmpty(conditionList)) {
            return null;
        }

        Boolean groupRelation = autoNodeConf.getGroupRelation();
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
    private boolean evaluateConditionGroup(List<BpmnNodeConditionsConfVueVo> group,
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
    private boolean evaluateSingleCondition(BpmnNodeConditionsConfVueVo item,
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
    private boolean compareNumeric(String formValueStr, String targetValue, Integer optType,
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

    @Override
    public void automaticAction(BusinessDataVo autoActionDto,Boolean conditionResult) {

    }
}
