package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.StringConstants;

import static org.junit.jupiter.api.Assertions.*;

class NodeLabelConstantsTest extends BaseTest {

    @Nested
    @DisplayName("dynamicCondition")
    class DynamicConditionTest {
        @Test
        @DisplayName("should have correct labelValue")
        void labelValue() {
            assertEquals(StringConstants.DYNAMIC_CONDITION_NODE, NodeLabelConstants.dynamicCondition.getLabelValue());
        }

        @Test
        @DisplayName("should have correct labelName")
        void labelName() {
            assertEquals("动态条件节点", NodeLabelConstants.dynamicCondition.getLabelName());
        }
    }

    @Nested
    @DisplayName("copyNode")
    class CopyNodeTest {
        @Test
        @DisplayName("should have correct labelValue")
        void labelValue() {
            assertEquals(StringConstants.COPY_NODE, NodeLabelConstants.copyNode.getLabelValue());
        }

        @Test
        @DisplayName("should have correct labelName")
        void labelName() {
            assertEquals("抄送节点", NodeLabelConstants.copyNode.getLabelName());
        }
    }

    @Nested
    @DisplayName("copyNodeV2")
    class CopyNodeV2Test {
        @Test
        @DisplayName("should have correct labelValue")
        void labelValue() {
            assertEquals(StringConstants.COPY_NODEV2, NodeLabelConstants.copyNodeV2.getLabelValue());
        }

        @Test
        @DisplayName("should have correct labelName")
        void labelName() {
            assertEquals("抄送节点V2", NodeLabelConstants.copyNodeV2.getLabelName());
        }
    }

    @Nested
    @DisplayName("automaticNode")
    class AutomaticNodeTest {
        @Test
        @DisplayName("should have correct labelValue")
        void labelValue() {
            assertEquals(StringConstants.AUTOMATIC_NODE, NodeLabelConstants.automaticNode.getLabelValue());
        }

        @Test
        @DisplayName("should have correct labelName")
        void labelName() {
            assertEquals("自动节点", NodeLabelConstants.automaticNode.getLabelName());
        }
    }

    @Nested
    @DisplayName("skippedAssignees")
    class SkippedAssigneesTest {
        @Test
        @DisplayName("should have correct labelValue")
        void labelValue() {
            assertEquals(StringConstants.SKIPPED_ASSIGNEE, NodeLabelConstants.skippedAssignees.getLabelValue());
        }

        @Test
        @DisplayName("should have correct labelName")
        void labelName() {
            assertEquals("跳过的审批人", NodeLabelConstants.skippedAssignees.getLabelName());
        }
    }

    @Nested
    @DisplayName("NONE_OPERATIONAL_NODES")
    class NoneOperationalNodesTest {
        @Test
        @DisplayName("should contain exactly 2 entries")
        void size() {
            assertEquals(2, NodeLabelConstants.NONE_OPERATIONAL_NODES.size());
        }

        @Test
        @DisplayName("should contain copyNodeV2")
        void containsCopyNodeV2() {
            assertTrue(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.copyNodeV2));
        }

        @Test
        @DisplayName("should contain automaticNode")
        void containsAutomaticNode() {
            assertTrue(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.automaticNode));
        }

        @Test
        @DisplayName("should not contain dynamicCondition")
        void notContainDynamicCondition() {
            assertFalse(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.dynamicCondition));
        }

        @Test
        @DisplayName("should not contain copyNode v1")
        void notContainCopyNodeV1() {
            assertFalse(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.copyNode));
        }
    }
}
