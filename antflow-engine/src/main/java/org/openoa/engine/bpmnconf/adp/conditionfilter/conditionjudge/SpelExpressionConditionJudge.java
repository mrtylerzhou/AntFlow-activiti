package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.engine.utils.SpelEvaluator;
import org.springframework.stereotype.Service;

@Service
public class SpelExpressionConditionJudge implements ConditionJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {

        return SpelEvaluator.evaluate(conditionsConf.getExpression(), bpmnStartConditionsVo);
    }
}
