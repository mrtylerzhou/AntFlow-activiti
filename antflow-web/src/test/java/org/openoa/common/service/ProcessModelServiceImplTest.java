package org.openoa.common.service;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.bpmnelementadp.BpmnAddFlowElementAdaptor;
import org.openoa.common.constant.enus.ElementPropertyEnum;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProcessModelServiceImplTest extends MockBaseTest {

    @InjectMocks
    private ProcessModelServiceImpl processModelService;

    private BpmnConfCommonVo bpmnConfCommonVo;
    private BpmnStartConditionsVo bpmnStartConditions;
    private Map<String, Object> startParamMap;
    private BpmnModel model;

    @BeforeEach
    void setUp() {
        bpmnConfCommonVo = new BpmnConfCommonVo();
        bpmnConfCommonVo.setProcessNum("PROC_001");
        bpmnConfCommonVo.setProcessName("Test Process");
        bpmnConfCommonVo.setElementList(new ArrayList<>());

        bpmnStartConditions = new BpmnStartConditionsVo();
        startParamMap = new HashMap<>();
        model = new BpmnModel();
    }

    private BpmnConfCommonElementVo createElement(String id, String name, int type) {
        return BpmnConfCommonElementVo.builder()
                .elementId(id)
                .elementName(name)
                .elementType(type)
                .build();
    }

    private BpmnConfCommonElementVo createElement(String id, String name, int type, int property) {
        return BpmnConfCommonElementVo.builder()
                .elementId(id)
                .elementName(name)
                .elementType(type)
                .elementProperty(property)
                .build();
    }

    private BpmnConfCommonElementVo createSequenceFlowElement(String id, String name, String flowFrom, String flowTo) {
        return BpmnConfCommonElementVo.builder()
                .elementId(id)
                .elementName(name)
                .elementType(ElementTypeEnum.ELEMENT_TYPE_SEQUENCE_FLOW.getCode())
                .flowFrom(flowFrom)
                .flowTo(flowTo)
                .build();
    }

    @Nested
    @DisplayName("execute")
    class ExecuteTest {

        @Nested
        @DisplayName("start and end events")
        class StartAndEndEventsTest {

            @Test
            @DisplayName("should create process with start and end events")
            void shouldCreateProcessWithStartAndEndEvents() {
                bpmnConfCommonVo.setElementList(Arrays.asList(
                        createElement("start1", "Start", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()),
                        createElement("end1", "End", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode())
                ));

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model);

                    Process process = model.getProcessById(bpmnConfCommonVo.getProcessNum());
                    assertNotNull(process);
                    assertEquals("PROC_001", process.getId());
                    assertEquals("Test Process", process.getName());

                    Collection<FlowElement> elements = process.getFlowElements();
                    assertEquals(2, elements.size());

                    boolean hasStartEvent = elements.stream().anyMatch(e -> e instanceof StartEvent);
                    boolean hasEndEvent = elements.stream().anyMatch(e -> e instanceof EndEvent);
                    assertTrue(hasStartEvent);
                    assertTrue(hasEndEvent);
                }
            }
        }

        @Nested
        @DisplayName("user task via adaptor")
        class UserTaskAdaptorTest {

            @Test
            @DisplayName("should add user task via adaptor")
            void shouldAddUserTaskViaAdaptor() {
                bpmnConfCommonVo.setElementList(Arrays.asList(
                        createElement("start1", "Start", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()),
                        createElement("task1", "Approve", ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode(), ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode()),
                        createSequenceFlowElement("flow1", "to task", "start1", "task1"),
                        createElement("end1", "End", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode())
                ));

                BpmnAddFlowElementAdaptor mockAdaptor = mock(BpmnAddFlowElementAdaptor.class);
                Class<?> adaptorClass = ElementPropertyEnum.getClsByCode(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode());

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    mockedStatic.when(() -> SpringBeanUtils.getBean(adaptorClass))
                            .thenReturn(mockAdaptor);

                    processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model);

                    verify(mockAdaptor).addFlowElement(
                            argThat(vo -> vo instanceof BpmnConfCommonElementVo && "task1".equals(((BpmnConfCommonElementVo) vo).getElementId())),
                            any(Process.class),
                            eq(startParamMap),
                            eq(bpmnStartConditions)
                    );
                }
            }
        }

        @Nested
        @DisplayName("sequence flow")
        class SequenceFlowTest {

            @Test
            @DisplayName("should add sequence flow")
            void shouldAddSequenceFlow() {
                bpmnConfCommonVo.setElementList(Arrays.asList(
                        createElement("start1", "Start", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()),
                        createElement("end1", "End", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode()),
                        createSequenceFlowElement("flow1", "to end", "start1", "end1")
                ));

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model);

                    Process process = model.getProcessById(bpmnConfCommonVo.getProcessNum());
                    assertNotNull(process);

                    boolean hasSequenceFlow = process.getFlowElements().stream()
                            .anyMatch(e -> e instanceof SequenceFlow);
                    assertTrue(hasSequenceFlow);

                    SequenceFlow flow = (SequenceFlow) process.getFlowElements().stream()
                            .filter(e -> e instanceof SequenceFlow)
                            .findFirst()
                            .orElse(null);
                    assertNotNull(flow);
                    assertEquals("flow1", flow.getId());
                    assertEquals("start1", flow.getSourceRef());
                    assertEquals("end1", flow.getTargetRef());
                }
            }
        }

        @Nested
        @DisplayName("parallel gateway")
        class ParallelGatewayTest {

            @Test
            @DisplayName("should add parallel gateway")
            void shouldAddParallelGateway() {
                bpmnConfCommonVo.setElementList(Arrays.asList(
                        createElement("start1", "Start", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()),
                        createElement("fork1", "Fork", ElementTypeEnum.ELEMENT_TYPE_PARALLEL_GATEWAY.getCode()),
                        createElement("end1", "End", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode())
                ));

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model);

                    Process process = model.getProcessById(bpmnConfCommonVo.getProcessNum());
                    assertNotNull(process);

                    boolean hasParallelGateway = process.getFlowElements().stream()
                            .anyMatch(e -> e instanceof ParallelGateway);
                    assertTrue(hasParallelGateway);

                    ParallelGateway gateway = (ParallelGateway) process.getFlowElements().stream()
                            .filter(e -> e instanceof ParallelGateway)
                            .findFirst()
                            .orElse(null);
                    assertNotNull(gateway);
                    assertEquals("fork1", gateway.getId());
                }
            }
        }

        @Nested
        @DisplayName("graphical info generation")
        class GraphicalInfoTest {

            @Test
            @DisplayName("should generate graphical info via SimpleBpmnFlowDesigner")
            void shouldGenerateGraphicalInfo() {
                bpmnConfCommonVo.setElementList(Arrays.asList(
                        createElement("start1", "Start", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()),
                        createElement("end1", "End", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode()),
                        createSequenceFlowElement("flow1", "to end", "start1", "end1")
                ));

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model);

                    Map<String, GraphicInfo> locationMap = model.getLocationMap();
                    assertFalse(locationMap.isEmpty());
                }
            }
        }

        @Nested
        @DisplayName("empty element list")
        class EmptyElementListTest {

            @Test
            @DisplayName("should handle empty element list without throwing")
            void shouldHandleEmptyElementList() {
                bpmnConfCommonVo.setElementList(Collections.emptyList());

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    assertDoesNotThrow(() ->
                            processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model));

                    Process process = model.getProcessById(bpmnConfCommonVo.getProcessNum());
                    assertNotNull(process);
                    assertTrue(process.getFlowElements().isEmpty());
                }
            }
        }

        @Nested
        @DisplayName("null adaptor bean")
        class NullAdaptorBeanTest {

            @Test
            @DisplayName("should skip user task when adaptor bean is null")
            void shouldSkipUserTaskWhenAdaptorBeanIsNull() {
                bpmnConfCommonVo.setElementList(Arrays.asList(
                        createElement("start1", "Start", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()),
                        createElement("task1", "Approve", ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode(), ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode()),
                        createElement("end1", "End", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode())
                ));

                Class<?> adaptorClass = ElementPropertyEnum.getClsByCode(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode());

                try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                    mockedStatic.when(() -> SpringBeanUtils.getBean(adaptorClass))
                            .thenReturn(null);

                    assertDoesNotThrow(() ->
                            processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model));

                    Process process = model.getProcessById(bpmnConfCommonVo.getProcessNum());
                    assertNotNull(process);

                    boolean hasUserTask = process.getFlowElements().stream()
                            .anyMatch(e -> e instanceof UserTask);
                    assertFalse(hasUserTask);
                }
            }
        }
    }
}
