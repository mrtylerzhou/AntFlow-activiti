package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.engine.utils.JuelEvaluator;
import org.springframework.stereotype.Service;

@Service
public class JuelExpressionConditionJudge implements ConditionJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        String expression = conditionsConf.getExpression();
        return JuelEvaluator.evaluate(expression, bpmnStartConditionsVo);
    }
}
