package org.openoa.common.util;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.common.constant.enus.ElementPropertyEnum;
import org.openoa.BaseTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ArbitrationSignTest extends BaseTest {

    private List<String> assignees(int n) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            list.add("user" + i);
        }
        return list;
    }

    private Map<String, String> assigneeMap(int n) {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i <= n; i++) {
            map.put("user" + i, "User " + i);
        }
        return map;
    }

    @Nested
    @DisplayName("getMultiplayerArbitrationElement")
    class GetMultiplayerArbitrationElementTest {

        @Test
        @DisplayName("should set signType=4 and elementProperty=22")
        void shouldSetSignTypeAndElementProperty() {
            List<String> collectionValue = assignees(5);
            Map<String, String> map = assigneeMap(5);

            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", collectionValue, map, 60);

            assertEquals("task1", element.getElementId());
            assertEquals("Approve", element.getElementName());
            assertEquals(SignTypeEnum.SIGN_TYPE_ARBITRATION.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_ARBITRATION.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("5 people 60% -> N=3")
        void shouldCalculateN_5ppl_60pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 60);

            assertEquals(3, element.getRequiredCount());
        }

        @Test
        @DisplayName("5 people 100% -> N=5")
        void shouldCalculateN_5ppl_100pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 100);

            assertEquals(5, element.getRequiredCount());
        }

        @Test
        @DisplayName("5 people 51% -> N=3 (ceil 2.55)")
        void shouldCalculateN_5ppl_51pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 51);

            assertEquals(3, element.getRequiredCount());
        }

        @Test
        @DisplayName("5 people 1% -> N=1 (min 1)")
        void shouldCalculateN_5ppl_1pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 1);

            assertEquals(1, element.getRequiredCount());
        }

        @Test
        @DisplayName("1 person any ratio -> N=1")
        void shouldCalculateN_1ppl_60pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(1), assigneeMap(1), 60);

            assertEquals(1, element.getRequiredCount());
        }

        @Test
        @DisplayName("3 people 34% -> N=2 (ceil 1.02)")
        void shouldCalculateN_3ppl_34pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(3), assigneeMap(3), 34);

            assertEquals(2, element.getRequiredCount());
        }

        @Test
        @DisplayName("5 people 50% -> N=3 (ceil 2.5)")
        void shouldCalculateN_5ppl_50pct() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 50);

            assertEquals(3, element.getRequiredCount());
        }

        @Test
        @DisplayName("should throw when ratio is null")
        void shouldThrowWhenRatioIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                BpmnElementUtils.getMultiplayerArbitrationElement(
                        "task1", "Approve", "userList1", assignees(5), assigneeMap(5), null);
            });
        }
    }

    @Nested
    @DisplayName("createArbitrationSignUserTask")
    class CreateArbitrationSignUserTaskTest {

        @Test
        @DisplayName("should create parallel multi-instance user task")
        void shouldCreateParallelMultiInstance() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 60);

            UserTask task = BpmnBuildUtils.createArbitrationSignUserTask(element);

            assertEquals("task1", task.getId());
            assertEquals("Approve", task.getName());
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertNotNull(mi);
            assertFalse(mi.isSequential());
        }

        @Test
        @DisplayName("should set completion condition with N=3 for 5ppl 60%")
        void shouldSetCompletionCondition() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerArbitrationElement(
                    "task1", "Approve", "userList1", assignees(5), assigneeMap(5), 60);

            UserTask task = BpmnBuildUtils.createArbitrationSignUserTask(element);
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();

            assertNotNull(mi.getCompletionCondition());
            assertTrue(mi.getCompletionCondition().contains("3"),
                    "completion condition should contain N=3, got: " + mi.getCompletionCondition());
        }

        @Test
        @DisplayName("should throw when requiredCount is null")
        void shouldThrowWhenRequiredCountIsNull() {
            BpmnConfCommonElementVo element = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approve")
                    .collectionName("userList1")
                    .signType(SignTypeEnum.SIGN_TYPE_ARBITRATION.getCode())
                    .requiredCount(null)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> {
                BpmnBuildUtils.createArbitrationSignUserTask(element);
            });
        }

        @Test
        @DisplayName("should throw when requiredCount <= 0")
        void shouldThrowWhenRequiredCountNotPositive() {
            BpmnConfCommonElementVo element = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approve")
                    .collectionName("userList1")
                    .signType(SignTypeEnum.SIGN_TYPE_ARBITRATION.getCode())
                    .requiredCount(0)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> {
                BpmnBuildUtils.createArbitrationSignUserTask(element);
            });
        }
    }

    @Nested
    @DisplayName("oppose threshold M calculation")
    class OpposeThresholdTest {

        /**
         * M = ceil(n * (100 - ratio) / 100)
         * This is a pure math test to verify the threshold logic conceptually.
         * The actual M calculation is in OpposeProcessImpl, but we verify the formula here.
         */
        private int calculateM(int n, int ratio) {
            return (int) Math.ceil(n * (100 - ratio) / 100.0);
        }

        @Test
        @DisplayName("5 people 60% -> M=2")
        void m_5ppl_60pct() {
            assertEquals(2, calculateM(5, 60));
        }

        @Test
        @DisplayName("5 people 40% -> M=3")
        void m_5ppl_40pct() {
            assertEquals(3, calculateM(5, 40));
        }

        @Test
        @DisplayName("5 people 50% -> M=3")
        void m_5ppl_50pct() {
            assertEquals(3, calculateM(5, 50));
        }

        @Test
        @DisplayName("5 people 100% -> M=0 (any oppose blocks)")
        void m_5ppl_100pct() {
            assertEquals(0, calculateM(5, 100));
        }

        @Test
        @DisplayName("N + M >= n for all ratios (no deadlock)")
        void nPlusMShouldCoverAllPeople() {
            for (int n = 1; n <= 20; n++) {
                for (int ratio = 1; ratio <= 100; ratio++) {
                    int N = (int) Math.ceil(n * ratio / 100.0);
                    N = Math.max(1, Math.min(N, n));
                    int M = calculateM(n, ratio);
                    assertTrue(N + M >= n,
                            String.format("N+M < n for n=%d ratio=%d: N=%d M=%d", n, ratio, N, M));
                }
            }
        }
    }
}
