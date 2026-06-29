package org.openoa.base.util;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.el.FixedValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionUtilsTest extends BaseTest {

    @Nested
    @DisplayName("stringToExpression")
    class StringToExpressionTest {
        @Test
        @DisplayName("should wrap string in FixedValue expression")
        void shouldWrapInFixedValue() {
            Expression expr = ExpressionUtils.stringToExpression("testExpr");
            assertNotNull(expr);
            assertTrue(expr instanceof FixedValue);
        }

        @Test
        @DisplayName("should preserve expression value")
        void shouldPreserveValue() {
            Expression expr = ExpressionUtils.stringToExpression("hello");
            assertEquals("hello", expr.getExpressionText());
        }

        @Test
        @DisplayName("should handle empty string")
        void emptyString() {
            Expression expr = ExpressionUtils.stringToExpression("");
            assertNotNull(expr);
            assertEquals("", expr.getExpressionText());
        }
    }

    @Nested
    @DisplayName("stringToExpressionSet")
    class StringToExpressionSetTest {
        @Test
        @DisplayName("should split by semicolon and create expression set")
        void splitBySemicolon() {
            Set<Expression> set = ExpressionUtils.stringToExpressionSet("a;b;c");
            assertEquals(3, set.size());
        }

        @Test
        @DisplayName("should handle single expression")
        void singleExpression() {
            Set<Expression> set = ExpressionUtils.stringToExpressionSet("onlyOne");
            assertEquals(1, set.size());
            assertEquals("onlyOne", set.iterator().next().getExpressionText());
        }

        @Test
        @DisplayName("should maintain insertion order via LinkedHashSet")
        void insertionOrder() {
            Set<Expression> set = ExpressionUtils.stringToExpressionSet("first;second;third");
            java.util.List<String> texts = new java.util.ArrayList<>();
            for (Expression e : set) {
                texts.add(e.getExpressionText());
            }
            assertEquals(java.util.Arrays.asList("first", "second", "third"), texts);
        }

        @Test
        @DisplayName("should handle trailing semicolon producing empty string")
        void trailingSemicolon() {
            Set<Expression> set = ExpressionUtils.stringToExpressionSet("a;b;");
            assertTrue(set.size() >= 2);
        }
    }
}
