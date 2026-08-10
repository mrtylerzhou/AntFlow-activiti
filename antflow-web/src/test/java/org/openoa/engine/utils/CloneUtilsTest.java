package org.openoa.engine.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class CloneUtilsTest extends BaseTest {

    @Nested
    @DisplayName("copyFields")
    class CopyFieldsTest {
        @Test
        @DisplayName("should copy specified fields between same type objects")
        void shouldCopySpecifiedFields() {
            TestSource source = new TestSource();
            source.setName("original");
            source.setValue(100);
            source.setExtra("extra");

            TestSource target = new TestSource();
            target.setName("target");
            target.setValue(0);

            CloneUtils.copyFields(source, target, "name", "value");

            assertEquals("original", target.getName());
            assertEquals(100, target.getValue());
            assertNull(target.getExtra());
        }

        @Test
        @DisplayName("should copy single field")
        void shouldCopySingleField() {
            TestSource source = new TestSource();
            source.setName("original");
            source.setValue(999);

            TestSource target = new TestSource();

            CloneUtils.copyFields(source, target, "name");

            assertEquals("original", target.getName());
            assertEquals(0, target.getValue());
        }

        @Test
        @DisplayName("should not throw when field does not exist")
        void shouldNotThrowWhenFieldNotExist() {
            TestSource source = new TestSource();
            TestSource target = new TestSource();

            assertDoesNotThrow(() ->
                    CloneUtils.copyFields(source, target, "nonExistentField"));
        }
    }

    static class TestSource {
        private String name;
        private int value;
        private String extra;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getExtra() { return extra; }
        public void setExtra(String extra) { this.extra = extra; }
    }
}
