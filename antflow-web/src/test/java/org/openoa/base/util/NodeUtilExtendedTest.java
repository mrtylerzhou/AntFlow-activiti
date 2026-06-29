package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.NodeLabelConstants;
import org.openoa.base.vo.ProcessActionButtonVo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeUtilExtendedTest extends BaseTest {

    @Nested
    @DisplayName("nodeLabelContainsAny with NodeExtraInfoDTO")
    class NodeLabelContainsAnyDtoTest {

        @Test
        @DisplayName("should return true when label is found")
        void shouldReturnTrueWhenLabelFound() {
            NodeExtraInfoDTO dto = new NodeExtraInfoDTO();
            dto.setNodeLabelVOS(Arrays.asList(
                    new BpmnNodeLabelVO("val1", "name1"),
                    new BpmnNodeLabelVO("val2", "name2")
            ));
            assertTrue(NodeUtil.nodeLabelContainsAny(dto, "val1"));
        }

        @Test
        @DisplayName("should return false when label is not found")
        void shouldReturnFalseWhenLabelNotFound() {
            NodeExtraInfoDTO dto = new NodeExtraInfoDTO();
            dto.setNodeLabelVOS(Collections.singletonList(
                    new BpmnNodeLabelVO("val1", "name1")
            ));
            assertFalse(NodeUtil.nodeLabelContainsAny(dto, "val99"));
        }

        @Test
        @DisplayName("should return true when no label values provided")
        void shouldReturnTrueWhenNoLabelValues() {
            NodeExtraInfoDTO dto = new NodeExtraInfoDTO();
            dto.setNodeLabelVOS(Collections.singletonList(
                    new BpmnNodeLabelVO("val1", "name1")
            ));
            assertTrue(NodeUtil.nodeLabelContainsAny(dto));
        }

        @Test
        @DisplayName("should return false when DTO is null")
        void shouldReturnFalseWhenDtoNull() {
            assertFalse(NodeUtil.nodeLabelContainsAny((NodeExtraInfoDTO) null, "val1"));
        }

        @Test
        @DisplayName("should return false when DTO labels list is null")
        void shouldReturnFalseWhenDtoLabelsNull() {
            NodeExtraInfoDTO dto = new NodeExtraInfoDTO();
            assertFalse(NodeUtil.nodeLabelContainsAny(dto, "val1"));
        }
    }

    @Nested
    @DisplayName("nodeLabelContainsAny with List")
    class NodeLabelContainsAnyListTest {

        @Test
        @DisplayName("should return true when label value is found")
        void shouldReturnTrueWhenLabelValueFound() {
            List<BpmnNodeLabelVO> labels = Arrays.asList(
                    new BpmnNodeLabelVO("val1", "name1"),
                    new BpmnNodeLabelVO("val2", "name2")
            );
            assertTrue(NodeUtil.nodeLabelContainsAny(labels, "val2"));
        }

        @Test
        @DisplayName("should return false when label value is not found")
        void shouldReturnFalseWhenLabelValueNotFound() {
            List<BpmnNodeLabelVO> labels = Collections.singletonList(
                    new BpmnNodeLabelVO("val1", "name1")
            );
            assertFalse(NodeUtil.nodeLabelContainsAny(labels, "val99"));
        }

        @Test
        @DisplayName("should return true when no label values provided")
        void shouldReturnTrueWhenNoLabelValues() {
            List<BpmnNodeLabelVO> labels = Collections.singletonList(
                    new BpmnNodeLabelVO("val1", "name1")
            );
            assertTrue(NodeUtil.nodeLabelContainsAny(labels));
        }

        @Test
        @DisplayName("should return false when list is empty")
        void shouldReturnFalseWhenListEmpty() {
            assertFalse(NodeUtil.nodeLabelContainsAny(Collections.emptyList(), "val1"));
        }

        @Test
        @DisplayName("should match any of multiple label values")
        void shouldMatchAnyOfMultipleLabelValues() {
            List<BpmnNodeLabelVO> labels = Arrays.asList(
                    new BpmnNodeLabelVO("val1", "name1"),
                    new BpmnNodeLabelVO("val2", "name2")
            );
            assertTrue(NodeUtil.nodeLabelContainsAny(labels, "val3", "val2"));
            assertFalse(NodeUtil.nodeLabelContainsAny(labels, "val3", "val4"));
        }
    }

    @Nested
    @DisplayName("repeatButtonFilter")
    class RepeatButtonFilterTest {

        @Test
        @DisplayName("should remove duplicate button types keeping first")
        void shouldRemoveDuplicateButtonTypesKeepingFirst() {
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
        @DisplayName("should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(Collections.emptyList());
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return same list when no duplicates")
        void shouldReturnSameListWhenNoDuplicates() {
            ProcessActionButtonVo btn1 = ProcessActionButtonVo.builder().buttonType(1).name("Submit").build();
            ProcessActionButtonVo btn2 = ProcessActionButtonVo.builder().buttonType(2).name("Reject").build();
            ProcessActionButtonVo btn3 = ProcessActionButtonVo.builder().buttonType(3).name("Cancel").build();

            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(Arrays.asList(btn1, btn2, btn3));

            assertEquals(3, result.size());
            assertEquals(1, result.get(0).getButtonType());
            assertEquals(2, result.get(1).getButtonType());
            assertEquals(3, result.get(2).getButtonType());
        }

        @Test
        @DisplayName("should handle multiple duplicates of same type")
        void shouldHandleMultipleDuplicatesOfSameType() {
            ProcessActionButtonVo btn1 = ProcessActionButtonVo.builder().buttonType(1).name("First").build();
            ProcessActionButtonVo btn2 = ProcessActionButtonVo.builder().buttonType(1).name("Second").build();
            ProcessActionButtonVo btn3 = ProcessActionButtonVo.builder().buttonType(1).name("Third").build();

            List<ProcessActionButtonVo> result = NodeUtil.repeatButtonFilter(Arrays.asList(btn1, btn2, btn3));

            assertEquals(1, result.size());
            assertEquals("First", result.get(0).getName());
        }
    }

    @Nested
    @DisplayName("nodeSpecialProcess")
    class NodeSpecialProcessTest {

        @Test
        @DisplayName("should clear label list")
        void shouldClearLabelList() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.singletonList(new BpmnNodeLabelVO("val1", "name1")))
                    .build();

            NodeUtil.nodeSpecialProcess(node);

            assertNull(node.getLabelList());
        }

        @Test
        @DisplayName("should convert COPY_V2 to APPROVER and set isCarbonCopyNode")
        void shouldConvertCopyV2ToApprover() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode())
                    .build();

            NodeUtil.nodeSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
            assertTrue(node.getIsCarbonCopyNode());
        }

        @Test
        @DisplayName("should not change APPROVER node type")
        void shouldNotChangeApproverNodeType() {
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
        @DisplayName("should set COPY_V2 type when label contains copyNodeV2")
        void shouldSetCopyV2TypeWhenLabelContainsCopyNodeV2() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.singletonList(NodeLabelConstants.copyNodeV2))
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode(), node.getNodeType());
        }

        @Test
        @DisplayName("should not change type when label does not contain copyNodeV2")
        void shouldNotChangeTypeWhenLabelDoesNotContainCopyNodeV2() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.singletonList(new BpmnNodeLabelVO("other", "Other")))
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
        }

        @Test
        @DisplayName("should handle empty label list")
        void shouldHandleEmptyLabelList() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .labelList(Collections.emptyList())
                    .build();

            NodeUtil.nodeLabelSpecialProcess(node);

            assertEquals(NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), node.getNodeType());
        }
    }
}
