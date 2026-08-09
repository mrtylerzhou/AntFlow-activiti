package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeLabelVOTest extends BaseTest {

    @Nested
    @DisplayName("no-arg constructor")
    class NoArgConstructorTest {
        @Test
        @DisplayName("should create with null fields")
        void nullFields() {
            BpmnNodeLabelVO vo = new BpmnNodeLabelVO();
            assertNull(vo.getLabelValue());
            assertNull(vo.getLabelName());
        }
    }

    @Nested
    @DisplayName("two-arg constructor")
    class TwoArgConstructorTest {
        @Test
        @DisplayName("should set labelValue and labelName")
        void shouldSetBothFields() {
            BpmnNodeLabelVO vo = new BpmnNodeLabelVO("testValue", "testName");
            assertEquals("testValue", vo.getLabelValue());
            assertEquals("testName", vo.getLabelName());
        }

        @Test
        @DisplayName("should accept null labelValue")
        void nullLabelValue() {
            BpmnNodeLabelVO vo = new BpmnNodeLabelVO(null, "name");
            assertNull(vo.getLabelValue());
            assertEquals("name", vo.getLabelName());
        }

        @Test
        @DisplayName("should accept null labelName")
        void nullLabelName() {
            BpmnNodeLabelVO vo = new BpmnNodeLabelVO("value", null);
            assertEquals("value", vo.getLabelValue());
            assertNull(vo.getLabelName());
        }
    }

    @Nested
    @DisplayName("setters and getters")
    class SetterGetterTest {
        @Test
        @DisplayName("should set and get labelValue")
        void labelValue() {
            BpmnNodeLabelVO vo = new BpmnNodeLabelVO();
            vo.setLabelValue("newVal");
            assertEquals("newVal", vo.getLabelValue());
        }

        @Test
        @DisplayName("should set and get labelName")
        void labelName() {
            BpmnNodeLabelVO vo = new BpmnNodeLabelVO();
            vo.setLabelName("newName");
            assertEquals("newName", vo.getLabelName());
        }
    }
}
