package org.openoa.common.util;

import org.activiti.bpmn.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnBuildUtilsTest extends BaseTest {

    @Nested
    @DisplayName("createStartEvent")
    class CreateStartEventTest {
        @Test
        @DisplayName("should create start event with given id and name")
        void shouldCreateStartEvent() {
            StartEvent event = BpmnBuildUtils.createStartEvent("start1", "Start Event");

            assertEquals("start1", event.getId());
            assertEquals("Start Event", event.getName());
        }

        @Test
        @DisplayName("should create start event with null name")
        void shouldCreateStartEventWithNullName() {
            StartEvent event = BpmnBuildUtils.createStartEvent("start1", null);

            assertEquals("start1", event.getId());
            assertNull(event.getName());
        }
    }

    @Nested
    @DisplayName("createEndEvent")
    class CreateEndEventTest {
        @Test
        @DisplayName("should create end event with given id and name")
        void shouldCreateEndEvent() {
            EndEvent event = BpmnBuildUtils.createEndEvent("end1", "End Event");

            assertEquals("end1", event.getId());
            assertEquals("End Event", event.getName());
        }
    }

    @Nested
    @DisplayName("createUserTask")
    class CreateUserTaskTest {
        @Test
        @DisplayName("should create single user task with assignee param")
        void shouldCreateSingleUserTask() {
            UserTask task = BpmnBuildUtils.createUserTask("task1", "Approve", "assignee1");

            assertEquals("task1", task.getId());
            assertEquals("Approve", task.getName());
            assertEquals("${assignee1}", task.getAssignee());
            assertNotNull(task.getTaskListeners());
            assertFalse(task.getTaskListeners().isEmpty());
        }

        @Test
        @DisplayName("should create user task from element vo")
        void shouldCreateUserTaskFromElementVo() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task2")
                    .elementName("Review")
                    .assigneeParamName("reviewer")
                    .build();

            UserTask task = BpmnBuildUtils.createUserTask(elementVo);

            assertEquals("task2", task.getId());
            assertEquals("Review", task.getName());
            assertEquals("${reviewer}", task.getAssignee());
            assertNotNull(task.getTaskListeners());
        }
    }

    @Nested
    @DisplayName("createSignUserTask")
    class CreateSignUserTaskTest {
        @Test
        @DisplayName("should create sign user task with multi instance loop characteristics")
        void shouldCreateSignUserTask() {
            UserTask task = BpmnBuildUtils.createSignUserTask("signTask1", "Sign Approve", "userList", "user");

            assertEquals("signTask1", task.getId());
            assertEquals("Sign Approve", task.getName());
            assertEquals("${user}", task.getAssignee());
            assertNotNull(task.getLoopCharacteristics());
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertFalse(mi.isSequential());
            assertEquals("${userList}", mi.getInputDataItem());
            assertEquals("user", mi.getElementVariable());
            assertNull(mi.getCompletionCondition());
        }

        @Test
        @DisplayName("should create sign user task from element vo")
        void shouldCreateSignUserTaskFromElementVo() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("signTask2")
                    .elementName("Sign Review")
                    .collectionName("reviewerList")
                    .build();

            UserTask task = BpmnBuildUtils.createSignUserTask(elementVo);

            assertEquals("signTask2", task.getId());
            assertNotNull(task.getLoopCharacteristics());
            assertFalse(task.getLoopCharacteristics().isSequential());
        }
    }

    @Nested
    @DisplayName("createOrSignUserTask")
    class CreateOrSignUserTaskTest {
        @Test
        @DisplayName("should create or-sign user task with completion condition")
        void shouldCreateOrSignUserTask() {
            UserTask task = BpmnBuildUtils.createOrSignUserTask("orTask1", "Or Sign", "userList", "user");

            assertEquals("orTask1", task.getId());
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertFalse(mi.isSequential());
            assertNotNull(mi.getCompletionCondition());
            assertTrue(mi.getCompletionCondition().contains("nrOfCompletedInstances"));
        }
    }

    @Nested
    @DisplayName("createLoopUserTask")
    class CreateLoopUserTaskTest {
        @Test
        @DisplayName("should create loop user task with end loop mark")
        void shouldCreateLoopUserTaskWithEndLoopMark() {
            UserTask task = BpmnBuildUtils.createLoopUserTask("loop1", "Loop Task", "loopAssignee", "endLoopMark");

            assertEquals("loop1", task.getId());
            assertEquals("Loop Task", task.getName());
            assertEquals("${loopAssignee}", task.getAssignee());
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertTrue(mi.isSequential());
            assertEquals("10", mi.getLoopCardinality());
            assertEquals("${endLoopMark}", mi.getCompletionCondition());
        }

        @Test
        @DisplayName("should create loop user task with cardinality and collection")
        void shouldCreateLoopUserTaskWithCardinality() {
            UserTask task = BpmnBuildUtils.createLoopUserTask("loop2", "Loop Task 2", "3", "signUpList", "signUpUser");

            assertEquals("loop2", task.getId());
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertTrue(mi.isSequential());
            assertEquals("${3}", mi.getLoopCardinality());
            assertEquals("${signUpList}", mi.getInputDataItem());
            assertEquals("signUpUser", mi.getElementVariable());
        }
    }

    @Nested
    @DisplayName("createExclusiveGateway")
    class CreateExclusiveGatewayTest {
        @Test
        @DisplayName("should create exclusive gateway with id")
        void shouldCreateExclusiveGateway() {
            ExclusiveGateway gateway = BpmnBuildUtils.createExclusiveGateway("gw1");

            assertEquals("gw1", gateway.getId());
        }
    }

    @Nested
    @DisplayName("createParallelGateWay")
    class CreateParallelGatewayTest {
        @Test
        @DisplayName("should create parallel gateway with id")
        void shouldCreateParallelGateway() {
            ParallelGateway gateway = BpmnBuildUtils.createParallelGateWay("pgw1");

            assertEquals("pgw1", gateway.getId());
        }
    }

    @Nested
    @DisplayName("createSequenceFlow")
    class CreateSequenceFlowTest {
        @Test
        @DisplayName("should create sequence flow from element vo")
        void shouldCreateSequenceFlowFromElementVo() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("flow1")
                    .flowFrom("task1")
                    .flowTo("task2")
                    .isLastSequenceFlow(0)
                    .build();

            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow(elementVo);

            assertEquals("flow1", flow.getId());
            assertEquals("task1", flow.getSourceRef());
            assertEquals("task2", flow.getTargetRef());
        }

        @Test
        @DisplayName("should add execution listener when is last sequence flow")
        void shouldAddListenerWhenLastSequenceFlow() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("flow2")
                    .flowFrom("task1")
                    .flowTo("task2")
                    .isLastSequenceFlow(1)
                    .build();

            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow(elementVo);

            assertNotNull(flow.getExecutionListeners());
            assertFalse(flow.getExecutionListeners().isEmpty());
        }

        @Test
        @DisplayName("should create sequence flow with condition expression")
        void shouldCreateSequenceFlowWithCondition() {
            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow("flow3", "gw1", "task1", "amount > 1000");

            assertEquals("flow3", flow.getId());
            assertEquals("gw1", flow.getSourceRef());
            assertEquals("task1", flow.getTargetRef());
            assertEquals("${amount > 1000}", flow.getConditionExpression());
        }
    }

    @Nested
    @DisplayName("createMultiplayerUserTask")
    class CreateMultiplayerUserTaskTest {

        @Test
        @DisplayName("should create sign user task with collection and element variable")
        void shouldCreateSignUserTaskWithCollection() {
            UserTask task = BpmnBuildUtils.createSignUserTask("multi1", "Multi Task", "userList", "user");

            assertEquals("multi1", task.getId());
            assertEquals("Multi Task", task.getName());
            assertEquals("${user}", task.getAssignee());
            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertNotNull(mi);
            assertFalse(mi.isSequential());
            assertEquals("${userList}", mi.getInputDataItem());
            assertEquals("user", mi.getElementVariable());
        }

        @Test
        @DisplayName("should create sign user task from element vo")
        void shouldCreateSignUserTaskFromElementVo() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("multi2")
                    .elementName("Multi Review")
                    .collectionName("reviewerList")
                    .build();

            UserTask task = BpmnBuildUtils.createSignUserTask(elementVo);

            assertEquals("multi2", task.getId());
            assertNotNull(task.getLoopCharacteristics());
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("createStartEvent with empty id")
        void createStartEventWithEmptyId() {
            StartEvent event = BpmnBuildUtils.createStartEvent("", "Start");

            assertEquals("", event.getId());
            assertEquals("Start", event.getName());
        }

        @Test
        @DisplayName("createEndEvent with null name")
        void createEndEventWithNullName() {
            EndEvent event = BpmnBuildUtils.createEndEvent("end1", null);

            assertEquals("end1", event.getId());
            assertNull(event.getName());
        }

        @Test
        @DisplayName("createSequenceFlow without condition expression produces empty expression")
        void createSequenceFlowWithoutCondition() {
            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow("flow1", "task1", "task2", null);

            assertEquals("flow1", flow.getId());
            assertEquals("${}", flow.getConditionExpression());
        }

        @Test
        @DisplayName("createUserTask should have task listener")
        void createUserTaskShouldHaveTaskListener() {
            UserTask task = BpmnBuildUtils.createUserTask("task1", "Approve", "assignee1");

            List<ActivitiListener> listeners = task.getTaskListeners();
            assertNotNull(listeners);
            assertFalse(listeners.isEmpty());
            assertNotNull(listeners.get(0).getImplementation());
        }
    }
}
