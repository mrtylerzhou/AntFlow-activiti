package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.adp.SecurityAccountDeviceFilterDataAdp;

import static org.junit.jupiter.api.Assertions.*;

class Round18EnumsBTest extends BaseTest {

    @Nested
    @DisplayName("FilterDataEnum")
    class FilterDataEnumTest {
        @Test
        @DisplayName("FD_SECURITY_ACCOUNT_DEVICE should have code 1")
        void securityAccountDevice() {
            assertEquals(1, FilterDataEnum.FD_SECURITY_ACCOUNT_DEVICE.getCode());
            assertEquals("账号与设备关联表漏斗数据", FilterDataEnum.FD_SECURITY_ACCOUNT_DEVICE.getDesc());
        }

        @Test
        @DisplayName("filterDataService should be SecurityAccountDeviceFilterDataAdp")
        void filterDataService() {
            assertEquals(SecurityAccountDeviceFilterDataAdp.class,
                    FilterDataEnum.FD_SECURITY_ACCOUNT_DEVICE.getFilterDataService());
        }

        @Test
        @DisplayName("getFilterDataServiceByCode should return correct class")
        void getByCode() {
            assertEquals(SecurityAccountDeviceFilterDataAdp.class,
                    FilterDataEnum.getFilterDataServiceByCode(1));
        }

