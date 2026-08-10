package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class SupporttedDatabaseEnumTest extends BaseTest {

    @Nested
    @DisplayName("getByDatabaseId")
    class GetByDatabaseIdTest {
        @Test
        @DisplayName("should return MYSQL for mysql")
        void shouldReturnMysql() {
            assertEquals(SupporttedDatabaseEnum.MYSQL, SupporttedDatabaseEnum.getByDatabaseId("mysql"));
        }

        @Test
        @DisplayName("should return ORACLE for oracle (first match)")
        void shouldReturnOracle() {
            assertEquals(SupporttedDatabaseEnum.ORACLE, SupporttedDatabaseEnum.getByDatabaseId("oracle"));
        }

        @Test
        @DisplayName("should return POSTGRESQL for postgresql")
        void shouldReturnPostgresql() {
            assertEquals(SupporttedDatabaseEnum.POSTGRESQL, SupporttedDatabaseEnum.getByDatabaseId("postgresql"));
        }

        @Test
        @DisplayName("should return CAN_NOT_DETERMAIN for null")
        void shouldReturnDefaultForNull() {
            assertEquals(SupporttedDatabaseEnum.CAN_NOT_DETERMAIN, SupporttedDatabaseEnum.getByDatabaseId(null));
        }

        @Test
        @DisplayName("should return CAN_NOT_DETERMAIN for empty string")
        void shouldReturnDefaultForEmpty() {
            assertEquals(SupporttedDatabaseEnum.CAN_NOT_DETERMAIN, SupporttedDatabaseEnum.getByDatabaseId(""));
        }

        @Test
        @DisplayName("should return CAN_NOT_DETERMAIN for unknown database")
        void shouldReturnDefaultForUnknown() {
            assertEquals(SupporttedDatabaseEnum.CAN_NOT_DETERMAIN, SupporttedDatabaseEnum.getByDatabaseId("unknown_db"));
        }
    }

    @Nested
    @DisplayName("getByDatabaseName")
    class GetByDatabaseNameTest {
        @Test
        @DisplayName("should return MYSQL for MySQL")
        void shouldReturnMysql() {
            assertEquals(SupporttedDatabaseEnum.MYSQL, SupporttedDatabaseEnum.getByDatabaseName("MySQL"));
        }

        @Test
        @DisplayName("should return SQLSERVER for OceanBase (share proxy)")
        void shouldReturnSqlserverForOceanBase() {
            assertEquals(SupporttedDatabaseEnum.SQLSERVER, SupporttedDatabaseEnum.getByDatabaseName("OceanBase"));
        }

        @Test
        @DisplayName("should return POSTGRESQL for openGauss (share proxy)")
        void shouldReturnPostgresqlForGauss() {
            assertEquals(SupporttedDatabaseEnum.POSTGRESQL, SupporttedDatabaseEnum.getByDatabaseName("openGauss"));
        }

        @Test
        @DisplayName("should return ORACLE for Oracle")
        void shouldReturnOracle() {
            assertEquals(SupporttedDatabaseEnum.ORACLE, SupporttedDatabaseEnum.getByDatabaseName("Oracle"));
        }

        @Test
        @DisplayName("should return CAN_NOT_DETERMAIN for null")
        void shouldReturnDefaultForNull() {
            assertEquals(SupporttedDatabaseEnum.CAN_NOT_DETERMAIN, SupporttedDatabaseEnum.getByDatabaseName(null));
        }

        @Test
        @DisplayName("should return CAN_NOT_DETERMAIN for empty")
        void shouldReturnDefaultForEmpty() {
            assertEquals(SupporttedDatabaseEnum.CAN_NOT_DETERMAIN, SupporttedDatabaseEnum.getByDatabaseName(""));
        }

        @Test
        @DisplayName("should return CAN_NOT_DETERMAIN for unknown name")
        void shouldReturnDefaultForUnknown() {
            assertEquals(SupporttedDatabaseEnum.CAN_NOT_DETERMAIN, SupporttedDatabaseEnum.getByDatabaseName("UnknownDB"));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("OCEANBASE should have share=SQLSERVER")
        void oceanBaseShareProxy() {
            assertEquals(SupporttedDatabaseEnum.SQLSERVER, SupporttedDatabaseEnum.OCEANBASE.getShare());
        }

        @Test
        @DisplayName("GAUSS should have share=POSTGRESQL")
        void gaussShareProxy() {
            assertEquals(SupporttedDatabaseEnum.POSTGRESQL, SupporttedDatabaseEnum.GAUSS.getShare());
        }

        @Test
        @DisplayName("MYSQL should have share=null")
        void mysqlNoShare() {
            assertNull(SupporttedDatabaseEnum.MYSQL.getShare());
        }
    }
}
