package org.openoa.base.util;

import org.activiti.engine.impl.el.FixedValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.SignatureRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionAndEnvUtilTest extends BaseTest {

    @AfterEach
    void cleanup() {
        ThreadLocalContainer.clean();
    }

    @Nested
    @DisplayName("ExpressionUtils")
    class ExpressionUtilsTest {
        @Test
        @DisplayName("stringToExpression should return FixedValue")
        void shouldReturnFixedValue() {
            Object expr = ExpressionUtils.stringToExpression("hello");
            assertTrue(expr instanceof FixedValue);
        }

        @Test
        @DisplayName("stringToExpressionSet should split by semicolon")
        void shouldSplitBySemicolon() {
            Set<?> set = ExpressionUtils.stringToExpressionSet("a;b;c");
            assertEquals(3, set.size());
        }

        @Test
        @DisplayName("stringToExpressionSet with single value")
        void shouldHandleSingleValue() {
            Set<?> set = ExpressionUtils.stringToExpressionSet("only");
            assertEquals(1, set.size());
        }

        @Test
        @DisplayName("stringToExpressionSet should handle empty parts")
        void shouldHandleEmptyParts() {
            Set<?> set = ExpressionUtils.stringToExpressionSet("a;;c");
            assertEquals(3, set.size());
        }
    }

    @Nested
    @DisplayName("EnvUtil")
    class EnvUtilTest {
        @Test
        @DisplayName("should return new SignatureRequest when not set")
        void shouldReturnNewWhenNotSet() {
            SignatureRequest req = EnvUtil.getSignatureReq();
            assertNotNull(req);
        }

        @Test
        @DisplayName("should return set SignatureRequest")
        void shouldReturnSetRequest() {
            SignatureRequest original = SignatureRequest.builder()
                    .systemType("iOS")
                    .appVersion("1.0")
                    .build();
            ThreadLocalContainer.set("SIGNATURE_REQUEST", original);
            SignatureRequest req = EnvUtil.getSignatureReq();
            assertSame(original, req);
            assertEquals("iOS", req.getSystemType());
        }

        @Test
        @DisplayName("should return new SignatureRequest after clean")
        void shouldReturnNewAfterClean() {
            SignatureRequest original = SignatureRequest.builder()
                    .systemType("Android")
                    .build();
            ThreadLocalContainer.set("SIGNATURE_REQUEST", original);
            ThreadLocalContainer.clean();
            SignatureRequest req = EnvUtil.getSignatureReq();
            assertNotNull(req);
            assertNotSame(original, req);
            assertNull(req.getSystemType());
        }
    }
}
