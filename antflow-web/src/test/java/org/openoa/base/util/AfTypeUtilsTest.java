package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.springframework.core.ResolvableType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AfTypeUtilsTest extends BaseTest {

    @Nested
    @DisplayName("getAllTypes")
    class GetAllTypesTest {
        @Test
        @DisplayName("should collect type hierarchy for ArrayList")
        void shouldCollectArrayListHierarchy() {
            ResolvableType type = ResolvableType.forClass(java.util.ArrayList.class);
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(type);
            assertFalse(allTypes.isEmpty());
            boolean hasList = allTypes.stream()
                    .anyMatch(t -> t.getRawClass() != null && java.util.List.class.isAssignableFrom(t.getRawClass()));
            assertTrue(hasList, "Should include List interface");
        }

        @Test
        @DisplayName("should include Object class for any type")
        void shouldIncludeObjectClass() {
            ResolvableType type = ResolvableType.forClass(String.class);
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(type);
            boolean hasObject = allTypes.stream()
                    .anyMatch(t -> t.getRawClass() == Object.class);
            assertTrue(hasObject, "Should include Object class");
        }

        @Test
        @DisplayName("should include the type itself")
        void shouldIncludeTypeItself() {
            ResolvableType type = ResolvableType.forClass(Integer.class);
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(type);
            boolean hasInteger = allTypes.stream()
                    .anyMatch(t -> t.getRawClass() == Integer.class);
            assertTrue(hasInteger, "Should include Integer itself");
        }

        @Test
        @DisplayName("should handle interface type")
        void shouldHandleInterface() {
            ResolvableType type = ResolvableType.forClass(java.util.Map.class);
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(type);
            assertFalse(allTypes.isEmpty());
        }

        @Test
        @DisplayName("should return empty for NONE type")
        void shouldReturnEmptyForNone() {
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(ResolvableType.NONE);
            assertTrue(allTypes.isEmpty());
        }

        @Test
        @DisplayName("should return empty for null")
        void shouldReturnEmptyForNull() {
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(null);
            assertTrue(allTypes.isEmpty());
        }
    }
}
