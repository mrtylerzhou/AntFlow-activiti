package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

/**
 * money operator judge
 * @author AntFlow
 * @since 0.5
 */
@Service
@Slf4j
public class MoneyOperatorJudge implements ConditionJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo,int index) {
        return true;
    }
}
