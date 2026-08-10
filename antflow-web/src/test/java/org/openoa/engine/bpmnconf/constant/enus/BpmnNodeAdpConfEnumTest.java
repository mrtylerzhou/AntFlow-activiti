package org.openoa.engine.bpmnconf.constant.enus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeAdpConfEnumTest extends BaseTest {

    @Nested
    @DisplayName("getBpmnNodeAdpConfEnumByEnum")
    class GetByEnumTest {
        @Test
        @DisplayName("should find conf by NodePropertyEnum")
        void shouldFindByNodePropertyEnum() {
            BpmnNodeAdpConfEnum result = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(NodePropertyEnum.NODE_PROPERTY_PERSONNEL);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_PERSONNEL, result);
        }

        @Test
        @DisplayName("should find conf by NodeTypeEnum")
        void shouldFindByNodeTypeEnum() {
            BpmnNodeAdpConfEnum result = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(NodeTypeEnum.NODE_TYPE_CONDITIONS);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_CONDITIONS, result);
        }

        @Test
        @DisplayName("should return null for unknown enum")
        void shouldReturnNullForUnknown() {
            BpmnNodeAdpConfEnum result = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(null);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getBpmnNodeAdpConfWithPersonnels")
    class GetWithPersonnelsTest {
        @Test
        @DisplayName("should return only entries with hasPropertyTable == 1")
        void shouldReturnOnlyPersonnelEntries() {
            List<BpmnNodeAdpConfEnum> result = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfWithPersonnels();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            for (BpmnNodeAdpConfEnum conf : result) {
                assertTrue(conf.getAnEnum() instanceof NodePropertyEnum,
                        conf.name() + " should be NodePropertyEnum");
            }
        }
    }

    @Nested
    @DisplayName("enum values consistency")
    class EnumConsistencyTest {
        @Test
        @DisplayName("all enum values should have non-null anEnum")
        void shouldHaveNonNullEnum() {
            for (BpmnNodeAdpConfEnum e : BpmnNodeAdpConfEnum.values()) {
                assertNotNull(e.getAnEnum(), "Null anEnum for " + e.name());
            }
        }
    }
}
