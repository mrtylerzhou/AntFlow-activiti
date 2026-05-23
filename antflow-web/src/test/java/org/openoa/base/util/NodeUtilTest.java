package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.NodeLabelConstants;
import org.openoa.base.vo.ProcessActionButtonVo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeUtilTest extends BaseTest {

    @Nested
    @DisplayName("nodeLabelContainsAny")
    class NodeLabelContainsAnyTest {

        @Test
        @DisplayName("returns true when labelValues is empty")
        void returnsTrueForEmptyLabelValues() {
            List<BpmnNodeLabelVO> labels = Collections.singletonList(new BpmnNodeLabelVO("val1", "name1"));
            assertTrue(NodeUtil.nodeLabelContainsAny(labels));
        }

        @Test
        @DisplayName("returns false when nodeLabelVOS is null and labelValues provided")
        void returnsFalseForNullLabels() {
            List<BpmnNodeLabelVO> nullList = null;
            assertFalse(NodeUtil.nodeLabelContainsAny(nullList, "val1"));
        }

        @Test
        @DisplayName("returns false when nodeLabelVOS is empty and labelValues provided")
        void returnsFalseForEmptyLabels() {
            assertFalse(NodeUtil.nodeLabelContainsAny(Collections.emptyList(), "val1"));
        }

        @Test
        @DisplayName("returns true when at least one label value matches")
        void returnsTrueWhenMatchExists() {
            List<BpmnNodeLabelVO> labels = Arrays.asList(
                    new BpmnNodeLabelVO("val1", "name1"),
                    new BpmnNodeLabelVO("val2", "name2")
            );
            assertTrue(NodeUtil.nodeLabelContainsAny(labels, "val2"));
        }

        @Test
        @DisplayName("returns false when no label values match")
        void returnsFalseWhenNoMatch() {
            List<BpmnNodeLabelVO> labels = Collections.singletonList(
                    new BpmnNodeLabelVO("val1", "name1")
            );
            assertFalse(NodeUtil.nodeLabelContainsAny(labels, "val99"));
        }
    }

    @Nested
    @DisplayName("repeatButtonFilter")
    class RepeatButtonFilterTest {

        @Test
        @DisplayName("returns empty list for null input")
        void returnsEmptyForNull() {
            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(null);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list for empty input")
        void returnsEmptyForEmpty() {
            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(Collections.emptyList());
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("removes duplicate buttons by type, keeping first occurrence")
        void removesDuplicates() {
            ProcessActionButtonVo btn1 = ProcessActionButtonVo.builder().buttonType(1).name("Submit").build();
            ProcessActionButtonVo btn2 = ProcessActionButtonVo.builder().buttonType(2).name("Reject").build();
            ProcessActionButtonVo btn3 = ProcessActionButtonVo.builder().buttonType(1).name("Submit2").build();

            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(Arrays.asList(btn1, btn2, btn3));

            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getButtonType());
            assertEquals("Submit", result.get(0).getName());
            assertEquals(2, result.get(1).getButtonType());
        }

        @Test
        @DisplayName("keeps all buttons when no duplicates")
        void keepsAllWhenNoDuplicates() {
            ProcessActionButtonVo btn1 = ProcessActionButtonVo.builder().buttonType(1).name("Submit").build();
            ProcessActionButtonVo btn2 = ProcessActionButtonVo.builder().buttonType(2).name("Reject").build();
            ProcessActionButtonVo btn3 = ProcessActionButtonVo.builder().buttonType(3).name("Cancel").build();

            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(Arrays.asList(btn1, btn2, btn3));

            assertEquals(3, result.size());
        }
    }

    @Nested
    @DisplayName("nodeSpecialProcess")
    class NodeSpecialProcessTest {

        @Test
        @DisplayName("sets nodeType to APPROVER and isCarbonCopyNode=true when nodeType is COPY_V2")
        void setsApproverAndCarbonCopyForCopyV2() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode())
                    .build();

            NodeUtil.nodeSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
            assertTrue(node.getIsCarbonCopyNode());
        }

        @Test
        @DisplayName("does nothing when nodeType is null")
        void doesNothingForNullNodeType() {
            BpmnNodeVo node = BpmnNodeVo.builder().build();
            Integer originalNodeType = node.getNodeType();
            Boolean originalIsCarbonCopy = node.getIsCarbonCopyNode();

            NodeUtil.nodeSpecialProcess(node);

            assertEquals(originalNodeType, node.getNodeType());
            assertEquals(originalIsCarbonCopy, node.getIsCarbonCopyNode());
        }

        @Test
        @DisplayName("does nothing when nodeType is not COPY_V2")
        void doesNothingForNonCopyV2() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .build();
            Boolean isCarbonCopyBefore = node.getIsCarbonCopyNode();

            NodeUtil.nodeSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
            assertEquals(isCarbonCopyBefore, node.getIsCarbonCopyNode());
        }
    }

    @Nested
    @DisplayName("nodeLabelSpecialProcess")
    class NodeLabelSpecialProcessTest {

        @Test
        @DisplayName("sets nodeType to COPY_V2 when labelList contains copyNodeV2 label")
        void setsCopyV2WhenLabelMatches() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.singletonList(NodeLabelConstants.copyNodeV2))
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode(), node.getNodeType());
        }

        @Test
        @DisplayName("does nothing when labelList is null")
        void doesNothingForNullLabelList() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(null)
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
        }

        @Test
        @DisplayName("does nothing when labelList is empty")
        void doesNothingForEmptyLabelList() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.emptyList())
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
        }

        @Test
        @DisplayName("does nothing when labelList doesn't contain copyNodeV2 label")
        void doesNothingForNonMatchingLabel() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.singletonList(new BpmnNodeLabelVO("other", "Other")))
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
        }
    }
}
