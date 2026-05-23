package org.openoa.engine.utils;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoundSqlUtilsTest extends BaseTest {

    @Nested
    @DisplayName("isCurdSql")
    class IsCurdSqlTest {
        @Test
        @DisplayName("should return true for SELECT statement")
        void shouldReturnTrueForSelect() {
            assertTrue(BoundSqlUtils.isCurdSql("SELECT * FROM users"));
        }

        @Test
        @DisplayName("should return true for INSERT statement")
        void shouldReturnTrueForInsert() {
            assertTrue(BoundSqlUtils.isCurdSql("INSERT INTO users (name) VALUES ('test')"));
        }

        @Test
        @DisplayName("should return true for UPDATE statement")
        void shouldReturnTrueForUpdate() {
            assertTrue(BoundSqlUtils.isCurdSql("UPDATE users SET name='test' WHERE id=1"));
        }

        @Test
        @DisplayName("should return true for DELETE statement")
        void shouldReturnTrueForDelete() {
            assertTrue(BoundSqlUtils.isCurdSql("DELETE FROM users WHERE id=1"));
        }

        @Test
        @DisplayName("should return true for lowercase select")
        void shouldReturnTrueForLowercaseSelect() {
            assertTrue(BoundSqlUtils.isCurdSql("select * from users"));
        }

        @Test
        @DisplayName("should return true for mixed case SeLeCt")
        void shouldReturnTrueForMixedCase() {
            assertTrue(BoundSqlUtils.isCurdSql("SeLeCt * from users"));
        }

        @Test
        @DisplayName("should return true for statement with leading whitespace")
        void shouldReturnTrueForLeadingWhitespace() {
            assertTrue(BoundSqlUtils.isCurdSql("  SELECT * FROM users"));
        }

        @Test
        @DisplayName("should return true for statement with leading comments")
        void shouldReturnTrueForLeadingComments() {
            assertTrue(BoundSqlUtils.isCurdSql("-- comment\nSELECT * FROM users"));
        }

        @Test
        @DisplayName("should return false for null input")
        void shouldReturnFalseForNull() {
            assertFalse(BoundSqlUtils.isCurdSql(null));
        }

        @Test
        @DisplayName("should return false for empty string")
        void shouldReturnFalseForEmpty() {
            assertFalse(BoundSqlUtils.isCurdSql(""));
        }

        @Test
        @DisplayName("should return false for whitespace only")
        void shouldReturnFalseForWhitespace() {
            assertFalse(BoundSqlUtils.isCurdSql("   "));
        }

        @Test
        @DisplayName("should return false for non-CRUD statement")
        void shouldReturnFalseForNonCrud() {
            assertFalse(BoundSqlUtils.isCurdSql("CREATE TABLE users (id INT)"));
        }

        @Test
        @DisplayName("should return false for word containing but not starting with select")
        void shouldReturnFalseForWordContainingSelect() {
            assertFalse(BoundSqlUtils.isCurdSql("reselect something"));
        }

        @Test
        @DisplayName("should return true for select as part of subquery")
        void shouldReturnTrueForSubquery() {
            assertTrue(BoundSqlUtils.isCurdSql("DELETE FROM users WHERE id IN (SELECT id FROM temp)"));
        }
    }

    @Nested
    @DisplayName("extractWhereColumnsAndParams")
    class ExtractWhereColumnsAndParamsTest {
        @Test
        @DisplayName("should extract single where column from SELECT with equals")
        void shouldExtractSingleWhereColumnFromSelect() {
            String sql = "SELECT * FROM users WHERE name = ?";
            List<ParameterMapping> mappings = createParameterMappings("name");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("name", result.get(0).getKey());
            assertEquals("name", result.get(0).getValue());
        }

        @Test
        @DisplayName("should extract multiple where columns connected by AND")
        void shouldExtractMultipleWhereColumns() {
            String sql = "SELECT * FROM users WHERE name = ? AND age = ?";
            List<ParameterMapping> mappings = createParameterMappings("name", "age");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(2, result.size());
            assertEquals("name", result.get(0).getKey());
            assertEquals("age", result.get(1).getKey());
        }

        @Test
        @DisplayName("should extract columns from UPDATE with WHERE clause")
        void shouldExtractColumnsFromUpdate() {
            String sql = "UPDATE users SET name = ? WHERE id = ?";
            List<ParameterMapping> mappings = createParameterMappings("name", "id");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("id", result.get(0).getKey());
        }

        @Test
        @DisplayName("should extract columns from DELETE with WHERE clause")
        void shouldExtractColumnsFromDelete() {
            String sql = "DELETE FROM users WHERE id = ?";
            List<ParameterMapping> mappings = createParameterMappings("id");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("id", result.get(0).getKey());
        }

        @Test
        @DisplayName("should return empty list when no WHERE clause")
        void shouldReturnEmptyWhenNoWhere() {
            String sql = "SELECT * FROM users";
            List<ParameterMapping> mappings = new ArrayList<>();
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should extract columns with greater than operator")
        void shouldExtractColumnsWithGreaterThan() {
            String sql = "SELECT * FROM users WHERE age > ?";
            List<ParameterMapping> mappings = createParameterMappings("age");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("age", result.get(0).getKey());
        }

        @Test
        @DisplayName("should extract columns with less than operator")
        void shouldExtractColumnsWithLessThan() {
            String sql = "SELECT * FROM users WHERE age < ?";
            List<ParameterMapping> mappings = createParameterMappings("age");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("age", result.get(0).getKey());
        }

        @Test
        @DisplayName("should extract columns with not equals operator")
        void shouldExtractColumnsWithNotEquals() {
            String sql = "SELECT * FROM users WHERE status != ?";
            List<ParameterMapping> mappings = createParameterMappings("status");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("status", result.get(0).getKey());
        }

        @Test
        @DisplayName("should extract columns with greater than or equals operator")
        void shouldExtractColumnsWithGreaterThanOrEquals() {
            String sql = "SELECT * FROM users WHERE age >= ?";
            List<ParameterMapping> mappings = createParameterMappings("age");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("age", result.get(0).getKey());
        }

        @Test
        @DisplayName("should extract columns with less than or equals operator")
        void shouldExtractColumnsWithLessThanOrEquals() {
            String sql = "SELECT * FROM users WHERE age <= ?";
            List<ParameterMapping> mappings = createParameterMappings("age");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
            assertEquals("age", result.get(0).getKey());
        }

        @Test
        @DisplayName("should handle mismatched column and parameter counts gracefully")
        void shouldHandleMismatchedCounts() {
            String sql = "SELECT * FROM users WHERE name = ? AND age = ?";
            List<ParameterMapping> mappings = createParameterMappings("name");
            BoundSql boundSql = createBoundSql(sql, mappings);

            List<Map.Entry<String, String>> result = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);

            assertEquals(1, result.size());
        }

        private BoundSql createBoundSql(String sql, List<ParameterMapping> parameterMappings) {
            try {
                Constructor<BoundSql> constructor = BoundSql.class.getDeclaredConstructor(
                        Configuration.class, String.class, List.class, Object.class);
                constructor.setAccessible(true);
                return constructor.newInstance(new Configuration(), sql, parameterMappings, null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create BoundSql", e);
            }
        }

        private List<ParameterMapping> createParameterMappings(String... properties) {
            Configuration config = new Configuration();
            List<ParameterMapping> mappings = new ArrayList<>();
            for (String prop : properties) {
                mappings.add(new ParameterMapping.Builder(config, prop, Object.class).build());
            }
            return mappings;
        }
    }
}
