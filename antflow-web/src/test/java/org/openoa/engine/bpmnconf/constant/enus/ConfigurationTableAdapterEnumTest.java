package org.openoa.engine.bpmnconf.constant.enus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTableAdapterEnumTest extends BaseTest {

    @Nested
    @DisplayName("getByTableFieldEnum")
    class GetByTableFieldEnumTest {
        @Test
        @DisplayName("should find FINANCE_CASHER_ADP by FINANCE_CASHER")
        void shouldFindFinanceCasherAdp() {
            ConfigurationTableAdapterEnum result = ConfigurationTableAdapterEnum.getByTableFieldEnum(
                    BusinessConfTableFieldEnum.FINANCE_CASHER);

            assertNotNull(result);
            assertEquals(ConfigurationTableAdapterEnum.FINANCE_CASHER_ADP, result);
        }

        @Test
        @DisplayName("should find FINANCE_CFO_ADP by FINANCE_CFO")
        void shouldFindFinanceCfoAdp() {
            ConfigurationTableAdapterEnum result = ConfigurationTableAdapterEnum.getByTableFieldEnum(
                    BusinessConfTableFieldEnum.FINANCE_CFO);

            assertNotNull(result);
            assertEquals(ConfigurationTableAdapterEnum.FINANCE_CFO_ADP, result);
        }

        @Test
        @DisplayName("should return null for null table field enum")
        void shouldReturnNullForNull() {
            assertNull(ConfigurationTableAdapterEnum.getByTableFieldEnum(null));
        }

        @Test
        @DisplayName("each adapter should reference correct table field enum")
        void eachAdapterShouldReferenceCorrectFieldEnum() {
            for (ConfigurationTableAdapterEnum adapter : ConfigurationTableAdapterEnum.values()) {
                ConfigurationTableAdapterEnum found = ConfigurationTableAdapterEnum.getByTableFieldEnum(
                        adapter.getTableFieldEnum());
                assertSame(adapter, found,
                        adapter.name() + " should be found by its tableFieldEnum " + adapter.getTableFieldEnum().name());
            }
        }
    }

    @Nested
    @DisplayName("enum values consistency")
    class EnumConsistencyTest {
        @Test
        @DisplayName("all enum values should have non-null tableFieldEnum")
        void shouldHaveNonNullTableFieldEnum() {
            for (ConfigurationTableAdapterEnum e : ConfigurationTableAdapterEnum.values()) {
                assertNotNull(e.getTableFieldEnum(), "Null tableFieldEnum for " + e.name());
            }
        }

        @Test
        @DisplayName("all adapter enums should map to distinct table field enums")
        void shouldMapToDistinctFieldEnums() {
            java.util.Set<BusinessConfTableFieldEnum> fieldEnums = new java.util.HashSet<>();
            for (ConfigurationTableAdapterEnum e : ConfigurationTableAdapterEnum.values()) {
                assertTrue(fieldEnums.add(e.getTableFieldEnum()),
                        "Duplicate tableFieldEnum mapping: " + e.getTableFieldEnum().name());
            }
        }
    }
}
