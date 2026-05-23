package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class SnowFlakeTest extends BaseTest {

    @Nested
    @DisplayName("nextId")
    class NextIdTest {
        @Test
        @DisplayName("should generate positive ID")
        void shouldGeneratePositiveId() {
            long id = SnowFlake.nextId();
            assertTrue(id > 0);
        }

        @Test
        @DisplayName("should generate unique IDs")
        void shouldGenerateUniqueIds() {
            long id1 = SnowFlake.nextId();
            long id2 = SnowFlake.nextId();
            assertNotEquals(id1, id2);
        }

        @Test
        @DisplayName("should generate 1000 unique IDs")
        void shouldGenerate1000UniqueIds() {
            java.util.Set<Long> ids = new java.util.HashSet<>();
            for (int i = 0; i < 1000; i++) {
                ids.add(SnowFlake.nextId());
            }
            assertEquals(1000, ids.size());
        }

        @Test
        @DisplayName("should generate monotonically increasing IDs")
        void shouldGenerateMonotonicallyIncreasing() {
            long prev = SnowFlake.nextId();
            for (int i = 0; i < 100; i++) {
                long curr = SnowFlake.nextId();
                assertTrue(curr >= prev, "ID should be >= previous: " + curr + " vs " + prev);
                prev = curr;
            }
        }
    }
}
