package org.openoa.engine.bpmnconf.service.biz.callback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.BusinessCallbackEnum;

import static org.junit.jupiter.api.Assertions.*;

class BusinessCallBackFactoryTest extends BaseTest {

    @Nested
    @DisplayName("build")
    class BuildTest {
        @Test
        @DisplayName("should return singleton instance")
        void shouldReturnSingleton() {
            BusinessCallBackFactory instance1 = BusinessCallBackFactory.build();
            BusinessCallBackFactory instance2 = BusinessCallBackFactory.build();
            assertNotNull(instance1);
            assertSame(instance1, instance2);
        }
    }

    @Nested
    @DisplayName("doCallBacks")
    class DoCallBacksTest {
        @Test
        @DisplayName("should not throw for null params with valid enum")
        void shouldNotThrowForNullParams() {
            BusinessCallBackFactory factory = BusinessCallBackFactory.build();
            assertDoesNotThrow(() -> factory.doCallBacks(null, BusinessCallbackEnum.PROCESS_EVENT_CALLBACK));
        }

        @Test
        @DisplayName("should throw NPE for null enum")
        void shouldThrowForNullEnum() {
            BusinessCallBackFactory factory = BusinessCallBackFactory.build();
            assertThrows(NullPointerException.class, () -> factory.doCallBacks("test", null));
        }
    }

    @Nested
    @DisplayName("doCallBack")
    class DoCallBackTest {
        @Test
        @DisplayName("should not throw for null enum")
        void shouldNotThrowForNullEnum() {
            BusinessCallBackFactory factory = BusinessCallBackFactory.build();
            assertDoesNotThrow(() -> factory.doCallBack("test", null, 1));
        }
    }
}
