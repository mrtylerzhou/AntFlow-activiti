package org.openoa.engine.utils;

import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.constant.AntFlowConstants;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class SpelEvaluator {
    public static boolean evaluate(String expression, BpmnStartConditionsVo startConditionsVo) {
        ExpressionParser expressionParser = SpringBeanUtils.getBean(ExpressionParser.class);
        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        EvaluationContext evaluationContext =null;
        if(startConditionsVo.isLowCodeFlow()){
            evaluationContext = new StandardEvaluationContext();
            Map<String, Object> lfConditions = startConditionsVo.getLfConditions();
            for (Map.Entry<String, Object> keyValuePair : lfConditions.entrySet()) {
                evaluationContext.setVariable(keyValuePair.getKey(),keyValuePair.getValue());
            }
        }else{
            evaluationContext = SpringBeanUtils.getBean(EvaluationContext.class);
            evaluationContext.setVariable(AntFlowConstants.SCRIPT_CONTEXT, businessDataVo);
        }
        Boolean evaluatedResult = expressionParser.parseExpression(expression).getValue(evaluationContext,Boolean.class);
        return Boolean.TRUE.equals(evaluatedResult);
    }
}
