package org.openoa.common.adaptor.bpmnelementadp;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BpmnAddFlowElementAdaptorTest extends MockBaseTest {

    private Process process;
    private Map<String, Object> startParamMap;
    private BpmnStartConditionsVo bpmnStartConditions;

    @BeforeEach
    void setUp() {
        process = new Process();
        startParamMap = new HashMap<>();
        bpmnStartConditions = new BpmnStartConditionsVo();
    }

    @Nested
    @DisplayName("BpmnAddFlowElementSingleAdp")
    class SingleAdpTest {

        private BpmnAddFlowElementSingleAdp adaptor;

        @BeforeEach
        void setUp() {
            adaptor = new BpmnAddFlowElementSingleAdp();
        }

        @Test
        @DisplayName("should add user task to process")
        void shouldAddUserTaskToProcess() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Single Approval")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(1, process.getFlowElements().size());
            assertTrue(process.getFlowElements().iterator().next() instanceof UserTask);
        }

        @Test
        @DisplayName("should put assigneeParamName and assigneeParamValue into startParamMap")
        void shouldPutAssigneeIntoStartParamMap() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Single Approval")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals("user001", startParamMap.get("assignee1"));
        }

        @Test
        @DisplayName("should create user task with correct id and name")
        void shouldCreateUserTaskWithCorrectIdAndName() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Single Approval")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            UserTask userTask = (UserTask) process.getFlowElements().iterator().next();
            assertEquals("task1", userTask.getId());
            assertEquals("Single Approval", userTask.getName());
            assertEquals("${assignee1}", userTask.getAssignee());
        }
    }

    @Nested
    @DisplayName("BpmnAddFlowElementMultSignAdp")
    class MultSignAdpTest {

        private BpmnAddFlowElementMultSignAdp adaptor;

        @BeforeEach
        void setUp() {
            adaptor = new BpmnAddFlowElementMultSignAdp();
        }

        @Test
        @DisplayName("should add sign user task to process")
        void shouldAddSignUserTaskToProcess() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task2")
                    .elementName("Sign Approval")
                    .collectionName("signUserList")
                    .collectionValue(Arrays.asList("user001", "user002"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(1, process.getFlowElements().size());
            assertTrue(process.getFlowElements().iterator().next() instanceof UserTask);
        }

        @Test
        @DisplayName("should put collectionName and collectionValue into startParamMap")
        void shouldPutCollectionIntoStartParamMap() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task2")
                    .elementName("Sign Approval")
                    .collectionName("signUserList")
                    .collectionValue(Arrays.asList("user001", "user002"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(Arrays.asList("user001", "user002"), startParamMap.get("signUserList"));
        }

        @Test
        @DisplayName("should create user task with non-sequential multi-instance")
        void shouldCreateNonSequentialMultiInstance() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task2")
                    .elementName("Sign Approval")
                    .collectionName("signUserList")
                    .collectionValue(Arrays.asList("user001", "user002"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            UserTask userTask = (UserTask) process.getFlowElements().iterator().next();
            MultiInstanceLoopCharacteristics loopChars = userTask.getLoopCharacteristics();
            assertNotNull(loopChars);
            assertFalse(loopChars.isSequential());
            assertNull(loopChars.getCompletionCondition());
        }
    }

    @Nested
    @DisplayName("BpmnAddFlowElementMultOrSignAdp")
    class MultOrSignAdpTest {

        private BpmnAddFlowElementMultOrSignAdp adaptor;

        @BeforeEach
        void setUp() {
            adaptor = new BpmnAddFlowElementMultOrSignAdp();
        }

        @Test
        @DisplayName("should add or-sign user task to process")
        void shouldAddOrSignUserTaskToProcess() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task3")
                    .elementName("Or-Sign Approval")
                    .collectionName("orSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002", "user003"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(1, process.getFlowElements().size());
            assertTrue(process.getFlowElements().iterator().next() instanceof UserTask);
        }

        @Test
        @DisplayName("should put collectionName and collectionValue into startParamMap")
        void shouldPutCollectionIntoStartParamMap() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task3")
                    .elementName("Or-Sign Approval")
                    .collectionName("orSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002", "user003"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(Arrays.asList("user001", "user002", "user003"), startParamMap.get("orSignUserList"));
        }

        @Test
        @DisplayName("should create user task with completion condition")
        void shouldCreateUserTaskWithCompletionCondition() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task3")
                    .elementName("Or-Sign Approval")
                    .collectionName("orSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002", "user003"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            UserTask userTask = (UserTask) process.getFlowElements().iterator().next();
            MultiInstanceLoopCharacteristics loopChars = userTask.getLoopCharacteristics();
            assertNotNull(loopChars);
            assertFalse(loopChars.isSequential());
            assertNotNull(loopChars.getCompletionCondition());
            assertTrue(loopChars.getCompletionCondition().contains("nrOfCompletedInstances"));
        }
    }

    @Nested
    @DisplayName("BpmnAddFlowElementLoopAdp")
    class LoopAdpTest {

        private BpmnAddFlowElementLoopAdp adaptor;

        @BeforeEach
        void setUp() {
            adaptor = new BpmnAddFlowElementLoopAdp();
        }

        @Test
        @DisplayName("should add loop user task to process")
        void shouldAddLoopUserTaskToProcess() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1418018332271")
                    .elementName("Loop Approval")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(1, process.getFlowElements().size());
            assertTrue(process.getFlowElements().iterator().next() instanceof UserTask);
        }

        @Test
        @DisplayName("should put endLoopMark into startParamMap with false value")
        void shouldPutEndLoopMarkIntoStartParamMap() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1418018332271")
                    .elementName("Loop Approval")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(false, startParamMap.get("endLoopMark1"));
        }

        @Test
        @DisplayName("should use ProcessNodeEnum to get elementCode from elementId")
        void shouldUseProcessNodeEnumToGetElementCode() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1418018332272")
                    .elementName("Loop Approval 2")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(false, startParamMap.get("endLoopMark2"));
        }

        @Test
        @DisplayName("should create user task with sequential multi-instance")
        void shouldCreateSequentialMultiInstance() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1418018332271")
                    .elementName("Loop Approval")
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            UserTask userTask = (UserTask) process.getFlowElements().iterator().next();
            MultiInstanceLoopCharacteristics loopChars = userTask.getLoopCharacteristics();
            assertNotNull(loopChars);
            assertTrue(loopChars.isSequential());
            assertEquals("task1418018332271", userTask.getId());
            assertEquals("Loop Approval", userTask.getName());
        }
    }

    @Nested
    @DisplayName("BpmnAddFlowElementSignUpSerialAdp")
    class SignUpSerialAdpTest {

        private BpmnAddFlowElementSignUpSerialAdp adaptor;

        @BeforeEach
        void setUp() {
            adaptor = new BpmnAddFlowElementSignUpSerialAdp();
        }

        @Test
        @DisplayName("should add loop user task to process")
        void shouldAddLoopUserTaskToProcess() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task5")
                    .elementName("Serial Sign Approval")
                    .collectionName("serialSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(1, process.getFlowElements().size());
            assertTrue(process.getFlowElements().iterator().next() instanceof UserTask);
        }

        @Test
        @DisplayName("should put collectionName and collectionValue into startParamMap")
        void shouldPutCollectionIntoStartParamMap() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task5")
                    .elementName("Serial Sign Approval")
                    .collectionName("serialSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(Arrays.asList("user001", "user002"), startParamMap.get("serialSignUserList"));
        }

        @Test
        @DisplayName("should put loopCardinality and collection size into startParamMap")
        void shouldPutLoopCardinalityAndCollectionSizeIntoStartParamMap() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task5")
                    .elementName("Serial Sign Approval")
                    .collectionName("serialSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002", "user003"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            assertEquals(3, startParamMap.get("task5size"));
        }

        @Test
        @DisplayName("should create user task with sequential multi-instance and loopCardinality")
        void shouldCreateSequentialMultiInstanceWithLoopCardinality() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task5")
                    .elementName("Serial Sign Approval")
                    .collectionName("serialSignUserList")
                    .collectionValue(Arrays.asList("user001", "user002"))
                    .build();

            adaptor.addFlowElement(elementVo, process, startParamMap, bpmnStartConditions);

            UserTask userTask = (UserTask) process.getFlowElements().iterator().next();
            MultiInstanceLoopCharacteristics loopChars = userTask.getLoopCharacteristics();
            assertNotNull(loopChars);
            assertTrue(loopChars.isSequential());
            assertEquals("${task5size}", loopChars.getLoopCardinality());
            assertEquals("${serialSignUserList}", loopChars.getInputDataItem());
            assertEquals("task5", userTask.getId());
            assertEquals("Serial Sign Approval", userTask.getName());
        }
    }
}
