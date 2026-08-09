package org.openoa.common.util;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnBuildUtilsExtendedTest extends BaseTest {

    @Nested
    @DisplayName("createStartEvent")
    class CreateStartEventTest {

        @Test
        @DisplayName("should set id and name")
        void shouldSetIdAndName() {
            StartEvent event = BpmnBuildUtils.createStartEvent("start1", "Start Event");

            assertEquals("start1", event.getId());
            assertEquals("Start Event", event.getName());
        }

        @Test
        @DisplayName("should handle null name")
        void shouldHandleNullName() {
            StartEvent event = BpmnBuildUtils.createStartEvent("start1", null);

            assertEquals("start1", event.getId());
            assertNull(event.getName());
        }
    }

    @Nested
    @DisplayName("createEndEvent")
    class CreateEndEventTest {

        @Test
        @DisplayName("should set id and name")
        void shouldSetIdAndName() {
            EndEvent event = BpmnBuildUtils.createEndEvent("end1", "End Event");

            assertEquals("end1", event.getId());
            assertEquals("End Event", event.getName());
        }
    }

    @Nested
    @DisplayName("createExclusiveGateway")
    class CreateExclusiveGatewayTest {

        @Test
        @DisplayName("should set id")
        void shouldSetId() {
            ExclusiveGateway gateway = BpmnBuildUtils.createExclusiveGateway("gw1");

            assertEquals("gw1", gateway.getId());
        }
    }

    @Nested
    @DisplayName("createParallelGateWay")
    class CreateParallelGateWayTest {

        @Test
        @DisplayName("should set id")
        void shouldSetId() {
            ParallelGateway gateway = BpmnBuildUtils.createParallelGateWay("pgw1");

            assertEquals("pgw1", gateway.getId());
        }
    }

    @Nested
    @DisplayName("createUserTask")
    class CreateUserTaskTest {

        @Test
        @DisplayName("should set id, name, and assignee as ${paramName}")
        void shouldSetIdNameAndAssignee() {
            UserTask task = BpmnBuildUtils.createUserTask("task1", "Approve", "assignee1");

            assertEquals("task1", task.getId());
            assertEquals("Approve", task.getName());
            assertEquals("${assignee1}", task.getAssignee());
        }

        @Test
        @DisplayName("should attach create task listener with delegateExpression ${bpmnTaskListener}")
        void shouldAttachCreateTaskListener() {
            UserTask task = BpmnBuildUtils.createUserTask("task1", "Approve", "assignee1");

            java.util.List<ActivitiListener> listeners = task.getTaskListeners();
            assertNotNull(listeners);
            assertFalse(listeners.isEmpty());

            ActivitiListener listener = listeners.get(0);
            assertEquals("create", listener.getEvent());
            assertEquals("delegateExpression", listener.getImplementationType());
            assertEquals("${bpmnTaskListener}", listener.getImplementation());
        }
    }

    @Nested
    @DisplayName("createSignUserTask")
    class CreateSignUserTaskTest {

        @Test
        @DisplayName("should set multiInstanceLoopCharacteristics with sequential=false")
        void shouldSetSequentialFalse() {
            UserTask task = BpmnBuildUtils.createSignUserTask("sign1", "Sign Approve", "userList", "user");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertNotNull(mi);
            assertFalse(mi.isSequential());
        }

        @Test
        @DisplayName("should not have completion condition")
        void shouldNotHaveCompletionCondition() {
            UserTask task = BpmnBuildUtils.createSignUserTask("sign1", "Sign Approve", "userList", "user");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertNull(mi.getCompletionCondition());
        }

        @Test
        @DisplayName("should set input data item and element variable")
        void shouldSetInputDataItemAndElementVariable() {
            UserTask task = BpmnBuildUtils.createSignUserTask("sign1", "Sign Approve", "userList", "user");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertEquals("${userList}", mi.getInputDataItem());
            assertEquals("user", mi.getElementVariable());
        }
    }

    @Nested
    @DisplayName("createOrSignUserTask")
    class CreateOrSignUserTaskTest {

        @Test
        @DisplayName("should set completion condition ${nrOfCompletedInstances >= 1}")
        void shouldSetCompletionCondition() {
            UserTask task = BpmnBuildUtils.createOrSignUserTask("or1", "Or Sign", "userList", "user");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertNotNull(mi.getCompletionCondition());
            assertEquals("${nrOfCompletedInstances >= 1 }", mi.getCompletionCondition());
        }

        @Test
        @DisplayName("should set sequential=false")
        void shouldSetSequentialFalse() {
            UserTask task = BpmnBuildUtils.createOrSignUserTask("or1", "Or Sign", "userList", "user");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertFalse(mi.isSequential());
        }
    }

    @Nested
    @DisplayName("createLoopUserTask")
    class CreateLoopUserTaskTest {

        @Test
        @DisplayName("should set sequential=true")
        void shouldSetSequentialTrue() {
            UserTask task = BpmnBuildUtils.createLoopUserTask("loop1", "Loop Task", "loopAssignee", "endLoopMark");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertNotNull(mi);
            assertTrue(mi.isSequential());
        }

        @Test
        @DisplayName("should set loopCardinality to 10")
        void shouldSetLoopCardinality() {
            UserTask task = BpmnBuildUtils.createLoopUserTask("loop1", "Loop Task", "loopAssignee", "endLoopMark");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertEquals("10", mi.getLoopCardinality());
        }

        @Test
        @DisplayName("should set completion condition")
        void shouldSetCompletionCondition() {
            UserTask task = BpmnBuildUtils.createLoopUserTask("loop1", "Loop Task", "loopAssignee", "endLoopMark");

            MultiInstanceLoopCharacteristics mi = task.getLoopCharacteristics();
            assertEquals("${endLoopMark}", mi.getCompletionCondition());
        }
    }

    @Nested
    @DisplayName("createSequenceFlow")
    class CreateSequenceFlowTest {

        @Test
        @DisplayName("should set sourceRef and targetRef")
        void shouldSetSourceRefAndTargetRef() {
            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow("flow1", "start", "task1", "amount > 1000");

            assertEquals("flow1", flow.getId());
            assertEquals("start", flow.getSourceRef());
            assertEquals("task1", flow.getTargetRef());
        }

        @Test
        @DisplayName("should wrap condition expression in ${...}")
        void shouldWrapConditionExpression() {
            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow("flow1", "start", "task1", "amount > 1000");

            assertEquals("${amount > 1000}", flow.getConditionExpression());
        }

        @Test
        @DisplayName("with null condition should set conditionExpression to ${}")
        void shouldSetEmptyExpressionForNullCondition() {
            SequenceFlow flow = BpmnBuildUtils.createSequenceFlow("flow1", "start", "task1", null);

            assertEquals("${}", flow.getConditionExpression());
        }
    }
}
