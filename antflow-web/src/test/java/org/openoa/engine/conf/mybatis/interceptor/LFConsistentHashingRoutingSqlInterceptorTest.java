package org.openoa.engine.conf.mybatis.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LFConsistentHashingRoutingSqlInterceptorTest extends BaseTest {

    @Nested
    @DisplayName("replaceTableName via reflection")
    class ReplaceTableNameTest {
        @Test
        @DisplayName("should replace table name in SELECT statement")
        void shouldReplaceTableNameInSelect() throws Exception {
            String sql = "SELECT * FROM lf_form_data_main WHERE form_code = 'test'";
            String result = invokeReplaceTableName("lf_form_data_main", "lf_form_data_main_1", sql);

            assertTrue(result.contains("lf_form_data_main_1"));
            assertFalse(result.contains("lf_form_data_main WHERE"));
        }

        @Test
        @DisplayName("should replace table name in INSERT statement")
        void shouldReplaceTableNameInInsert() throws Exception {
            String sql = "INSERT INTO lf_form_data_main (id, form_code) VALUES (1, 'test')";
            String result = invokeReplaceTableName("lf_form_data_main", "lf_form_data_main_2", sql);

            assertTrue(result.contains("lf_form_data_main_2"));
        }

        @Test
        @DisplayName("should replace table name in UPDATE statement")
        void shouldReplaceTableNameInUpdate() throws Exception {
            String sql = "UPDATE lf_form_data_main SET form_code = 'test' WHERE id = 1";
            String result = invokeReplaceTableName("lf_form_data_main", "lf_form_data_main_3", sql);

            assertTrue(result.contains("lf_form_data_main_3"));
        }

        @Test
        @DisplayName("should replace table name in DELETE statement")
        void shouldReplaceTableNameInDelete() throws Exception {
            String sql = "DELETE FROM lf_form_data_main WHERE id = 1";
            String result = invokeReplaceTableName("lf_form_data_main", "lf_form_data_main_4", sql);

            assertTrue(result.contains("lf_form_data_main_4"));
        }

        @Test
        @DisplayName("should not replace non-matching table name")
        void shouldNotReplaceNonMatchingTable() throws Exception {
            String sql = "SELECT * FROM other_table WHERE id = 1";
            String result = invokeReplaceTableName("lf_form_data_main", "lf_form_data_main_1", sql);

            assertTrue(result.contains("other_table"));
            assertFalse(result.contains("lf_form_data_main_1"));
        }

        private String invokeReplaceTableName(String original, String newName, String sql) throws Exception {
            Method method = LFConsistentHashingRoutingSqlInterceptor.class.getDeclaredMethod(
                    "replaceTableName", String.class, String.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, original, newName, sql);
        }
    }

    @Nested
    @DisplayName("getColumnNames")
    class GetColumnNamesTest {
        @Test
        @DisplayName("should extract column from simple column expression")
        void shouldExtractFromSimpleColumn() {
            net.sf.jsqlparser.schema.Column col = new net.sf.jsqlparser.schema.Column("name");
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getColumnNames(col);

            assertEquals(1, result.size());
            assertEquals("name", result.get(0));
        }

        @Test
        @DisplayName("should extract columns from binary expression")
        void shouldExtractFromBinaryExpression() {
            String sql = "SELECT * FROM t WHERE name = 'test' AND age > 18";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getColumnNamesFromSelectSql(sql);

            assertTrue(result.contains("name"));
            assertTrue(result.contains("age"));
        }
    }

    @Nested
    @DisplayName("getWhereColumnNamesFromSql")
    class GetWhereColumnNamesFromSqlTest {
        @Test
        @DisplayName("should extract WHERE columns from SELECT")
        void shouldExtractFromSelect() {
            String sql = "SELECT * FROM users WHERE name = 'test' AND age > 18";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getWhereColumnNamesFromSql(sql);

            assertTrue(result.contains("name"));
            assertTrue(result.contains("age"));
        }

        @Test
        @DisplayName("should extract WHERE columns from UPDATE")
        void shouldExtractFromUpdate() {
            String sql = "UPDATE users SET name = 'test' WHERE id = 1";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getWhereColumnNamesFromSql(sql);

            assertTrue(result.contains("id"));
        }

        @Test
        @DisplayName("should extract WHERE columns from DELETE")
        void shouldExtractFromDelete() {
            String sql = "DELETE FROM users WHERE id = 1 AND status = 0";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getWhereColumnNamesFromSql(sql);

            assertTrue(result.contains("id"));
            assertTrue(result.contains("status"));
        }

        @Test
        @DisplayName("should return empty list when no WHERE clause")
        void shouldReturnEmptyWhenNoWhere() {
            String sql = "SELECT * FROM users";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getWhereColumnNamesFromSql(sql);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should handle parenthesized WHERE conditions")
        void shouldHandleParenthesizedConditions() {
            String sql = "SELECT * FROM users WHERE (name = 'test' OR age > 18) AND status = 1";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getWhereColumnNamesFromSql(sql);

            assertTrue(result.contains("name"));
            assertTrue(result.contains("age"));
            assertTrue(result.contains("status"));
        }
    }

    @Nested
    @DisplayName("getColumnNamesFromSelectSql")
    class GetColumnNamesFromSelectSqlTest {
        @Test
        @DisplayName("should extract columns from SELECT and WHERE")
        void shouldExtractFromSelectAndWhere() {
            String sql = "SELECT name, age FROM users WHERE id = 1";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getColumnNamesFromSelectSql(sql);

            assertTrue(result.contains("name"));
            assertTrue(result.contains("age"));
            assertTrue(result.contains("id"));
        }

        @Test
        @DisplayName("should extract columns from SELECT *")
        void shouldExtractFromSelectStar() {
            String sql = "SELECT * FROM users WHERE name = 'test'";
            List<String> result = LFConsistentHashingRoutingSqlInterceptor.getColumnNamesFromSelectSql(sql);

            assertTrue(result.contains("name"));
        }
    }
}
