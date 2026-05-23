package org.openoa.engine.bpmnconf.constant.enus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTableEnumTest extends BaseTest {

    @Nested
    @DisplayName("getInstanceByCode")
    class GetInstanceByCodeTest {
        @Test
        @DisplayName("should find COMPANY_FINANCE by code 1")
        void shouldFindCompanyFinanceByCode1() {
            ConfigurationTableEnum result = ConfigurationTableEnum.getInstanceByCode(1);

            assertNotNull(result);
            assertEquals(ConfigurationTableEnum.COMPANY_FINANCE, result);
            assertEquals("财务审核流程配置表", result.getDesc());
        }

        @Test
        @DisplayName("should return null for unknown code")
        void shouldReturnNullForUnknownCode() {
            assertNull(ConfigurationTableEnum.getInstanceByCode(-1));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNullCode() {
            assertNull(ConfigurationTableEnum.getInstanceByCode(null));
        }

        @Test
        @DisplayName("should return null for code not matching any enum")
        void shouldReturnNullForNonMatchingCode() {
            assertNull(ConfigurationTableEnum.getInstanceByCode(999));
        }
    }

    @Nested
    @DisplayName("enum values consistency")
    class EnumConsistencyTest {
        @Test
        @DisplayName("all enum values should have unique codes")
        void shouldHaveUniqueCodes() {
            java.util.Set<Integer> codes = new java.util.HashSet<>();
            for (ConfigurationTableEnum e : ConfigurationTableEnum.values()) {
                assertTrue(codes.add(e.getCode()), "Duplicate code: " + e.getCode());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null code")
        void shouldHaveNonNullCode() {
            for (ConfigurationTableEnum e : ConfigurationTableEnum.values()) {
                assertNotNull(e.getCode(), "Null code for " + e.name());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null desc")
        void shouldHaveNonNullDesc() {
            for (ConfigurationTableEnum e : ConfigurationTableEnum.values()) {
                assertNotNull(e.getDesc(), "Null desc for " + e.name());
            }
        }

        @Test
        @DisplayName("getInstanceByCode should find all existing enum values")
        void shouldFindAllExistingValues() {
            for (ConfigurationTableEnum e : ConfigurationTableEnum.values()) {
                ConfigurationTableEnum found = ConfigurationTableEnum.getInstanceByCode(e.getCode());
                assertSame(e, found, "Should find " + e.name() + " by code " + e.getCode());
            }
        }
    }
}
