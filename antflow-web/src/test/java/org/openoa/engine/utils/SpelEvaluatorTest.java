package org.openoa.engine.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class SpelEvaluatorTest extends MockBaseTest {

    @Nested
    @DisplayName("low-code flow")
    class LowCodeFlowTest {

        @Test
        @DisplayName("should return true when expression evaluates to true")
        void shouldReturnTrueWhenExpressionEvaluatesToTrue() {
            ExpressionParser parser = new SpelExpressionParser();
            try (MockedStatic<SpringBeanUtils> mocked = mockStatic(SpringBeanUtils.class)) {
                mocked.when(() -> SpringBeanUtils.getBean(ExpressionParser.class)).thenReturn(parser);

                Map<String, Object> lfConditions = new HashMap<>();
                lfConditions.put("amount", 150);
                BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = SpelEvaluator.evaluate("#amount > 100", vo);
                assertTrue(result);
            }
        }

        @Test
        @DisplayName("should return false when expression evaluates to false")
        void shouldReturnFalseWhenExpressionEvaluatesToFalse() {
            ExpressionParser parser = new SpelExpressionParser();
            try (MockedStatic<SpringBeanUtils> mocked = mockStatic(SpringBeanUtils.class)) {
                mocked.when(() -> SpringBeanUtils.getBean(ExpressionParser.class)).thenReturn(parser);

                Map<String, Object> lfConditions = new HashMap<>();
                lfConditions.put("amount", 150);
                BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = SpelEvaluator.evaluate("#amount < 100", vo);
                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("standard flow")
    class StandardFlowTest {

        @Test
        @DisplayName("should evaluate expression using businessDataVo as it variable")
        void shouldEvaluateUsingBusinessDataVo() {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();
            try (MockedStatic<SpringBeanUtils> mocked = mockStatic(SpringBeanUtils.class)) {
                mocked.when(() -> SpringBeanUtils.getBean(ExpressionParser.class)).thenReturn(parser);
                mocked.when(() -> SpringBeanUtils.getBean(EvaluationContext.class)).thenReturn(context);

                BusinessDataVo businessDataVo = BusinessDataVo.builder()
                        .processNumber("PROC-001")
                        .processTitle("Test Process")
                        .build();
                BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(false)
                        .businessDataVo(businessDataVo)
                        .build();

                boolean result = SpelEvaluator.evaluate("#it.processNumber == 'PROC-001'", vo);
                assertTrue(result);
            }
        }

        @Test
        @DisplayName("should return false when standard flow expression is false")
        void shouldReturnFalseWhenStandardFlowExpressionIsFalse() {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();
            try (MockedStatic<SpringBeanUtils> mocked = mockStatic(SpringBeanUtils.class)) {
                mocked.when(() -> SpringBeanUtils.getBean(ExpressionParser.class)).thenReturn(parser);
                mocked.when(() -> SpringBeanUtils.getBean(EvaluationContext.class)).thenReturn(context);

                BusinessDataVo businessDataVo = BusinessDataVo.builder()
                        .processNumber("PROC-001")
                        .processTitle("Test Process")
                        .build();
                BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(false)
                        .businessDataVo(businessDataVo)
                        .build();

                boolean result = SpelEvaluator.evaluate("#it.processNumber == 'PROC-999'", vo);
                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("null and false results")
    class NullAndFalseResultsTest {

        @Test
        @DisplayName("should return false when expression evaluates to null")
        void shouldReturnFalseWhenExpressionEvaluatesToNull() {
            ExpressionParser parser = new SpelExpressionParser();
            try (MockedStatic<SpringBeanUtils> mocked = mockStatic(SpringBeanUtils.class)) {
                mocked.when(() -> SpringBeanUtils.getBean(ExpressionParser.class)).thenReturn(parser);

                Map<String, Object> lfConditions = new HashMap<>();
                BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = SpelEvaluator.evaluate("null", vo);
                assertFalse(result);
            }
        }

        @Test
        @DisplayName("should return false when expression evaluates to false")
        void shouldReturnFalseWhenExpressionEvaluatesToFalseDirectly() {
            ExpressionParser parser = new SpelExpressionParser();
            try (MockedStatic<SpringBeanUtils> mocked = mockStatic(SpringBeanUtils.class)) {
                mocked.when(() -> SpringBeanUtils.getBean(ExpressionParser.class)).thenReturn(parser);

                Map<String, Object> lfConditions = new HashMap<>();
                BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder()
                        .isLowCodeFlow(true)
                        .lfConditions(lfConditions)
                        .build();

                boolean result = SpelEvaluator.evaluate("false", vo);
                assertFalse(result);
            }
        }
    }
}
