package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.JudgeOperatorEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 退回次数条件判断器.
 * <p>
 * 从 conditionsConf.returnCount 读取用户配置的阈值,从 Activiti act_ru_variable
 * 中读取流程实例的 returnCount 变量值(null 当 0),使用 AbstractComparableJudge
 * 提供的 9 种运算符进行比较.
 * <p>
 * 支持区间运算(optType 6-9):returnCount 存 "lower,upper" 格式.
 */
@Slf4j
@Service
public class ReturnCountConditionJudge extends AbstractComparableJudge {

    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf,
                         BpmnStartConditionsVo bpmnStartConditionsVo, int group, int index) {
        String returnCountConf = conditionsConf.getReturnCount();
        if (!StringUtils.hasText(returnCountConf)) {
            return false;
        }
        Integer operator = conditionsConf.getNumberOperator();
        if (operator == null) {
            log.warn("returnCount condition has no numberOperator, nodeId={}", nodeId);
            return false;
        }

        // 读取运行时退回次数
        String processNum = bpmnStartConditionsVo.getProcessNum();
        BpmBusinessProcess process = bpmBusinessProcessService.getBpmBusinessProcess(processNum);
        if (process == null) {
            log.warn("returnCount judge: BpmBusinessProcess not found, processNum={}", processNum);
            return false;
        }
        String procInstId = process.getProcInstId();
        if(!StringUtils.hasText(procInstId)){
            return false;
        }
        Object varValue = runtimeService.getVariable(procInstId, StringConstants.RETURN_COUNT_VARIABLE_NAME);
        int actualCount = 0;
        if (varValue instanceof Number) {
            actualCount = ((Number) varValue).intValue();
        }

        // 解析阈值
        BigDecimal confTotal;
        BigDecimal confTotal2 = null;
        try {
            if (JudgeOperatorEnum.binaryOperator().contains(operator)) {
                String[] parts = returnCountConf.split(",");
                confTotal = new BigDecimal(parts[0].trim());
                confTotal2 = new BigDecimal(parts[1].trim());
            } else {
                confTotal = new BigDecimal(returnCountConf.trim());
            }
        } catch (Exception e) {
            log.error("returnCount condition parse failed, conf={}, nodeId={}", returnCountConf, nodeId, e);
            return false;
        }

        BigDecimal actual = new BigDecimal(actualCount);
        return super.compareJudge(confTotal, confTotal2, actual, operator);
    }
}
