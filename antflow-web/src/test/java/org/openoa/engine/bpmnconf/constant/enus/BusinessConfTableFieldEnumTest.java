package org.openoa.engine.bpmnconf.constant.enus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusinessConfTableFieldEnumTest extends BaseTest {

    @Nested
    @DisplayName("getByParentTable")
    class GetByParentTableTest {
        @Test
        @DisplayName("should find all fields under COMPANY_FINANCE")
        void shouldFindAllFieldsUnderCompanyFinance() {
            List<BusinessConfTableFieldEnum> result = BusinessConfTableFieldEnum.getByParentTable(ConfigurationTableEnum.COMPANY_FINANCE);

            assertNotNull(result);
            assertEquals(6, result.size());
        }

        @Test
        @DisplayName("should return all fields with correct parent table")
        void shouldReturnFieldsWithCorrectParentTable() {
            List<BusinessConfTableFieldEnum> result = BusinessConfTableFieldEnum.getByParentTable(ConfigurationTableEnum.COMPANY_FINANCE);

            for (BusinessConfTableFieldEnum field : result) {
                assertEquals(ConfigurationTableEnum.COMPANY_FINANCE, field.getParentTable());
            }
        }

        @Test
        @DisplayName("should return null when parent table is null")
        void shouldReturnNullWhenParentTableIsNull() {
            assertNull(BusinessConfTableFieldEnum.getByParentTable(null));
        }
    }

    @Nested
    @DisplayName("getTableFieldEnumByCode")
    class GetTableFieldEnumByCodeTest {
        @Test
        @DisplayName("should find FINANCE_CASHER by code 1")
        void shouldFindFinanceCasherByCode1() {
            BusinessConfTableFieldEnum result = BusinessConfTableFieldEnum.getTableFieldEnumByCode(1);

            assertNotNull(result);
            assertEquals(BusinessConfTableFieldEnum.FINANCE_CASHER, result);
            assertEquals("票据审核", result.getDesc());
        }

        @Test
        @DisplayName("should find FINANCE_CFO by code 16")
        void shouldFindFinanceCfoByCode16() {
            BusinessConfTableFieldEnum result = BusinessConfTableFieldEnum.getTableFieldEnumByCode(16);

            assertNotNull(result);
            assertEquals(BusinessConfTableFieldEnum.FINANCE_CFO, result);
        }

        @Test
        @DisplayName("should return null for unknown code")
        void shouldReturnNullForUnknownCode() {
            assertNull(BusinessConfTableFieldEnum.getTableFieldEnumByCode(-1));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNullCode() {
            assertNull(BusinessConfTableFieldEnum.getTableFieldEnumByCode(null));
        }
    }

    @Nested
    @DisplayName("enum values consistency")
    class EnumConsistencyTest {
        @Test
        @DisplayName("all enum values should have unique codes")
        void shouldHaveUniqueCodes() {
            java.util.Set<Integer> codes = new java.util.HashSet<>();
            for (BusinessConfTableFieldEnum e : BusinessConfTableFieldEnum.values()) {
                assertTrue(codes.add(e.getCode()), "Duplicate code: " + e.getCode());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null parent table")
        void shouldHaveNonNullParentTable() {
            for (BusinessConfTableFieldEnum e : BusinessConfTableFieldEnum.values()) {
                assertNotNull(e.getParentTable(), "Null parentTable for " + e.name());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null code")
        void shouldHaveNonNullCode() {
            for (BusinessConfTableFieldEnum e : BusinessConfTableFieldEnum.values()) {
                assertNotNull(e.getCode(), "Null code for " + e.name());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null desc")
        void shouldHaveNonNullDesc() {
            for (BusinessConfTableFieldEnum e : BusinessConfTableFieldEnum.values()) {
                assertNotNull(e.getDesc(), "Null desc for " + e.name());
            }
        }

        @Test
        @DisplayName("getTableFieldEnumByCode should find all existing enum values")
        void shouldFindAllExistingValues() {
            for (BusinessConfTableFieldEnum e : BusinessConfTableFieldEnum.values()) {
                BusinessConfTableFieldEnum found = BusinessConfTableFieldEnum.getTableFieldEnumByCode(e.getCode());
                assertSame(e, found, "Should find " + e.name() + " by code " + e.getCode());
            }
        }
    }
}
