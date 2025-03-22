package org.openoa.engine.script;

import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class JuelFactory {

    @Bean
    SimpleContext simpleContext() {
        return new SimpleContext();
    }
    @Bean
    ExpressionFactory expressionFactory(){
        return ExpressionFactory.newInstance();
    }
}
