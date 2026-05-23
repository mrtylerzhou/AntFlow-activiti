package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import static org.junit.jupiter.api.Assertions.*;

class ProcessNodeEnumTest extends BaseTest {

    @Nested
    @DisplayName("enum values")
    class EnumValuesTest {
        @Test
        @DisplayName("START_TASK_KEY should have code 1")
        void startTaskKey() {
            assertEquals(1, ProcessNodeEnum.START_TASK_KEY.getCode());
            assertEquals("task1418018332271", ProcessNodeEnum.START_TASK_KEY.getDesc());
        }

        @Test
        @DisplayName("TWO_TASK_KEY should have code 2")
        void twoTaskKey() {
            assertEquals(2, ProcessNodeEnum.TWO_TASK_KEY.getCode());
            assertEquals("task1418018332272", ProcessNodeEnum.TWO_TASK_KEY.getDesc());
        }

        @Test
        @DisplayName("should have 200 enum values")
        void valueCount() {
            assertEquals(200, ProcessNodeEnum.values().length);
        }

        @Test
        @DisplayName("codes should be sequential from 1 to 200")
        void sequentialCodes() {
            for (int i = 1; i <= 200; i++) {
                final int code = i;
                boolean found = false;
                for (ProcessNodeEnum e : ProcessNodeEnum.values()) {
                    if (e.getCode() == code) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Missing code: " + code);
            }
        }

        @Test
        @DisplayName("all desc values should start with 'task'")
        void allDescsStartWithTask() {
            for (ProcessNodeEnum e : ProcessNodeEnum.values()) {
                assertTrue(e.getDesc().startsWith("task"),
                        e.name() + " desc should start with 'task': " + e.getDesc());
            }
        }
    }

    @Nested
    @DisplayName("getDescByCode")
    class GetDescByCodeTest {
        @Test
        @DisplayName("should return correct desc for code 1")
        void code1() {
            assertEquals("task1418018332271", ProcessNodeEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("should return correct desc for code 200")
        void code200() {
            assertEquals("task1418018332600", ProcessNodeEnum.getDescByCode(200));
        }

        @Test
        @DisplayName("should return null for unknown code")
        void unknownCode() {
            assertNull(ProcessNodeEnum.getDescByCode(999));
        }
    }

    @Nested
    @DisplayName("getCodeByDesc")
    class GetCodeByDescTest {
        @Test
        @DisplayName("should return correct code for desc")
        void correctCode() {
            assertEquals(1, ProcessNodeEnum.getCodeByDesc("task1418018332271"));
        }

        @Test
        @DisplayName("should return null for unknown desc")
        void unknownDesc() {
            assertNull(ProcessNodeEnum.getCodeByDesc("task999"));
        }
    }

    @Nested
    @DisplayName("getGeneralNextNode")
    class GetGeneralNextNodeTest {
        @Test
        @DisplayName("should return next node key")
        void nextNode() {
            assertEquals("task1418018332272", ProcessNodeEnum.getGeneralNextNode("task1418018332271"));
        }

        @Test
        @DisplayName("should increment by 1")
        void incrementByOne() {
            assertEquals("task1418018332280", ProcessNodeEnum.getGeneralNextNode("task1418018332279"));
        }

        @Test
        @DisplayName("should throw for null input")
        void nullInput() {
            assertThrows(AFBizException.class, () -> ProcessNodeEnum.getGeneralNextNode(null));
        }

        @Test
        @DisplayName("should throw for empty input")
        void emptyInput() {
            assertThrows(AFBizException.class, () -> ProcessNodeEnum.getGeneralNextNode(""));
        }
    }

    @Nested
    @DisplayName("getGeneralPrevNode")
    class GetGeneralPrevNodeTest {
        @Test
        @DisplayName("should return previous node key")
        void prevNode() {
            assertEquals("task1418018332271", ProcessNodeEnum.getGeneralPrevNode("task1418018332272"));
        }

        @Test
        @DisplayName("should decrement by 1")
        void decrementByOne() {
            assertEquals("task1418018332278", ProcessNodeEnum.getGeneralPrevNode("task1418018332279"));
        }

        @Test
        @DisplayName("should throw for null input")
        void nullInput() {
            assertThrows(AFBizException.class, () -> ProcessNodeEnum.getGeneralPrevNode(null));
        }
    }

    @Nested
    @DisplayName("compare")
    class CompareTest {
        @Test
        @DisplayName("should return negative when first is smaller")
        void firstSmaller() {
            assertTrue(ProcessNodeEnum.compare("task1418018332271", "task1418018332272") < 0);
        }

        @Test
        @DisplayName("should return positive when first is larger")
        void firstLarger() {
            assertTrue(ProcessNodeEnum.compare("task1418018332273", "task1418018332271") > 0);
        }

        @Test
        @DisplayName("should return zero when equal")
        void equal() {
            assertEquals(0, ProcessNodeEnum.compare("task1418018332271", "task1418018332271"));
        }

        @Test
        @DisplayName("should throw for null first arg")
        void nullFirst() {
            assertThrows(AFBizException.class, () -> ProcessNodeEnum.compare(null, "task1418018332271"));
        }

        @Test
        @DisplayName("should throw for null second arg")
        void nullSecond() {
            assertThrows(AFBizException.class, () -> ProcessNodeEnum.compare("task1418018332271", null));
        }
    }
}
