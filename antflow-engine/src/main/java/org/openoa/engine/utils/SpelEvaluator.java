package org.openoa.engine.utils;

import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.constant.AntFlowConstants;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public class SpelEvaluator {
    public static boolean evaluate(String expression, BpmnStartConditionsVo startConditionsVo) {
        ExpressionParser expressionParser = SpringBeanUtils.getBean(ExpressionParser.class);
        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        EvaluationContext evaluationContext;
        if(startConditionsVo.isLowCodeFlow()){
            Map<String, Object> rootMap = new HashMap<>(startConditionsVo.getLfConditions());
            StandardEvaluationContext ctx = new StandardEvaluationContext(rootMap);
            ctx.addPropertyAccessor(new MapAccessor());
            evaluationContext = ctx;
        }else{
            evaluationContext = new StandardEvaluationContext();
            evaluationContext.setVariable(AntFlowConstants.SCRIPT_CONTEXT, businessDataVo);
        }
        Boolean evaluatedResult = expressionParser.parseExpression(expression).getValue(evaluationContext,Boolean.class);
        return Boolean.TRUE.equals(evaluatedResult);
    }
}
