package org.openoa.common.actflowmodel;

import org.activiti.bpmn.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBpmnFlowDesignerTest extends BaseTest {

    private static final int EVENT_SIZE = 30;
    private static final int GATEWAY_SIZE = 40;
    private static final int TASK_WIDTH = 100;
    private static final int TASK_HEIGHT = 60;
    private static final int H_GAP = 50;
    private static final int MARGIN = 20;

    private BpmnModel bpmnModel;
    private org.activiti.bpmn.model.Process process;

    @BeforeEach
    void setUp() {
        bpmnModel = new BpmnModel();
        process = new org.activiti.bpmn.model.Process();
        process.setId("testProcess");
        bpmnModel.addProcess(process);
    }

    private SimpleBpmnFlowDesigner createDesigner() {
        return new SimpleBpmnFlowDesigner(bpmnModel);
    }

    private StartEvent addStartEvent(String id) {
        StartEvent start = new StartEvent();
        start.setId(id);
        process.addFlowElement(start);
        return start;
    }

    private EndEvent addEndEvent(String id) {
        EndEvent end = new EndEvent();
        end.setId(id);
        process.addFlowElement(end);
        return end;
    }

    private UserTask addUserTask(String id) {
        UserTask task = new UserTask();
        task.setId(id);
        process.addFlowElement(task);
        return task;
    }

    private ExclusiveGateway addExclusiveGateway(String id) {
        ExclusiveGateway gateway = new ExclusiveGateway();
        gateway.setId(id);
        process.addFlowElement(gateway);
        return gateway;
    }

    private ParallelGateway addParallelGateway(String id) {
        ParallelGateway gateway = new ParallelGateway();
        gateway.setId(id);
        process.addFlowElement(gateway);
        return gateway;
    }

    private SequenceFlow addSequenceFlow(String id, String sourceRef, String targetRef) {
        SequenceFlow flow = new SequenceFlow();
        flow.setId(id);
        flow.setSourceRef(sourceRef);
        flow.setTargetRef(targetRef);
        process.addFlowElement(flow);
        return flow;
    }

    @Nested
    @DisplayName("Simple linear flow")
    class SimpleLinearFlowTest {

        @Test
        @DisplayName("should have 3 GraphicInfo entries in locationMap")
        void shouldHaveThreeGraphicInfoEntries() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(3, locationMap.size());
            assertTrue(locationMap.containsKey("start"));
            assertTrue(locationMap.containsKey("task"));
            assertTrue(locationMap.containsKey("end"));
        }

        @Test
        @DisplayName("should position StartEvent at column 0 near MARGIN")
        void shouldPositionStartEventAtColumn0() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            assertNotNull(startInfo);
            assertEquals(MARGIN, startInfo.getX(), 0.01);
        }

        @Test
        @DisplayName("should position UserTask at column 1 after StartEvent")
        void shouldPositionUserTaskAtColumn1() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            GraphicInfo taskInfo = bpmnModel.getGraphicInfo("task");
            assertNotNull(taskInfo);
            assertTrue(taskInfo.getX() > startInfo.getX() + EVENT_SIZE);
        }

        @Test
        @DisplayName("should position EndEvent at column 2 after UserTask")
        void shouldPositionEndEventAtColumn2() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo taskInfo = bpmnModel.getGraphicInfo("task");
            GraphicInfo endInfo = bpmnModel.getGraphicInfo("end");
            assertNotNull(endInfo);
            assertTrue(endInfo.getX() > taskInfo.getX() + TASK_WIDTH);
        }

        @Test
        @DisplayName("should have 2 waypoints for each sequence flow")
        void shouldHaveTwoWaypointsPerFlow() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            List<GraphicInfo> flow1Waypoints = bpmnModel.getFlowLocationGraphicInfo("flow1");
            assertNotNull(flow1Waypoints);
            assertEquals(2, flow1Waypoints.size());

            List<GraphicInfo> flow2Waypoints = bpmnModel.getFlowLocationGraphicInfo("flow2");
            assertNotNull(flow2Waypoints);
            assertEquals(2, flow2Waypoints.size());
        }

        @Test
        @DisplayName("waypoints should go from source right-center to target left-center")
        void shouldHaveCorrectWaypointCoordinates() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            GraphicInfo taskInfo = bpmnModel.getGraphicInfo("task");
            List<GraphicInfo> flow1Waypoints = bpmnModel.getFlowLocationGraphicInfo("flow1");

            double expectedSourceX = startInfo.getX() + startInfo.getWidth();
            double expectedSourceY = startInfo.getY() + startInfo.getHeight() / 2.0;
            double expectedTargetX = taskInfo.getX();
            double expectedTargetY = taskInfo.getY() + taskInfo.getHeight() / 2.0;

            assertEquals(expectedSourceX, flow1Waypoints.get(0).getX(), 0.01);
            assertEquals(expectedSourceY, flow1Waypoints.get(0).getY(), 0.01);
            assertEquals(expectedTargetX, flow1Waypoints.get(1).getX(), 0.01);
            assertEquals(expectedTargetY, flow1Waypoints.get(1).getY(), 0.01);
        }
    }

    @Nested
    @DisplayName("Flow with exclusive gateway")
    class ExclusiveGatewayFlowTest {

        @Test
        @DisplayName("should position all nodes in exclusive gateway flow")
        void shouldPositionAllNodes() {
            addStartEvent("start");
            ExclusiveGateway gw = addExclusiveGateway("gw");
            addUserTask("task1");
            addUserTask("task2");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "gw");
            addSequenceFlow("flow2", "gw", "task1");
            addSequenceFlow("flow3", "gw", "task2");
            addSequenceFlow("flow4", "task1", "end");
            addSequenceFlow("flow5", "task2", "end");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(5, locationMap.size());
            assertTrue(locationMap.containsKey("start"));
            assertTrue(locationMap.containsKey("gw"));
            assertTrue(locationMap.containsKey("task1"));
            assertTrue(locationMap.containsKey("task2"));
            assertTrue(locationMap.containsKey("end"));
        }

        @Test
        @DisplayName("should position gateway between start and tasks")
        void shouldPositionGatewayBetweenStartAndTasks() {
            addStartEvent("start");
            addExclusiveGateway("gw");
            addUserTask("task1");
            addUserTask("task2");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "gw");
            addSequenceFlow("flow2", "gw", "task1");
            addSequenceFlow("flow3", "gw", "task2");
            addSequenceFlow("flow4", "task1", "end");
            addSequenceFlow("flow5", "task2", "end");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            GraphicInfo gwInfo = bpmnModel.getGraphicInfo("gw");
            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");

            assertTrue(gwInfo.getX() > startInfo.getX() + EVENT_SIZE);
            assertTrue(task1Info.getX() > gwInfo.getX() + GATEWAY_SIZE);
            assertTrue(task2Info.getX() > gwInfo.getX() + GATEWAY_SIZE);
        }

        @Test
        @DisplayName("should position parallel tasks at same x with different y")
        void shouldPositionParallelTasksAtSameX() {
            addStartEvent("start");
            addExclusiveGateway("gw");
            addUserTask("task1");
            addUserTask("task2");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "gw");
            addSequenceFlow("flow2", "gw", "task1");
            addSequenceFlow("flow3", "gw", "task2");
            addSequenceFlow("flow4", "task1", "end");
            addSequenceFlow("flow5", "task2", "end");

            createDesigner().execute();

            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");

            assertEquals(task1Info.getX(), task2Info.getX(), 0.01);
            assertNotEquals(task1Info.getY(), task2Info.getY());
        }
    }

    @Nested
    @DisplayName("Empty process")
    class EmptyProcessTest {

        @Test
        @DisplayName("should not throw on empty process")
        void shouldNotThrowOnEmptyProcess() {
            assertDoesNotThrow(() -> createDesigner().execute());
        }

        @Test
        @DisplayName("should have empty locationMap for empty process")
        void shouldHaveEmptyLocationMap() {
            createDesigner().execute();

            assertTrue(bpmnModel.getLocationMap().isEmpty());
        }

        @Test
        @DisplayName("should have empty flowLocationMap for empty process")
        void shouldHaveEmptyFlowLocationMap() {
            createDesigner().execute();

            assertTrue(bpmnModel.getFlowLocationMap().isEmpty());
        }
    }

    @Nested
    @DisplayName("Process with only a StartEvent")
    class OnlyStartEventTest {

        @Test
        @DisplayName("should have 1 GraphicInfo entry")
        void shouldHaveOneGraphicInfoEntry() {
            addStartEvent("start");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(1, locationMap.size());
            assertTrue(locationMap.containsKey("start"));
        }

        @Test
        @DisplayName("should position StartEvent at MARGIN")
        void shouldPositionStartEventAtMargin() {
            addStartEvent("start");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            assertNotNull(startInfo);
            assertEquals(MARGIN, startInfo.getX(), 0.01);
        }
    }

    @Nested
    @DisplayName("Parallel gateway flow")
    class ParallelGatewayFlowTest {

        @Test
        @DisplayName("should position all nodes in parallel gateway flow")
        void shouldPositionAllNodes() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "fork");
            addSequenceFlow("flow2", "fork", "task1");
            addSequenceFlow("flow3", "fork", "task2");
            addSequenceFlow("flow4", "task1", "join");
            addSequenceFlow("flow5", "task2", "join");
            addSequenceFlow("flow6", "join", "end");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(6, locationMap.size());
            assertTrue(locationMap.containsKey("start"));
            assertTrue(locationMap.containsKey("fork"));
            assertTrue(locationMap.containsKey("task1"));
            assertTrue(locationMap.containsKey("task2"));
            assertTrue(locationMap.containsKey("join"));
            assertTrue(locationMap.containsKey("end"));
        }

        @Test
        @DisplayName("should position fork gateway before parallel tasks")
        void shouldPositionForkBeforeTasks() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "fork");
            addSequenceFlow("flow2", "fork", "task1");
            addSequenceFlow("flow3", "fork", "task2");
            addSequenceFlow("flow4", "task1", "join");
            addSequenceFlow("flow5", "task2", "join");
            addSequenceFlow("flow6", "join", "end");

            createDesigner().execute();

            GraphicInfo forkInfo = bpmnModel.getGraphicInfo("fork");
            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");

            assertTrue(task1Info.getX() > forkInfo.getX() + GATEWAY_SIZE);
            assertTrue(task2Info.getX() > forkInfo.getX() + GATEWAY_SIZE);
        }

        @Test
        @DisplayName("should position join gateway after parallel tasks")
        void shouldPositionJoinAfterTasks() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "fork");
            addSequenceFlow("flow2", "fork", "task1");
            addSequenceFlow("flow3", "fork", "task2");
            addSequenceFlow("flow4", "task1", "join");
            addSequenceFlow("flow5", "task2", "join");
            addSequenceFlow("flow6", "join", "end");

            createDesigner().execute();

            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");
            GraphicInfo joinInfo = bpmnModel.getGraphicInfo("join");

            assertTrue(joinInfo.getX() > task1Info.getX() + TASK_WIDTH);
            assertTrue(joinInfo.getX() > task2Info.getX() + TASK_WIDTH);
        }

        @Test
        @DisplayName("should position end event after join gateway")
        void shouldPositionEndAfterJoin() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "fork");
            addSequenceFlow("flow2", "fork", "task1");
            addSequenceFlow("flow3", "fork", "task2");
            addSequenceFlow("flow4", "task1", "join");
            addSequenceFlow("flow5", "task2", "join");
            addSequenceFlow("flow6", "join", "end");

            createDesigner().execute();

            GraphicInfo joinInfo = bpmnModel.getGraphicInfo("join");
            GraphicInfo endInfo = bpmnModel.getGraphicInfo("end");

            assertTrue(endInfo.getX() > joinInfo.getX() + GATEWAY_SIZE);
        }
    }

    @Nested
    @DisplayName("Node dimensions in GraphicInfo")
    class NodeDimensionsTest {

        @Test
        @DisplayName("StartEvent should have width=30 height=30")
        void startEventDimensions() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            assertEquals(EVENT_SIZE, startInfo.getWidth(), 0.01);
            assertEquals(EVENT_SIZE, startInfo.getHeight(), 0.01);
        }

        @Test
        @DisplayName("EndEvent should have width=30 height=30")
        void endEventDimensions() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo endInfo = bpmnModel.getGraphicInfo("end");
            assertEquals(EVENT_SIZE, endInfo.getWidth(), 0.01);
            assertEquals(EVENT_SIZE, endInfo.getHeight(), 0.01);
        }

        @Test
        @DisplayName("Gateway should have width=40 height=40")
        void gatewayDimensions() {
            addStartEvent("start");
            addExclusiveGateway("gw");
            addUserTask("task1");
            addUserTask("task2");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "gw");
            addSequenceFlow("flow2", "gw", "task1");
            addSequenceFlow("flow3", "gw", "task2");
            addSequenceFlow("flow4", "task1", "end");
            addSequenceFlow("flow5", "task2", "end");

            createDesigner().execute();

            GraphicInfo gwInfo = bpmnModel.getGraphicInfo("gw");
            assertEquals(GATEWAY_SIZE, gwInfo.getWidth(), 0.01);
            assertEquals(GATEWAY_SIZE, gwInfo.getHeight(), 0.01);
        }

        @Test
        @DisplayName("UserTask should have width=100 height=60")
        void userTaskDimensions() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            createDesigner().execute();

            GraphicInfo taskInfo = bpmnModel.getGraphicInfo("task");
            assertEquals(TASK_WIDTH, taskInfo.getWidth(), 0.01);
            assertEquals(TASK_HEIGHT, taskInfo.getHeight(), 0.01);
        }

        @Test
        @DisplayName("ParallelGateway should have width=40 height=40")
        void parallelGatewayDimensions() {
            addStartEvent("start");
            addParallelGateway("pgw");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "pgw");
            addSequenceFlow("flow2", "pgw", "task");
            addSequenceFlow("flow3", "task", "end");

            createDesigner().execute();

            GraphicInfo pgwInfo = bpmnModel.getGraphicInfo("pgw");
            assertEquals(GATEWAY_SIZE, pgwInfo.getWidth(), 0.01);
            assertEquals(GATEWAY_SIZE, pgwInfo.getHeight(), 0.01);
        }
    }

    @Nested
    @DisplayName("Execute clears previous layout")
    class ClearPreviousLayoutTest {

        @Test
        @DisplayName("should clear existing locationMap entries")
        void shouldClearLocationMap() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            bpmnModel.addGraphicInfo("dummy", new GraphicInfo());

            createDesigner().execute();

            assertFalse(bpmnModel.getLocationMap().containsKey("dummy"));
        }

        @Test
        @DisplayName("should clear existing flowLocationMap entries")
        void shouldClearFlowLocationMap() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            GraphicInfo dummyWaypoint = new GraphicInfo();
            dummyWaypoint.setX(999);
            dummyWaypoint.setY(999);
            bpmnModel.addFlowGraphicInfoList("oldFlow", java.util.Collections.singletonList(dummyWaypoint));

            createDesigner().execute();

            assertFalse(bpmnModel.getFlowLocationMap().containsKey("oldFlow"));
        }

        @Test
        @DisplayName("should replace old layout with new layout on re-execute")
        void shouldReplaceOldLayoutOnReExecute() {
            addStartEvent("start");
            addUserTask("task");
            addEndEvent("end");
            addSequenceFlow("flow1", "start", "task");
            addSequenceFlow("flow2", "task", "end");

            SimpleBpmnFlowDesigner designer = createDesigner();
            designer.execute();

            GraphicInfo firstStartInfo = bpmnModel.getGraphicInfo("start");
            double firstX = firstStartInfo.getX();
            double firstY = firstStartInfo.getY();

            designer.execute();

            GraphicInfo secondStartInfo = bpmnModel.getGraphicInfo("start");
            assertEquals(firstX, secondStartInfo.getX(), 0.01);
            assertEquals(firstY, secondStartInfo.getY(), 0.01);
        }
    }

    @Nested
    @DisplayName("Three-branch parallel gateway flow")
    class ThreeBranchParallelFlowTest {

        @Test
        @DisplayName("should position all 3 parallel tasks at same x")
        void shouldPositionThreeParallelTasksAtSameX() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addUserTask("task3");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "fork");
            addSequenceFlow("f2", "fork", "task1");
            addSequenceFlow("f3", "fork", "task2");
            addSequenceFlow("f4", "fork", "task3");
            addSequenceFlow("f5", "task1", "join");
            addSequenceFlow("f6", "task2", "join");
            addSequenceFlow("f7", "task3", "join");
            addSequenceFlow("f8", "join", "end");

            createDesigner().execute();

            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");
            GraphicInfo task3Info = bpmnModel.getGraphicInfo("task3");

            assertEquals(task1Info.getX(), task2Info.getX(), 0.01);
            assertEquals(task2Info.getX(), task3Info.getX(), 0.01);
            assertNotEquals(task1Info.getY(), task2Info.getY());
            assertNotEquals(task2Info.getY(), task3Info.getY());
        }

        @Test
        @DisplayName("should position join after all 3 parallel tasks")
        void shouldPositionJoinAfterAllThreeTasks() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addUserTask("task3");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "fork");
            addSequenceFlow("f2", "fork", "task1");
            addSequenceFlow("f3", "fork", "task2");
            addSequenceFlow("f4", "fork", "task3");
            addSequenceFlow("f5", "task1", "join");
            addSequenceFlow("f6", "task2", "join");
            addSequenceFlow("f7", "task3", "join");
            addSequenceFlow("f8", "join", "end");

            createDesigner().execute();

            GraphicInfo joinInfo = bpmnModel.getGraphicInfo("join");
            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");
            GraphicInfo task3Info = bpmnModel.getGraphicInfo("task3");

            assertTrue(joinInfo.getX() > task1Info.getX() + TASK_WIDTH);
            assertTrue(joinInfo.getX() > task2Info.getX() + TASK_WIDTH);
            assertTrue(joinInfo.getX() > task3Info.getX() + TASK_WIDTH);
        }
    }

    @Nested
    @DisplayName("Long linear chain flow")
    class LongLinearChainTest {

        @Test
        @DisplayName("should position 10 sequential tasks in increasing x order")
        void shouldPositionTenTasksInOrder() {
            addStartEvent("start");
            for (int i = 1; i <= 10; i++) {
                addUserTask("task" + i);
            }
            addEndEvent("end");
            addSequenceFlow("f0", "start", "task1");
            for (int i = 1; i < 10; i++) {
                addSequenceFlow("f" + i, "task" + i, "task" + (i + 1));
            }
            addSequenceFlow("f10", "task10", "end");

            createDesigner().execute();

            double prevX = bpmnModel.getGraphicInfo("start").getX();
            for (int i = 1; i <= 10; i++) {
                GraphicInfo taskInfo = bpmnModel.getGraphicInfo("task" + i);
                assertTrue(taskInfo.getX() > prevX, "task" + i + " should be after previous");
                prevX = taskInfo.getX();
            }
            GraphicInfo endInfo = bpmnModel.getGraphicInfo("end");
            assertTrue(endInfo.getX() > prevX);
        }

        @Test
        @DisplayName("should have correct number of location entries for long chain")
        void shouldHaveCorrectLocationCount() {
            addStartEvent("start");
            for (int i = 1; i <= 10; i++) {
                addUserTask("task" + i);
            }
            addEndEvent("end");
            addSequenceFlow("f0", "start", "task1");
            for (int i = 1; i < 10; i++) {
                addSequenceFlow("f" + i, "task" + i, "task" + (i + 1));
            }
            addSequenceFlow("f10", "task10", "end");

            createDesigner().execute();

            assertEquals(12, bpmnModel.getLocationMap().size());
        }
    }

    @Nested
    @DisplayName("Start and end only flow")
    class StartEndOnlyTest {

        @Test
        @DisplayName("should position start and end events")
        void shouldPositionStartAndEnd() {
            addStartEvent("start");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "end");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(2, locationMap.size());
            assertTrue(locationMap.containsKey("start"));
            assertTrue(locationMap.containsKey("end"));
        }

        @Test
        @DisplayName("should position end after start")
        void shouldPositionEndAfterStart() {
            addStartEvent("start");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "end");

            createDesigner().execute();

            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            GraphicInfo endInfo = bpmnModel.getGraphicInfo("end");
            assertTrue(endInfo.getX() > startInfo.getX());
        }
    }

    @Nested
    @DisplayName("Y-axis centering for parallel branches")
    class YAxisCenteringTest {

        @Test
        @DisplayName("first parallel task should be above center line")
        void firstTaskAboveCenter() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1");
            addUserTask("task2");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "fork");
            addSequenceFlow("f2", "fork", "task1");
            addSequenceFlow("f3", "fork", "task2");
            addSequenceFlow("f4", "task1", "join");
            addSequenceFlow("f5", "task2", "join");
            addSequenceFlow("f6", "join", "end");

            createDesigner().execute();

            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");
            assertTrue(task1Info.getY() < task2Info.getY());
        }
    }

    @Nested
    @DisplayName("Nested gateway flow")
    class NestedGatewayFlowTest {

        @Test
        @DisplayName("should position all nodes in nested exclusive gateway flow")
        void shouldPositionAllNodesInNestedExclusiveGateway() {
            addStartEvent("start");
            addExclusiveGateway("gw1");
            addUserTask("task1");
            addExclusiveGateway("gw2");
            addUserTask("task2a");
            addUserTask("task2b");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "gw1");
            addSequenceFlow("f2", "gw1", "task1");
            addSequenceFlow("f3", "gw1", "gw2");
            addSequenceFlow("f4", "gw2", "task2a");
            addSequenceFlow("f5", "gw2", "task2b");
            addSequenceFlow("f6", "task1", "end");
            addSequenceFlow("f7", "task2a", "end");
            addSequenceFlow("f8", "task2b", "end");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(7, locationMap.size());
        }

        @Test
        @DisplayName("should position nested gateway after outer gateway")
        void shouldPositionNestedGatewayAfterOuterGateway() {
            addStartEvent("start");
            addExclusiveGateway("gw1");
            addUserTask("task1");
            addExclusiveGateway("gw2");
            addUserTask("task2a");
            addUserTask("task2b");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "gw1");
            addSequenceFlow("f2", "gw1", "task1");
            addSequenceFlow("f3", "gw1", "gw2");
            addSequenceFlow("f4", "gw2", "task2a");
            addSequenceFlow("f5", "gw2", "task2b");
            addSequenceFlow("f6", "task1", "end");
            addSequenceFlow("f7", "task2a", "end");
            addSequenceFlow("f8", "task2b", "end");

            createDesigner().execute();

            GraphicInfo gw1Info = bpmnModel.getGraphicInfo("gw1");
            GraphicInfo gw2Info = bpmnModel.getGraphicInfo("gw2");
            assertTrue(gw2Info.getX() > gw1Info.getX() + GATEWAY_SIZE);
        }
    }

    @Nested
    @DisplayName("Multiple tasks between gateways")
    class MultipleTasksBetweenGatewaysTest {

        @Test
        @DisplayName("should position sequential tasks in parallel branch")
        void shouldPositionSequentialTasksInParallelBranch() {
            addStartEvent("start");
            addParallelGateway("fork");
            addUserTask("task1a");
            addUserTask("task1b");
            addUserTask("task2");
            addParallelGateway("join");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "fork");
            addSequenceFlow("f2", "fork", "task1a");
            addSequenceFlow("f3", "task1a", "task1b");
            addSequenceFlow("f4", "fork", "task2");
            addSequenceFlow("f5", "task1b", "join");
            addSequenceFlow("f6", "task2", "join");
            addSequenceFlow("f7", "join", "end");

            createDesigner().execute();

            GraphicInfo task1aInfo = bpmnModel.getGraphicInfo("task1a");
            GraphicInfo task1bInfo = bpmnModel.getGraphicInfo("task1b");
            assertTrue(task1bInfo.getX() > task1aInfo.getX() + TASK_WIDTH);
        }
    }

    @Nested
    @DisplayName("Exclusive gateway with default flow")
    class ExclusiveGatewayDefaultFlowTest {

        @Test
        @DisplayName("should position all nodes with 3 exclusive branches")
        void shouldPositionAllNodesWithThreeBranches() {
            addStartEvent("start");
            addExclusiveGateway("gw");
            addUserTask("task1");
            addUserTask("task2");
            addUserTask("task3");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "gw");
            addSequenceFlow("f2", "gw", "task1");
            addSequenceFlow("f3", "gw", "task2");
            addSequenceFlow("f4", "gw", "task3");
            addSequenceFlow("f5", "task1", "end");
            addSequenceFlow("f6", "task2", "end");
            addSequenceFlow("f7", "task3", "end");

            createDesigner().execute();

            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            assertEquals(6, locationMap.size());
            assertTrue(locationMap.containsKey("start"));
            assertTrue(locationMap.containsKey("gw"));
            assertTrue(locationMap.containsKey("task1"));
            assertTrue(locationMap.containsKey("task2"));
            assertTrue(locationMap.containsKey("task3"));
            assertTrue(locationMap.containsKey("end"));
        }

        @Test
        @DisplayName("should position 3 exclusive branch tasks at same x")
        void shouldPositionThreeExclusiveBranchTasksAtSameX() {
            addStartEvent("start");
            addExclusiveGateway("gw");
            addUserTask("task1");
            addUserTask("task2");
            addUserTask("task3");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "gw");
            addSequenceFlow("f2", "gw", "task1");
            addSequenceFlow("f3", "gw", "task2");
            addSequenceFlow("f4", "gw", "task3");
            addSequenceFlow("f5", "task1", "end");
            addSequenceFlow("f6", "task2", "end");
            addSequenceFlow("f7", "task3", "end");

            createDesigner().execute();

            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");
            GraphicInfo task3Info = bpmnModel.getGraphicInfo("task3");

            assertEquals(task1Info.getX(), task2Info.getX(), 0.01);
            assertEquals(task2Info.getX(), task3Info.getX(), 0.01);
        }
    }

    @Nested
    @DisplayName("Flow with ServiceTask")
    class ServiceTaskFlowTest {

        @Test
        @DisplayName("should position ServiceTask in linear flow")
        void shouldPositionServiceTaskInLinearFlow() {
            addStartEvent("start");
            ServiceTask serviceTask = new ServiceTask();
            serviceTask.setId("svc1");
            process.addFlowElement(serviceTask);
            addUserTask("task1");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "svc1");
            addSequenceFlow("f2", "svc1", "task1");
            addSequenceFlow("f3", "task1", "end");

            createDesigner().execute();

            GraphicInfo svcInfo = bpmnModel.getGraphicInfo("svc1");
            assertNotNull(svcInfo);
            GraphicInfo startInfo = bpmnModel.getGraphicInfo("start");
            GraphicInfo taskInfo = bpmnModel.getGraphicInfo("task1");
            assertTrue(svcInfo.getX() > startInfo.getX() + EVENT_SIZE);
            assertTrue(taskInfo.getX() > svcInfo.getX() + TASK_WIDTH);
        }
    }

    @Nested
    @DisplayName("Y-axis layout for single linear flow")
    class YAxisLayoutTest {

        @Test
        @DisplayName("all UserTask nodes in linear flow should have same Y coordinate")
        void allUserTasksShouldHaveSameY() {
            addStartEvent("start");
            addUserTask("task1");
            addUserTask("task2");
            addEndEvent("end");
            addSequenceFlow("f1", "start", "task1");
            addSequenceFlow("f2", "task1", "task2");
            addSequenceFlow("f3", "task2", "end");

            createDesigner().execute();

            GraphicInfo task1Info = bpmnModel.getGraphicInfo("task1");
            GraphicInfo task2Info = bpmnModel.getGraphicInfo("task2");

            assertEquals(task1Info.getY(), task2Info.getY(), 0.01);
        }
    }
}