        @Test
        @DisplayName("getFilterDataServiceByCode should return null for unknown code")
        void getByCodeUnknown() {
            assertNull(FilterDataEnum.getFilterDataServiceByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessEnum")
    class ProcessEnumTest {
        @Test
        @DisplayName("AGENCY_TYPE should have code 1")
        void agencyType() {
            assertEquals(1, ProcessEnum.AGENCY_TYPE.getCode());
            assertEquals("委托流程", ProcessEnum.AGENCY_TYPE.getDesc());
        }

        @Test
        @DisplayName("LAUNCH_TYPE should have code 3")
        void launchType() {
            assertEquals(3, ProcessEnum.LAUNCH_TYPE.getCode());
            assertEquals("发起流程", ProcessEnum.LAUNCH_TYPE.getDesc());
        }

        @Test
        @DisplayName("HOST_TYPE should have code 1 (same as AGENCY_TYPE)")
        void hostType() {
            assertEquals(1, ProcessEnum.HOST_TYPE.getCode());
            assertEquals("承办类型", ProcessEnum.HOST_TYPE.getDesc());
        }

        @Test
        @DisplayName("START_TASK_KEY should have code 71")
        void startTaskKey() {
            assertEquals(71, ProcessEnum.START_TASK_KEY.getCode());
        }

        @Test
        @DisplayName("getDescByCode should return first match for duplicate codes")
        void getDescByCodeDuplicate() {
            String desc = ProcessEnum.getDescByCode(1);
            assertNotNull(desc);
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessEnum.getDescByCode(999));
        }
    }

    @Nested
    @DisplayName("ProcessBusinessCallBackTypeEnum")
    class ProcessBusinessCallBackTypeEnumTest {
        @Test
        @DisplayName("Send_MQ_Message should exist")
        void sendMqMessage() {
            assertNotNull(ProcessBusinessCallBackTypeEnum.Send_MQ_Message);
            assertEquals("发送事件消息到mq队列", ProcessBusinessCallBackTypeEnum.Send_MQ_Message.getDesc());
        }

        @Test
        @DisplayName("getEnumByCode should return correct enum")
        void getEnumByCode() {
            assertEquals(ProcessBusinessCallBackTypeEnum.Send_MQ_Message,
                    ProcessBusinessCallBackTypeEnum.getEnumByCode(
                            ProcessBusinessCallBackTypeEnum.Send_MQ_Message.getCode()));
        }

        @Test
        @DisplayName("getAdaptorByCode should return null")
        void getAdaptorByCode() {
            assertNull(ProcessBusinessCallBackTypeEnum.Send_MQ_Message.getAdaptorByCode(
                    ProcessBusinessCallBackTypeEnum.Send_MQ_Message.getCode()));
        }
    }

    @Nested
    @DisplayName("BusinessCallbackEnum")
    class BusinessCallbackEnumTest {
        @Test
        @DisplayName("PROCESS_EVENT_CALLBACK should have code 1")
        void processEventCallback() {
            assertEquals(1, BusinessCallbackEnum.PROCESS_EVENT_CALLBACK.getCode());
            assertEquals("流程类回调枚举", BusinessCallbackEnum.PROCESS_EVENT_CALLBACK.getDesc());
        }

        @Test
        @DisplayName("should have exactly 1 enum value")
        void valueCount() {
            assertEquals(1, BusinessCallbackEnum.values().length);
        }

        @Test
        @DisplayName("clsz should be ProcessBusinessCallBackTypeEnum")
        void clsz() {
            assertEquals(ProcessBusinessCallBackTypeEnum.class,
                    BusinessCallbackEnum.PROCESS_EVENT_CALLBACK.getClsz());
        }
    }

    @Nested
    @DisplayName("NodeTypeEnum")
    class NodeTypeEnumTest {
        @Test
        @DisplayName("should have values")
        void hasValues() {
            assertTrue(NodeTypeEnum.values().length > 0);
        }

        @Test
        @DisplayName("all values should have non-null code and desc")
        void allFieldsNonNull() {
            for (NodeTypeEnum e : NodeTypeEnum.values()) {
                assertNotNull(e.getCode(), e.name() + " code should not be null");
                assertNotNull(e.getDesc(), e.name() + " desc should not be null");
            }
        }
    }

    @Nested
    @DisplayName("JudgeOperatorEnum")
    class JudgeOperatorEnumTest {
        @Test
        @DisplayName("should have values")
        void hasValues() {
            assertTrue(JudgeOperatorEnum.values().length > 0);
        }

        @Test
        @DisplayName("all values should have non-null code and desc")
        void allFieldsNonNull() {
            for (JudgeOperatorEnum e : JudgeOperatorEnum.values()) {
                assertNotNull(e.getCode(), e.name() + " code should not be null");
                assertNotNull(e.getDesc(), e.name() + " desc should not be null");
            }
        }
    }

    @Nested
    @DisplayName("ErrLevelEnum")
    class ErrLevelEnumTest {
        @Test
        @DisplayName("should have values")
        void hasValues() {
            assertTrue(ErrLevelEnum.values().length > 0);
        }

        @Test
        @DisplayName("all values should have non-null code and desc")
        void allFieldsNonNull() {
            for (ErrLevelEnum e : ErrLevelEnum.values()) {
                assertNotNull(e.getCode(), e.name() + " code should not be null");
                assertNotNull(e.getDesc(), e.name() + " desc should not be null");
            }
        }
    }

    @Nested
    @DisplayName("FieldPermEnum")
    class FieldPermEnumTest {
        @Test
        @DisplayName("should have R, E, H values")
        void hasValues() {
            assertEquals(3, FieldPermEnum.values().length);
            assertNotNull(FieldPermEnum.R);
            assertNotNull(FieldPermEnum.E);
            assertNotNull(FieldPermEnum.H);
        }
    }

    @Nested
    @DisplayName("EventTypeEnum")
    class EventTypeEnumTest {
        @Test
        @DisplayName("should have values")
        void hasValues() {
            assertTrue(EventTypeEnum.values().length > 0);
        }

        @Test
        @DisplayName("all values should have non-null code and desc")
        void allFieldsNonNull() {
            for (EventTypeEnum e : EventTypeEnum.values()) {
                assertNotNull(e.getCode(), e.name() + " code should not be null");
                assertNotNull(e.getDesc(), e.name() + " desc should not be null");
            }
        }
    }

    @Nested
    @DisplayName("MissingAssigneeProcessStragtegyEnum")
    class MissingAssigneeProcessStragtegyEnumTest {
        @Test
        @DisplayName("should have values")
        void hasValues() {
            assertTrue(MissingAssigneeProcessStragtegyEnum.values().length > 0);
        }

        @Test
        @DisplayName("all values should have non-null code and desc")
        void allFieldsNonNull() {
            for (MissingAssigneeProcessStragtegyEnum e : MissingAssigneeProcessStragtegyEnum.values()) {
                assertNotNull(e.getCode(), e.name() + " code should not be null");
                assertNotNull(e.getDesc(), e.name() + " desc should not be null");
            }
        }
    }

    @Nested
    @DisplayName("DuplicationProcessStrategyEnum")
    class DuplicationProcessStrategyEnumTest {
        @Test
        @DisplayName("should have values")
        void hasValues() {
            assertTrue(DuplicationProcessStrategyEnum.values().length > 0);
        }

        @Test
        @DisplayName("all values should have non-null code and desc")
        void allFieldsNonNull() {
            for (DuplicationProcessStrategyEnum e : DuplicationProcessStrategyEnum.values()) {
                assertNotNull(e.getCode(), e.name() + " code should not be null");
                assertNotNull(e.getDesc(), e.name() + " desc should not be null");
            }
        }
    }
}
