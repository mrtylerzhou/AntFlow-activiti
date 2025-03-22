package org.openoa.engine.utils;

import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.juel.SimpleContext;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.constant.AntFlowConstants;

import java.util.Map;

public class JuelEvaluator {
    public static boolean evaluate(String expression, BpmnStartConditionsVo startConditionsVo) {
        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        ExpressionFactory expressionFactory = SpringBeanUtils.getBean(ExpressionFactory.class);
        SimpleContext simpleContext = null;
        boolean lowCodeFlow = startConditionsVo.isLowCodeFlow();
        if(lowCodeFlow){
            simpleContext=new SimpleContext();
            Map<String, Object> lfConditions = startConditionsVo.getLfConditions();
            for (Map.Entry<String, Object> keyValuePair : lfConditions.entrySet()) {
                simpleContext.setVariable(keyValuePair.getKey(),expressionFactory.createValueExpression(keyValuePair.getValue(), keyValuePair.getValue().getClass()));
            }
        }else{
            simpleContext=SpringBeanUtils.getBean(SimpleContext.class);
            simpleContext.setVariable(AntFlowConstants.SCRIPT_CONTEXT, expressionFactory.createValueExpression(businessDataVo, BusinessDataVo.class));
        }

        Boolean evaluatedResult = (Boolean) expressionFactory.createValueExpression(simpleContext, expression, Boolean.class).getValue(simpleContext);
        return Boolean.TRUE.equals(evaluatedResult);
    }
}
