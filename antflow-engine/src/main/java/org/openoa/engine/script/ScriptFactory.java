package org.openoa.engine.script;

import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.juel.SimpleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

@Service
public class ScriptFactory {

    @Bean
    SimpleContext simpleContext() {
        return new SimpleContext();
    }
    @Bean
    ExpressionFactory expressionFactory(){
        return org.activiti.engine.impl.javax.el.ExpressionFactory.newInstance();
    }
    @Bean
    EvaluationContext evaluationContext(){
        return new StandardEvaluationContext();
    }
    @Bean
    ExpressionParser expressionParser(){
        return new SpelExpressionParser();
    }
}
