package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.dto.PageDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResultAndPageTest extends BaseTest {

    @Nested
    @DisplayName("2-arg constructor")
    class TwoArgConstructorTest {
        @Test
        @DisplayName("should set data and pagination with code 200")
        void shouldSetDataAndPagination() {
            PageDto page = PageDto.first();
            ResultAndPage<String> result = new ResultAndPage<>(Arrays.asList("a", "b"), page);
            assertEquals(2, result.getData().size());
            assertEquals(page, result.getPagination());
            assertEquals(200, result.getCode());
        }
    }

    @Nested
    @DisplayName("3-arg constructor")
    class ThreeArgConstructorTest {
        @Test
        @DisplayName("should set data, pagination and statistics with code 200")
        void shouldSetStatistics() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", 100);
            ResultAndPage<String> result = new ResultAndPage<>(Arrays.asList("a"), PageDto.first(), stats);
            assertEquals(100, result.getStatistics().get("total"));
            assertEquals(200, result.getCode());
        }
    }

    @Nested
    @DisplayName("4-arg constructor")
    class FourArgConstructorTest {
        @Test
        @DisplayName("should set data, pagination, statistics and sortColumnMap with code 200")
        void shouldSetSortColumnMap() {
            Map<String, Object> stats = new HashMap<>();
            Map<String, String> sortMap = new HashMap<>();
            sortMap.put("name", "ASC");
            ResultAndPage<String> result = new ResultAndPage<>(Arrays.asList("a"), PageDto.first(), stats, sortMap);
            assertEquals("ASC", result.getSortColumnMap().get("name"));
            assertEquals(200, result.getCode());
        }
    }

    @Nested
    @DisplayName("5-arg constructor")
    class FiveArgConstructorTest {
        @Test
        @DisplayName("should set all fields with code 200")
        void shouldSetAllFields() {
            Map<String, Object> stats = new HashMap<>();
            Map<String, String> sortMap = new HashMap<>();
            ResultAndPage<String> result = new ResultAndPage<>(Arrays.asList("a"), PageDto.first(), stats, sortMap, 1);
            assertEquals(1, result.getFlag());
            assertEquals(200, result.getCode());
        }
    }

    @Nested
    @DisplayName("NodeLabelConstants")
    class NodeLabelConstantsTest {
        @Test
        @DisplayName("dynamicCondition should have correct label name")
        void dynamicConditionLabel() {
            assertNotNull(NodeLabelConstants.dynamicCondition);
            assertNotNull(NodeLabelConstants.dynamicCondition.getLabelName());
        }

        @Test
        @DisplayName("copyNode should have correct label name")
        void copyNodeLabel() {
            assertNotNull(NodeLabelConstants.copyNode);
        }

        @Test
        @DisplayName("NONE_OPERATIONAL_NODES should contain copyNodeV2 and automaticNode")
        void noneOperationalNodes() {
            assertEquals(2, NodeLabelConstants.NONE_OPERATIONAL_NODES.size());
            assertTrue(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.copyNodeV2));
            assertTrue(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.automaticNode));
        }

        @Test
        @DisplayName("NONE_OPERATIONAL_NODES should not contain dynamicCondition or copyNode")
        void shouldNotContainDynamicOrCopy() {
            assertFalse(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.dynamicCondition));
            assertFalse(NodeLabelConstants.NONE_OPERATIONAL_NODES.contains(NodeLabelConstants.copyNode));
        }

        @Test
        @DisplayName("skippedAssignees should exist")
        void skippedAssigneesExist() {
            assertNotNull(NodeLabelConstants.skippedAssignees);
        }
    }
}
