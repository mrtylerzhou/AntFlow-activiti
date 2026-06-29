package org.openoa.engine.utils;

import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class JuelEvaluatorTest extends MockBaseTest {

    @Nested
    @DisplayName("low-code flow evaluation")
    class LowCodeFlowTest {

        @Test
        @DisplayName("should return true when low-code expression evaluates to true")
        void shouldReturnTrueWhenLowCodeExpressionEvaluatesToTrue() {
            ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(ExpressionFactory.class))
                        .thenReturn(expressionFactory);

                Map<String, Object> lfConditions = new HashMap<>();
                lfConditions.put("amount", 150);
                BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = JuelEvaluator.evaluate("${amount > 100}", startConditionsVo);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("should return false when low-code expression evaluates to false")
        void shouldReturnFalseWhenLowCodeExpressionEvaluatesToFalse() {
            ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(ExpressionFactory.class))
                        .thenReturn(expressionFactory);

                Map<String, Object> lfConditions = new HashMap<>();
                lfConditions.put("amount", 150);
                BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = JuelEvaluator.evaluate("${amount < 100}", startConditionsVo);

                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("standard flow evaluation")
    class StandardFlowTest {

        @Test
        @DisplayName("should evaluate standard flow expression using businessDataVo as it variable")
        void shouldEvaluateStandardFlowExpression() {
            ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
            SimpleContext simpleContext = new SimpleContext();
            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(ExpressionFactory.class))
                        .thenReturn(expressionFactory);
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(SimpleContext.class))
                        .thenReturn(simpleContext);

                BusinessDataVo businessDataVo = BusinessDataVo.builder()
                        .processNumber("PROC-001")
                        .build();
                BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(false)
                        .businessDataVo(businessDataVo)
                        .build();

                boolean result = JuelEvaluator.evaluate("${it.processNumber eq 'PROC-001'}", startConditionsVo);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("should return false when standard flow expression does not match")
        void shouldReturnFalseWhenStandardFlowExpressionDoesNotMatch() {
            ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
            SimpleContext simpleContext = new SimpleContext();
            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(ExpressionFactory.class))
                        .thenReturn(expressionFactory);
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(SimpleContext.class))
                        .thenReturn(simpleContext);

                BusinessDataVo businessDataVo = BusinessDataVo.builder()
                        .processNumber("PROC-001")
                        .build();
                BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(false)
                        .businessDataVo(businessDataVo)
                        .build();

                boolean result = JuelEvaluator.evaluate("${it.processNumber eq 'PROC-999'}", startConditionsVo);

                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("null and false result handling")
    class NullAndFalseResultTest {

        @Test
        @DisplayName("should return false when expression evaluates to null")
        void shouldReturnFalseWhenExpressionEvaluatesToNull() {
            ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(ExpressionFactory.class))
                        .thenReturn(expressionFactory);

                Map<String, Object> lfConditions = new HashMap<>();
                lfConditions.put("amount", 150);
                BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = JuelEvaluator.evaluate("${amount > 100 and amount < 100}", startConditionsVo);

                assertFalse(result);
            }
        }

        @Test
        @DisplayName("should return false when expression evaluates to false")
        void shouldReturnFalseWhenExpressionEvaluatesToFalse() {
            ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBean(ExpressionFactory.class))
                        .thenReturn(expressionFactory);

                Map<String, Object> lfConditions = new HashMap<>();
                lfConditions.put("amount", 50);
                BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = JuelEvaluator.evaluate("${amount > 100}", startConditionsVo);

                assertFalse(result);
            }
        }
    }
}
