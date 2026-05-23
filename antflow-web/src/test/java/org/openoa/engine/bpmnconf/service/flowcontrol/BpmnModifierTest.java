package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnModifierTest extends BaseTest {

    private static final String BPMN_XML_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "targetNamespace=\"http://www.activiti.org/test\">\n" +
            "  <process id=\"testProcess\" isExecutable=\"true\">\n" +
            "    <startEvent id=\"start\"/>\n" +
            "    <userTask id=\"task1\" name=\"审批\">\n" +
            "      <multiInstanceLoopCharacteristics isSequential=\"true\" " +
            "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
            "    </userTask>\n" +
            "    <userTask id=\"task2\" name=\"审批2\">\n" +
            "      <multiInstanceLoopCharacteristics isSequential=\"false\" " +
            "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
            "    </userTask>\n" +
            "    <endEvent id=\"end\"/>\n" +
            "  </process>\n" +
            "</definitions>";

    @Nested
    @DisplayName("changeSequential")
    class ChangeSequentialTest {

        @Test
        @DisplayName("should change isSequential from true to false")
        void shouldChangeSequentialTrueToFalse() throws Exception {
            String result = BpmnModifier.changeSequential(BPMN_XML_TEMPLATE, "task1", false);

            assertTrue(result.contains("id=\"task1\""));
            assertTrue(result.contains("isSequential=\"false\""));
        }

        @Test
        @DisplayName("should change isSequential from false to true")
        void shouldChangeSequentialFalseToTrue() throws Exception {
            String result = BpmnModifier.changeSequential(BPMN_XML_TEMPLATE, "task2", true);

            assertTrue(result.contains("id=\"task2\""));
            assertTrue(result.contains("isSequential=\"true\""));
        }

        @Test
        @DisplayName("should not modify other userTasks when changing one")
        void shouldNotModifyOtherTasks() throws Exception {
            String result = BpmnModifier.changeSequential(BPMN_XML_TEMPLATE, "task1", false);

            assertTrue(result.contains("id=\"task1\""));
            assertTrue(result.contains("id=\"task2\""));
            int sequentialFalseCount = countOccurrences(result, "isSequential=\"false\"");
            assertEquals(2, sequentialFalseCount);
        }

        @Test
        @DisplayName("should return unchanged XML when taskId not found")
        void shouldReturnUnchangedWhenTaskIdNotFound() throws Exception {
            String result = BpmnModifier.changeSequential(BPMN_XML_TEMPLATE, "nonExistentTask", true);

            assertTrue(result.contains("isSequential=\"true\""));
            assertTrue(result.contains("isSequential=\"false\""));
        }

        @Test
        @DisplayName("should handle userTask without multiInstanceLoopCharacteristics")
        void shouldHandleTaskWithoutMultiInstance() throws Exception {
            String xmlWithoutMulti =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                    "targetNamespace=\"http://www.activiti.org/test\">\n" +
                    "  <process id=\"testProcess\" isExecutable=\"true\">\n" +
                    "    <userTask id=\"singleTask\" name=\"单人审批\"/>\n" +
                    "  </process>\n" +
                    "</definitions>";

            String result = BpmnModifier.changeSequential(xmlWithoutMulti, "singleTask", true);

            assertNotNull(result);
            assertTrue(result.contains("id=\"singleTask\""));
        }

        @Test
        @DisplayName("should preserve XML structure after modification")
        void shouldPreserveXmlStructure() throws Exception {
            String result = BpmnModifier.changeSequential(BPMN_XML_TEMPLATE, "task1", false);

            assertTrue(result.contains("<startEvent"));
            assertTrue(result.contains("<endEvent"));
            assertTrue(result.contains("</definitions>"));
        }
    }

    @Nested
    @DisplayName("changeSequential edge cases")
    class ChangeSequentialEdgeCaseTest {

        @Test
        @DisplayName("should handle XML with multiple processes")
        void shouldHandleMultipleProcesses() throws Exception {
            String xmlWithMultipleProcesses =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                    "targetNamespace=\"http://www.activiti.org/test\">\n" +
                    "  <process id=\"process1\" isExecutable=\"true\">\n" +
                    "    <userTask id=\"task1\" name=\"Task1\">\n" +
                    "      <multiInstanceLoopCharacteristics isSequential=\"true\" " +
                    "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
                    "    </userTask>\n" +
                    "  </process>\n" +
                    "  <process id=\"process2\" isExecutable=\"true\">\n" +
                    "    <userTask id=\"task2\" name=\"Task2\">\n" +
                    "      <multiInstanceLoopCharacteristics isSequential=\"false\" " +
                    "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
                    "    </userTask>\n" +
                    "  </process>\n" +
                    "</definitions>";

            String result = BpmnModifier.changeSequential(xmlWithMultipleProcesses, "task2", true);

            assertTrue(result.contains("id=\"task2\""));
            assertTrue(result.contains("isSequential=\"true\""));
        }

        @Test
        @DisplayName("should handle same sequential value (idempotent)")
        void shouldHandleSameSequentialValue() throws Exception {
            String result = BpmnModifier.changeSequential(BPMN_XML_TEMPLATE, "task1", true);

            assertTrue(result.contains("id=\"task1\""));
            assertTrue(result.contains("isSequential=\"true\""));
        }

        @Test
        @DisplayName("should handle userTask with additional attributes")
        void shouldHandleTaskWithAdditionalAttributes() throws Exception {
            String xmlWithAttrs =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                    "xmlns:activiti=\"http://activiti.org/bpmn\" " +
                    "targetNamespace=\"http://www.activiti.org/test\">\n" +
                    "  <process id=\"testProcess\" isExecutable=\"true\">\n" +
                    "    <userTask id=\"task1\" name=\"审批\" activiti:assignee=\"${assignee}\">\n" +
                    "      <multiInstanceLoopCharacteristics isSequential=\"true\" " +
                    "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
                    "    </userTask>\n" +
                    "  </process>\n" +
                    "</definitions>";

            String result = BpmnModifier.changeSequential(xmlWithAttrs, "task1", false);

            assertTrue(result.contains("id=\"task1\""));
            assertTrue(result.contains("isSequential=\"false\""));
        }

        @Test
        @DisplayName("should handle empty process (no userTasks)")
        void shouldHandleEmptyProcess() throws Exception {
            String emptyXml =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                    "targetNamespace=\"http://www.activiti.org/test\">\n" +
                    "  <process id=\"emptyProcess\" isExecutable=\"true\">\n" +
                    "    <startEvent id=\"start\"/>\n" +
                    "    <endEvent id=\"end\"/>\n" +
                    "  </process>\n" +
                    "</definitions>";

            String result = BpmnModifier.changeSequential(emptyXml, "anyTask", true);

            assertNotNull(result);
            assertTrue(result.contains("<startEvent"));
            assertTrue(result.contains("<endEvent"));
        }

        @Test
        @DisplayName("should preserve sequence flow elements")
        void shouldPreserveSequenceFlows() throws Exception {
            String xmlWithFlows =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                    "targetNamespace=\"http://www.activiti.org/test\">\n" +
                    "  <process id=\"testProcess\" isExecutable=\"true\">\n" +
                    "    <startEvent id=\"start\"/>\n" +
                    "    <sequenceFlow id=\"flow1\" sourceRef=\"start\" targetRef=\"task1\"/>\n" +
                    "    <userTask id=\"task1\" name=\"审批\">\n" +
                    "      <multiInstanceLoopCharacteristics isSequential=\"true\" " +
                    "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
                    "    </userTask>\n" +
                    "    <sequenceFlow id=\"flow2\" sourceRef=\"task1\" targetRef=\"end\"/>\n" +
                    "    <endEvent id=\"end\"/>\n" +
                    "  </process>\n" +
                    "</definitions>";

            String result = BpmnModifier.changeSequential(xmlWithFlows, "task1", false);

            assertTrue(result.contains("sourceRef=\"start\""));
            assertTrue(result.contains("targetRef=\"task1\""));
            assertTrue(result.contains("sourceRef=\"task1\""));
            assertTrue(result.contains("targetRef=\"end\""));
        }

        @Test
        @DisplayName("should handle exclusive gateway in XML")
        void shouldHandleExclusiveGateway() throws Exception {
            String xmlWithGateway =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                    "targetNamespace=\"http://www.activiti.org/test\">\n" +
                    "  <process id=\"testProcess\" isExecutable=\"true\">\n" +
                    "    <startEvent id=\"start\"/>\n" +
                    "    <exclusiveGateway id=\"gw1\"/>\n" +
                    "    <userTask id=\"task1\" name=\"审批\">\n" +
                    "      <multiInstanceLoopCharacteristics isSequential=\"true\" " +
                    "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"/>\n" +
                    "    </userTask>\n" +
                    "    <endEvent id=\"end\"/>\n" +
                    "  </process>\n" +
                    "</definitions>";

            String result = BpmnModifier.changeSequential(xmlWithGateway, "task1", false);

            assertTrue(result.contains("exclusiveGateway"));
            assertTrue(result.contains("isSequential=\"false\""));
        }
    }

    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
