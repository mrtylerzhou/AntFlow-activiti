package org.openoa.engine.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest extends BaseTest {

    @Nested
    @DisplayName("getProperty")
    class GetPropertyTest {
        @Test
        @DisplayName("should get public field value")
        void shouldGetPublicFieldValue() throws Exception {
            TestPerson person = new TestPerson();
            person.name = "Alice";
            assertEquals("Alice", ReflectionUtils.getProperty(person, "name"));
        }

        @Test
        @DisplayName("should get private field value")
        void shouldGetPrivateFieldValue() throws Exception {
            TestPerson person = new TestPerson();
            person.setAge(25);
            assertEquals(25, ReflectionUtils.getProperty(person, "age"));
        }

        @Test
        @DisplayName("should return null for null object")
        void shouldReturnNullForNullObject() {
            assertNull(ReflectionUtils.getProperty(null, "name"));
        }

        @Test
        @DisplayName("should return null for null fieldName")
        void shouldReturnNullForNullFieldName() {
            TestPerson person = new TestPerson();
            assertNull(ReflectionUtils.getProperty(person, null));
        }

        @Test
        @DisplayName("should return null for non-existent field")
        void shouldReturnNullForNonExistentField() {
            TestPerson person = new TestPerson();
            assertNull(ReflectionUtils.getProperty(person, "nonExistent"));
        }
    }

    @Nested
    @DisplayName("hasProperty")
    class HasPropertyTest {
        @Test
        @DisplayName("should return true for existing field")
        void shouldReturnTrueForExisting() {
            TestPerson person = new TestPerson();
            assertTrue(ReflectionUtils.hasProperty(person, "name"));
            assertTrue(ReflectionUtils.hasProperty(person, "age"));
        }

        @Test
        @DisplayName("should return true for parent class field")
        void shouldReturnTrueForParentField() {
            TestEmployee employee = new TestEmployee();
            assertTrue(ReflectionUtils.hasProperty(employee, "name"));
            assertTrue(ReflectionUtils.hasProperty(employee, "company"));
        }

        @Test
        @DisplayName("should return false for non-existent field")
        void shouldReturnFalseForNonExistent() {
            TestPerson person = new TestPerson();
            assertFalse(ReflectionUtils.hasProperty(person, "nonExistent"));
        }

        @Test
        @DisplayName("should return false for null object")
        void shouldReturnFalseForNull() {
            assertFalse(ReflectionUtils.hasProperty(null, "name"));
        }
    }

    @Nested
    @DisplayName("setProperty")
    class SetPropertyTest {
        @Test
        @DisplayName("should set public field value")
        void shouldSetPublicField() {
            TestPerson person = new TestPerson();
            boolean result = ReflectionUtils.setProperty(person, "name", "Bob");
            assertTrue(result);
            assertEquals("Bob", person.name);
        }

        @Test
        @DisplayName("should set private field value")
        void shouldSetPrivateField() {
            TestPerson person = new TestPerson();
            boolean result = ReflectionUtils.setProperty(person, "age", 30);
            assertTrue(result);
            assertEquals(30, person.getAge());
        }

        @Test
        @DisplayName("should return false for null object")
        void shouldReturnFalseForNull() {
            assertFalse(ReflectionUtils.setProperty(null, "name", "value"));
        }

        @Test
        @DisplayName("should return false for non-existent field")
        void shouldReturnFalseForNonExistent() {
            TestPerson person = new TestPerson();
            assertFalse(ReflectionUtils.setProperty(person, "nonExistent", "value"));
        }
    }

    @Nested
    @DisplayName("getPropertyType")
    class GetPropertyTypeTest {
        @Test
        @DisplayName("should return correct type for String field")
        void shouldReturnStringType() {
            TestPerson person = new TestPerson();
            assertEquals(String.class, ReflectionUtils.getPropertyType(person, "name"));
        }

        @Test
        @DisplayName("should return correct type for int field")
        void shouldReturnIntType() {
            TestPerson person = new TestPerson();
            assertEquals(int.class, ReflectionUtils.getPropertyType(person, "age"));
        }

        @Test
        @DisplayName("should return null for non-existent field")
        void shouldReturnNullForNonExistent() {
            TestPerson person = new TestPerson();
            assertNull(ReflectionUtils.getPropertyType(person, "nonExistent"));
        }

        @Test
        @DisplayName("should return null for null object")
        void shouldReturnNullForNull() {
            assertNull(ReflectionUtils.getPropertyType(null, "name"));
        }
    }

    @Nested
    @DisplayName("getAllFields")
    class GetAllFieldsTest {
        @Test
        @DisplayName("should get fields from class only")
        void shouldGetFieldsFromClassOnly() {
            Field[] fields = ReflectionUtils.getAllFields(TestPerson.class);
            assertTrue(fields.length >= 2);
        }

        @Test
        @DisplayName("should include parent class fields")
        void shouldIncludeParentFields() {
            Field[] employeeFields = ReflectionUtils.getAllFields(TestEmployee.class);
            boolean hasName = false;
            boolean hasCompany = false;
            for (Field f : employeeFields) {
                if (f.getName().equals("name")) hasName = true;
                if (f.getName().equals("company")) hasCompany = true;
            }
            assertTrue(hasName);
            assertTrue(hasCompany);
        }

        @Test
        @DisplayName("should not include Object class fields")
        void shouldNotIncludeObjectFields() {
            Field[] fields = ReflectionUtils.getAllFields(TestPerson.class);
            for (Field f : fields) {
                assertNotEquals("class", f.getName());
            }
        }
    }

    @Nested
    @DisplayName("copyProperties")
    class CopyPropertiesTest {
        @Test
        @DisplayName("should copy matching properties")
        void shouldCopyMatchingProperties() {
            TestPerson source = new TestPerson();
            source.name = "Alice";
            source.setAge(25);

            TestPerson target = new TestPerson();
            ReflectionUtils.copyProperties(source, target);

            assertEquals("Alice", target.name);
            assertEquals(25, target.getAge());
        }

        @Test
        @DisplayName("should not throw for incompatible types")
        void shouldNotThrowForIncompatibleTypes() {
            TestPerson source = new TestPerson();
            source.name = "Alice";
            assertDoesNotThrow(() -> ReflectionUtils.copyProperties(source, new Object()));
        }
    }

    class TestPerson {
        String name;
        private int age;

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    class TestEmployee extends TestPerson {
        String company;

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
    }
}
