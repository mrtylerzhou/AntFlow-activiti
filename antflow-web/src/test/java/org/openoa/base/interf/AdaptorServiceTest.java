package org.openoa.base.interf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.BusinessCallbackEnum;

import static org.junit.jupiter.api.Assertions.*;

class AdaptorServiceTest extends BaseTest {

    private static class TestAdaptor implements AdaptorService {
        @Override
        public void setSupportBusinessObjects() {
        }
    }

    @AfterEach
    void cleanup() {
        AdaptorService.SUPPORTED_BUSINESS.clear();
    }

    @Nested
    @DisplayName("addSupportBusinessObjects")
    class AddSupportBusinessObjectsTest {
        @Test
        @DisplayName("should add enum to supported list")
        void addEnum() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            String key = adaptor.getClass().getName();
            assertTrue(AdaptorService.SUPPORTED_BUSINESS.containsKey(key));
            assertEquals(1, AdaptorService.SUPPORTED_BUSINESS.get(key).size());
        }

        @Test
        @DisplayName("should add multiple enums at once")
        void addMultiple() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects(
                    BusinessCallbackEnum.PROCESS_EVENT_CALLBACK,
                    BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            String key = adaptor.getClass().getName();
            assertEquals(2, AdaptorService.SUPPORTED_BUSINESS.get(key).size());
        }

        @Test
        @DisplayName("should append to existing list on subsequent calls")
        void appendToExisting() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            adaptor.addSupportBusinessObjects(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            String key = adaptor.getClass().getName();
            assertEquals(2, AdaptorService.SUPPORTED_BUSINESS.get(key).size());
        }

        @Test
        @DisplayName("should handle null enums gracefully")
        void nullEnums() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects((Enum<?>[]) null);
            String key = adaptor.getClass().getName();
            assertFalse(AdaptorService.SUPPORTED_BUSINESS.containsKey(key));
        }

        @Test
        @DisplayName("should use marker in key")
        void withMarker() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects("marker1", BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            String keyWithMarker = adaptor.getClass().getName() + "marker1";
            String keyWithoutMarker = adaptor.getClass().getName();
            assertTrue(AdaptorService.SUPPORTED_BUSINESS.containsKey(keyWithMarker));
            assertFalse(AdaptorService.SUPPORTED_BUSINESS.containsKey(keyWithoutMarker));
        }
    }

    @Nested
    @DisplayName("isSupportBusinessObject")
    class IsSupportBusinessObjectTest {
        @Test
        @DisplayName("should return true for supported enum")
        void supportedEnum() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            assertTrue(adaptor.isSupportBusinessObject(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK));
        }

        @Test
        @DisplayName("should return false for unsupported enum")
        void unsupportedEnum() {
            TestAdaptor adaptor = new TestAdaptor();
            assertFalse(adaptor.isSupportBusinessObject(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK));
        }

        @Test
        @DisplayName("should work with marker")
        void withMarker() {
            TestAdaptor adaptor = new TestAdaptor();
            adaptor.addSupportBusinessObjects("m1", BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
            assertTrue(adaptor.isSupportBusinessObject("m1", BusinessCallbackEnum.PROCESS_EVENT_CALLBACK));
            assertFalse(adaptor.isSupportBusinessObject("m2", BusinessCallbackEnum.PROCESS_EVENT_CALLBACK));
        }

        @Test
        @DisplayName("should call setSupportBusinessObjects on miss")
        void callsSetSupportOnMiss() {
            TestAdaptor adaptor = new TestAdaptor() {
                @Override
                public void setSupportBusinessObjects() {
                    addSupportBusinessObjects(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK);
                }
            };
            assertFalse(AdaptorService.SUPPORTED_BUSINESS.containsKey(adaptor.getClass().getName()));
            assertTrue(adaptor.isSupportBusinessObject(BusinessCallbackEnum.PROCESS_EVENT_CALLBACK));
        }
    }
}
