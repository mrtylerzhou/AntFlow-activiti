package org.openoa.engine.bpmnconf.adp.processnotice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.UserMsgVo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AbstractMessageSendAdaptorTest extends BaseTest {

    private static class TestAdaptor extends AbstractMessageSendAdaptor<String> {
        @Override
        public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        }

        @Override
        public void setSupportBusinessObjects() {
        }

        public Map<String, String> callMessageProcessing(List<UserMsgVo> userMsgVos, java.util.function.Function<UserMsgVo, String> fun) {
            return messageProcessing(userMsgVos, fun);
        }
    }

    private final TestAdaptor adaptor = new TestAdaptor();

    @Nested
    @DisplayName("messageProcessing")
    class MessageProcessingTest {
        @Test
        @DisplayName("should return null for empty list")
        void shouldReturnNullForEmpty() {
            Map<String, String> result = adaptor.callMessageProcessing(
                    Collections.emptyList(),
                    vo -> "result"
            );
            assertNull(result);
        }

        @Test
        @DisplayName("should return null for null list")
        void shouldReturnNullForNull() {
            Map<String, String> result = adaptor.callMessageProcessing(
                    null,
                    vo -> "result"
            );
            assertNull(result);
        }

        @Test
        @DisplayName("should apply function to each UserMsgVo")
        void shouldApplyFunction() {
            UserMsgVo vo1 = UserMsgVo.builder().userId("user1").build();
            UserMsgVo vo2 = UserMsgVo.builder().userId("user2").build();
            Map<String, String> result = adaptor.callMessageProcessing(
                    Arrays.asList(vo1, vo2),
                    vo -> "processed_" + vo.getUserId()
            );
            assertEquals(2, result.size());
            assertEquals("processed_user1", result.get("user1"));
            assertEquals("processed_user2", result.get("user2"));
        }

        @Test
        @DisplayName("should handle single element list")
        void shouldHandleSingleElement() {
            UserMsgVo vo = UserMsgVo.builder().userId("single").build();
            Map<String, String> result = adaptor.callMessageProcessing(
                    Collections.singletonList(vo),
                    v -> "ok"
            );
            assertEquals(1, result.size());
            assertEquals("ok", result.get("single"));
        }
    }

    @Nested
    @DisplayName("messageProcessing via reflection")
    class MessageProcessingReflectionTest {
        @Test
        @DisplayName("should invoke messageProcessing via reflection")
        void shouldInvokeViaReflection() throws Exception {
            TestAdaptor testAdaptor = new TestAdaptor();
            Method method = AbstractMessageSendAdaptor.class.getDeclaredMethod(
                    "messageProcessing", List.class, java.util.function.Function.class);
            method.setAccessible(true);

            UserMsgVo vo = UserMsgVo.builder().userId("refl").build();
            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) method.invoke(
                    testAdaptor,
                    Collections.singletonList(vo),
                    (java.util.function.Function<UserMsgVo, String>) v -> "refl_result"
            );
            assertEquals(1, result.size());
            assertEquals("refl_result", result.get("refl"));
        }
    }
}
