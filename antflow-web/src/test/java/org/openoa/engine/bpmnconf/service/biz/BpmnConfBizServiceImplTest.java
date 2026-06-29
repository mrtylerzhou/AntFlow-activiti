package org.openoa.engine.bpmnconf.service.biz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodeVo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BpmnConfBizServiceImplTest extends BaseTest {

    @Nested
    @DisplayName("getNodeMapAndStartNode - static method via reflection")
    class GetNodeMapAndStartNodeTest {
        @Test
        @DisplayName("should find start node and build map")
        void shouldFindStartNodeAndBuildMap() throws Exception {
            BpmnNodeVo startNode = createNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "next1");
            BpmnNodeVo node1 = createNode("next1", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null);

            List<BpmnNodeVo> nodeList = Arrays.asList(startNode, node1);
            Map<String, BpmnNodeVo> map = new HashMap<>();

            BpmnNodeVo result = invokeGetNodeMapAndStartNode(nodeList, map);

            assertNotNull(result);
            assertSame(startNode, result);
            assertEquals(2, map.size());
            assertTrue(map.containsKey("start"));
            assertTrue(map.containsKey("next1"));
        }

        @Test
        @DisplayName("should throw when more than one start node exists")
        void shouldThrowWhenMultipleStartNodes() throws Exception {
            BpmnNodeVo start1 = createNode("start1", NodeTypeEnum.NODE_TYPE_START.getCode(), "next1");
            BpmnNodeVo start2 = createNode("start2", NodeTypeEnum.NODE_TYPE_START.getCode(), "next1");
            BpmnNodeVo end = createNode("next1", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null);

            List<BpmnNodeVo> nodeList = Arrays.asList(start1, start2, end);
            Map<String, BpmnNodeVo> map = new HashMap<>();

            InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                    () -> invokeGetNodeMapAndStartNode(nodeList, map));
            assertTrue(ex.getCause() instanceof AFBizException);
        }

        @Test
        @DisplayName("should throw when no end node exists")
        void shouldThrowWhenNoEndNode() throws Exception {
            BpmnNodeVo startNode = createNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "next1");
            BpmnNodeVo node1 = createNode("next1", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), "next2");
            BpmnNodeVo node2 = createNode("next2", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), "next3");

            List<BpmnNodeVo> nodeList = Arrays.asList(startNode, node1, node2);
            Map<String, BpmnNodeVo> map = new HashMap<>();

            InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                    () -> invokeGetNodeMapAndStartNode(nodeList, map));
            assertTrue(ex.getCause() instanceof AFBizException);
        }

        @Test
        @DisplayName("should return null start node when no start node exists but end node exists")
        void shouldReturnNullWhenNoStartNode() throws Exception {
            BpmnNodeVo node1 = createNode("node1", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null);

            List<BpmnNodeVo> nodeList = Arrays.asList(node1);
            Map<String, BpmnNodeVo> map = new HashMap<>();

            BpmnNodeVo result = invokeGetNodeMapAndStartNode(nodeList, map);

            assertNull(result);
        }

        @Test
        @DisplayName("should handle node with blank nodeTo as end node")
        void shouldHandleBlankNodeToAsEndNode() throws Exception {
            BpmnNodeVo startNode = createNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "next1");
            BpmnNodeVo endNode = createNode("next1", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), "");
            endNode.getParams().setNodeTo("");

            List<BpmnNodeVo> nodeList = Arrays.asList(startNode, endNode);
            Map<String, BpmnNodeVo> map = new HashMap<>();

            BpmnNodeVo result = invokeGetNodeMapAndStartNode(nodeList, map);

            assertNotNull(result);
            assertSame(startNode, result);
        }

        private BpmnNodeVo invokeGetNodeMapAndStartNode(List<BpmnNodeVo> nodeList, Map<String, BpmnNodeVo> map) throws Exception {
            Method method = BpmnConfBizServiceImpl.class.getDeclaredMethod("getNodeMapAndStartNode", List.class, Map.class);
            method.setAccessible(true);
            return (BpmnNodeVo) method.invoke(null, nodeList, map);
        }
    }

    @Nested
    @DisplayName("formatOutSideFormCode - private method via reflection")
    class FormatOutSideFormCodeTest {
        @Test
        @DisplayName("should extract formCode after first underscore")
        void shouldExtractFormCodeAfterFirstUnderscore() throws Exception {
            org.openoa.base.vo.BpmnConfVo vo = new org.openoa.base.vo.BpmnConfVo();
            vo.setFormCode("PARTY_A_form001");

            String result = invokeFormatOutSideFormCode(vo);

            assertEquals("A_form001", result);
        }

        @Test
        @DisplayName("should extract after first underscore when multiple underscores")
        void shouldExtractAfterFirstUnderscore() throws Exception {
            org.openoa.base.vo.BpmnConfVo vo = new org.openoa.base.vo.BpmnConfVo();
            vo.setFormCode("MARK_form_001");

            String result = invokeFormatOutSideFormCode(vo);

            assertEquals("form_001", result);
        }

        private String invokeFormatOutSideFormCode(org.openoa.base.vo.BpmnConfVo vo) throws Exception {
            Method method = BpmnConfBizServiceImpl.class.getDeclaredMethod("formatOutSideFormCode", org.openoa.base.vo.BpmnConfVo.class);
            method.setAccessible(true);
            BpmnConfBizServiceImpl instance = new BpmnConfBizServiceImpl();
            return (String) method.invoke(instance, vo);
        }
    }

    private BpmnNodeVo createNode(String nodeId, Integer nodeType, String nodeTo) {
        BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                .nodeTo(nodeTo)
                .paramType(1)
                .assignee(BpmnNodeParamsAssigneeVo.builder()
                        .assigneeName("testUser")
                        .elementName("testElement")
                        .build())
                .build();

        return BpmnNodeVo.builder()
                .nodeId(nodeId)
                .nodeType(nodeType)
                .nodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode())
                .params(params)
                .build();
    }
}
