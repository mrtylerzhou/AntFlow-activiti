package org.openoa.common.constant.enus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.common.adaptor.BpmnInsertVariableSubs;
import org.openoa.common.adaptor.bpmnelementadp.*;

import static org.junit.jupiter.api.Assertions.*;

class ElementPropertyEnumTest extends BaseTest {

    @Nested
    @DisplayName("getClsByCode")
    class GetClsByCodeTest {
        @Test
        @DisplayName("should return single adp class for code 1")
        void shouldReturnSingleAdpForCode1() {
            assertEquals(BpmnAddFlowElementSingleAdp.class, ElementPropertyEnum.getClsByCode(1));
        }

        @Test
        @DisplayName("should return mult sign adp class for code 2")
        void shouldReturnMultSignAdpForCode2() {
            assertEquals(BpmnAddFlowElementMultSignAdp.class, ElementPropertyEnum.getClsByCode(2));
        }

        @Test
        @DisplayName("should return mult or sign adp class for code 3")
        void shouldReturnMultOrSignAdpForCode3() {
            assertEquals(BpmnAddFlowElementMultOrSignAdp.class, ElementPropertyEnum.getClsByCode(3));
        }

        @Test
        @DisplayName("should return null for gateway codes 4 and 5")
        void shouldReturnNullForGatewayCodes() {
            assertNull(ElementPropertyEnum.getClsByCode(4));
            assertNull(ElementPropertyEnum.getClsByCode(5));
        }

        @Test
        @DisplayName("should return null for sequence flow codes 6 and 7")
        void shouldReturnNullForSequenceFlowCodes() {
            assertNull(ElementPropertyEnum.getClsByCode(6));
            assertNull(ElementPropertyEnum.getClsByCode(7));
        }

        @Test
        @DisplayName("should return loop adp class for code 8")
        void shouldReturnLoopAdpForCode8() {
            assertEquals(BpmnAddFlowElementLoopAdp.class, ElementPropertyEnum.getClsByCode(8));
        }

        @Test
        @DisplayName("should return sign up serial adp class for code 9")
        void shouldReturnSignUpSerialAdpForCode9() {
            assertEquals(BpmnAddFlowElementSignUpSerialAdp.class, ElementPropertyEnum.getClsByCode(9));
        }

        @Test
        @DisplayName("should return null for unknown code")
        void shouldReturnNullForUnknownCode() {
            assertNull(ElementPropertyEnum.getClsByCode(999));
        }

        @Test
        @DisplayName("should return mult sign adp class for code 10 (sign up parallel)")
        void shouldReturnMultSignAdpForCode10() {
            assertEquals(BpmnAddFlowElementMultSignAdp.class, ElementPropertyEnum.getClsByCode(10));
        }

        @Test
        @DisplayName("should return mult or sign adp class for code 11 (sign up parallel or)")
        void shouldReturnMultOrSignAdpForCode11() {
            assertEquals(BpmnAddFlowElementMultOrSignAdp.class, ElementPropertyEnum.getClsByCode(11));
        }

        @Test
        @DisplayName("should return sign up serial adp class for code 21 (multiplayer sign in order)")
        void shouldReturnSignUpSerialAdpForCode21() {
            assertEquals(BpmnAddFlowElementSignUpSerialAdp.class, ElementPropertyEnum.getClsByCode(21));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNullCode() {
            assertNull(ElementPropertyEnum.getClsByCode(null));
        }
    }

    @Nested
    @DisplayName("getVariableSubClsByCode")
    class GetVariableSubClsByCodeTest {


        @Test
        @DisplayName("should return multiplayer sign variable sub class for code 2")
        void shouldReturnMultiplayerSignVariableSubForCode2() {
            assertEquals(BpmnInsertVariableSubsMultiplayerSignAdp.class, ElementPropertyEnum.getVariableSubClsByCode(2));
        }

        @Test
        @DisplayName("should return null for loop code 8")
        void shouldReturnNullForLoopCode() {
            assertNull(ElementPropertyEnum.getVariableSubClsByCode(8));
        }

        @Test
        @DisplayName("should return null for unknown code")
        void shouldReturnNullForUnknownCode() {
            assertNull(ElementPropertyEnum.getVariableSubClsByCode(999));
        }

        @Test
        @DisplayName("should return multiplayer or sign variable sub for code 3")
        void shouldReturnMultiplayerOrSignVariableSubForCode3() {
            assertEquals(BpmnInsertVariableSubsMultiplayerOrSignAdp.class, ElementPropertyEnum.getVariableSubClsByCode(3));
        }

        @Test
        @DisplayName("should return null for code 10 (sign up parallel has no variable sub)")
        void shouldReturnNullForCode10() {
            assertNull(ElementPropertyEnum.getVariableSubClsByCode(10));
        }

        @Test
        @DisplayName("should return null for code 11 (sign up parallel or has no variable sub)")
        void shouldReturnNullForCode11() {
            assertNull(ElementPropertyEnum.getVariableSubClsByCode(11));
        }

        @Test
        @DisplayName("should return multiplayer sign variable sub for code 21")
        void shouldReturnMultiplayerSignVariableSubForCode21() {
            assertEquals(BpmnInsertVariableSubsMultiplayerSignAdp.class, ElementPropertyEnum.getVariableSubClsByCode(21));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNullCode() {
            assertNull(ElementPropertyEnum.getVariableSubClsByCode(null));
        }
    }

    @Nested
    @DisplayName("enum values consistency")
    class EnumConsistencyTest {
        @Test
        @DisplayName("all enum values should have unique codes")
        void shouldHaveUniqueCodes() {
            java.util.Set<Integer> codes = new java.util.HashSet<>();
            for (ElementPropertyEnum e : ElementPropertyEnum.values()) {
                assertTrue(codes.add(e.getCode()), "Duplicate code found: " + e.getCode());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null desc")
        void shouldHaveNonNullDesc() {
            for (ElementPropertyEnum e : ElementPropertyEnum.values()) {
                assertNotNull(e.getDesc(), "Null desc for " + e.name());
            }
        }

        @Test
        @DisplayName("all enum values should have non-null code")
        void shouldHaveNonNullCode() {
            for (ElementPropertyEnum e : ElementPropertyEnum.values()) {
                assertNotNull(e.getCode(), "Null code for " + e.name());
            }
        }
    }
}
