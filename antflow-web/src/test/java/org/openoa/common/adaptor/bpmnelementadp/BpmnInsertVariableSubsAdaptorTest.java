package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.constant.enums.SupporttedDatabaseEnum;
import org.openoa.base.util.DataSourceUtils;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.entity.BpmVariableSingle;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.mapper.BpmVariableSingleMapper;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.common.service.BpmVariableSingleServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BpmnInsertVariableSubsAdaptorTest extends MockBaseTest {

    @Nested
    @DisplayName("BpmnInsertVariableSubsMultiplayerOrSignAdp")
    class MultiplayerOrSignAdpTest {

        @Spy
        @InjectMocks
        private BpmnInsertVariableSubsMultiplayerOrSignAdp adaptor;

        @Mock
        private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

        @Mock
        private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

        @Mock
        private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;

        @BeforeEach
        void setUp() {
            lenient().doReturn(bpmVariableMultiplayerMapper).when(bpmVariableMultiplayerService).getBaseMapper();
            doAnswer(invocation -> {
                BpmVariableMultiplayer entity = invocation.getArgument(0);
                entity.setId(100L);
                return 1;
            }).when(bpmVariableMultiplayerMapper).insert(any(BpmVariableMultiplayer.class));
        }

        private void runWithDbMock(SupporttedDatabaseEnum dbType, Runnable test) {
            try (MockedStatic<DataSourceUtils> dbMock = mockStatic(DataSourceUtils.class)) {
                dbMock.when(DataSourceUtils::getCurrentDb).thenReturn(dbType);
                test.run();
            }
        }

        @Test
        @DisplayName("should build BpmVariableMultiplayer with OR_SIGN signType")
        void shouldBuildMultiplayerWithOrSignSignType() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1"))
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                ArgumentCaptor<BpmVariableMultiplayer> captor = ArgumentCaptor.forClass(BpmVariableMultiplayer.class);
                verify(bpmVariableMultiplayerMapper).insert(captor.capture());
                assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), captor.getValue().getSignType());
            });
        }

        @Test
        @DisplayName("should insert BpmVariableMultiplayer via mapper")
        void shouldInsertMultiplayerViaMapper() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1"))
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                ArgumentCaptor<BpmVariableMultiplayer> captor = ArgumentCaptor.forClass(BpmVariableMultiplayer.class);
                verify(bpmVariableMultiplayerMapper).insert(captor.capture());
                BpmVariableMultiplayer inserted = captor.getValue();
                assertEquals(variableId, inserted.getVariableId());
                assertEquals("elem1", inserted.getElementId());
                assertEquals("node1", inserted.getNodeId());
                assertEquals("OrSign Task", inserted.getElementName());
                assertEquals("orSignList", inserted.getCollectionName());
            });
        }

        @Test
        @DisplayName("should map collection values to personnel entities with assignee names from assigneeMap")
        void shouldMapCollectionValuesToPersonnelWithAssigneeNames() {
            Map<String, String> assigneeMap = new HashMap<>();
            assigneeMap.put("user1", "Zhang San");
            assigneeMap.put("user2", "Li Si");

            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1", "user2"))
                    .assigneeMap(assigneeMap)
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                @SuppressWarnings("unchecked")
                ArgumentCaptor<List<BpmVariableMultiplayerPersonnel>> captor = ArgumentCaptor.forClass(List.class);
                verify(bpmVariableMultiplayerPersonnelService).saveBatch(captor.capture());
                List<BpmVariableMultiplayerPersonnel> personnelList = captor.getValue();
                assertEquals(2, personnelList.size());
                BpmVariableMultiplayerPersonnel p1 = personnelList.get(0);
                assertEquals("user1", p1.getAssignee());
                assertEquals("Zhang San", p1.getAssigneeName());
                assertEquals(100L, p1.getVariableMultiplayerId());
                assertEquals(0, p1.getUndertakeStatus());
                BpmVariableMultiplayerPersonnel p2 = personnelList.get(1);
                assertEquals("user2", p2.getAssignee());
                assertEquals("Li Si", p2.getAssigneeName());
                assertEquals(100L, p2.getVariableMultiplayerId());
                assertEquals(0, p2.getUndertakeStatus());
            });
        }

        @Test
        @DisplayName("should use empty string for assigneeName when assigneeMap is empty")
        void shouldUseEmptyStringForAssigneeNameWhenAssigneeMapIsEmpty() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1"))
                    .assigneeMap(Collections.emptyMap())
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                @SuppressWarnings("unchecked")
                ArgumentCaptor<List<BpmVariableMultiplayerPersonnel>> captor = ArgumentCaptor.forClass(List.class);
                verify(bpmVariableMultiplayerPersonnelService).saveBatch(captor.capture());
                assertEquals("", captor.getValue().get(0).getAssigneeName());
            });
        }

        @Test
        @DisplayName("should use empty string for assigneeName when assigneeMap is null")
        void shouldUseEmptyStringForAssigneeNameWhenAssigneeMapIsNull() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1"))
                    .assigneeMap(null)
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                @SuppressWarnings("unchecked")
                ArgumentCaptor<List<BpmVariableMultiplayerPersonnel>> captor = ArgumentCaptor.forClass(List.class);
                verify(bpmVariableMultiplayerPersonnelService).saveBatch(captor.capture());
                assertEquals("", captor.getValue().get(0).getAssigneeName());
            });
        }

        @Test
        @DisplayName("should use saveBatch when DB is not SQLSERVER")
        void shouldUseSaveBatchWhenDbIsNotSqlserver() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1"))
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                verify(bpmVariableMultiplayerPersonnelService).saveBatch(anyList());
                verify(bpmVariableMultiplayerPersonnelService, never()).save(any(BpmVariableMultiplayerPersonnel.class));
            });
        }

        @Test
        @DisplayName("should use save one-by-one when DB is SQLSERVER")
        void shouldUseSaveOneByOneWhenDbIsSqlserver() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("OrSign Task")
                    .collectionName("orSignList")
                    .collectionValue(Arrays.asList("user1", "user2"))
                    .build();
            Long variableId = 10L;

            runWithDbMock(SupporttedDatabaseEnum.SQLSERVER, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                verify(bpmVariableMultiplayerPersonnelService, times(2)).save(any(BpmVariableMultiplayerPersonnel.class));
                verify(bpmVariableMultiplayerPersonnelService, never()).saveBatch(anyList());
            });
        }
    }

    @Nested
    @DisplayName("BpmnInsertVariableSubsMultiplayerSignAdp")
    class MultiplayerSignAdpTest {

        @Spy
        @InjectMocks
        private BpmnInsertVariableSubsMultiplayerSignAdp adaptor;

        @Mock
        private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

        @Mock
        private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

        @Mock
        private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;

        @BeforeEach
        void setUp() {
            lenient().doReturn(bpmVariableMultiplayerMapper).when(bpmVariableMultiplayerService).getBaseMapper();
            doAnswer(invocation -> {
                BpmVariableMultiplayer entity = invocation.getArgument(0);
                entity.setId(200L);
                return 1;
            }).when(bpmVariableMultiplayerMapper).insert(any(BpmVariableMultiplayer.class));
        }

        private void runWithDbMock(SupporttedDatabaseEnum dbType, Runnable test) {
            try (MockedStatic<DataSourceUtils> dbMock = mockStatic(DataSourceUtils.class)) {
                dbMock.when(DataSourceUtils::getCurrentDb).thenReturn(dbType);
                test.run();
            }
        }

        @Test
        @DisplayName("should build BpmVariableMultiplayer with SIGN signType")
        void shouldBuildMultiplayerWithSignSignType() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("Sign Task")
                    .collectionName("signList")
                    .collectionValue(Arrays.asList("user1"))
                    .build();
            Long variableId = 20L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                ArgumentCaptor<BpmVariableMultiplayer> captor = ArgumentCaptor.forClass(BpmVariableMultiplayer.class);
                verify(bpmVariableMultiplayerMapper).insert(captor.capture());
                assertEquals(SignTypeEnum.SIGN_TYPE_SIGN.getCode(), captor.getValue().getSignType());
            });
        }

        @Test
        @DisplayName("should insert BpmVariableMultiplayer via mapper")
        void shouldInsertMultiplayerViaMapper() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("Sign Task")
                    .collectionName("signList")
                    .collectionValue(Arrays.asList("user1"))
                    .build();
            Long variableId = 20L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                ArgumentCaptor<BpmVariableMultiplayer> captor = ArgumentCaptor.forClass(BpmVariableMultiplayer.class);
                verify(bpmVariableMultiplayerMapper).insert(captor.capture());
                BpmVariableMultiplayer inserted = captor.getValue();
                assertEquals(variableId, inserted.getVariableId());
                assertEquals("elem1", inserted.getElementId());
                assertEquals("node1", inserted.getNodeId());
                assertEquals("Sign Task", inserted.getElementName());
                assertEquals("signList", inserted.getCollectionName());
            });
        }

        @Test
        @DisplayName("should map collection values to personnel entities with assignee names")
        void shouldMapCollectionValuesToPersonnelWithAssigneeNames() {
            Map<String, String> assigneeMap = new HashMap<>();
            assigneeMap.put("user1", "Zhang San");
            assigneeMap.put("user2", "Li Si");

            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("Sign Task")
                    .collectionName("signList")
                    .collectionValue(Arrays.asList("user1", "user2"))
                    .assigneeMap(assigneeMap)
                    .build();
            Long variableId = 20L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                @SuppressWarnings("unchecked")
                ArgumentCaptor<List<BpmVariableMultiplayerPersonnel>> captor = ArgumentCaptor.forClass(List.class);
                verify(bpmVariableMultiplayerPersonnelService).saveBatch(captor.capture());
                List<BpmVariableMultiplayerPersonnel> personnelList = captor.getValue();
                assertEquals(2, personnelList.size());
                BpmVariableMultiplayerPersonnel p1 = personnelList.get(0);
                assertEquals("user1", p1.getAssignee());
                assertEquals("Zhang San", p1.getAssigneeName());
                assertEquals(200L, p1.getVariableMultiplayerId());
                assertEquals(0, p1.getUndertakeStatus());
                BpmVariableMultiplayerPersonnel p2 = personnelList.get(1);
                assertEquals("user2", p2.getAssignee());
                assertEquals("Li Si", p2.getAssigneeName());
                assertEquals(200L, p2.getVariableMultiplayerId());
                assertEquals(0, p2.getUndertakeStatus());
            });
        }

        @Test
        @DisplayName("should use empty string for assigneeName when assigneeMap is empty")
        void shouldUseEmptyStringForAssigneeNameWhenAssigneeMapIsEmpty() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("Sign Task")
                    .collectionName("signList")
                    .collectionValue(Arrays.asList("user1"))
                    .assigneeMap(Collections.emptyMap())
                    .build();
            Long variableId = 20L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                @SuppressWarnings("unchecked")
                ArgumentCaptor<List<BpmVariableMultiplayerPersonnel>> captor = ArgumentCaptor.forClass(List.class);
                verify(bpmVariableMultiplayerPersonnelService).saveBatch(captor.capture());
                assertEquals("", captor.getValue().get(0).getAssigneeName());
            });
        }

        @Test
        @DisplayName("should use saveBatch when DB is not SQLSERVER")
        void shouldUseSaveBatchWhenDbIsNotSqlserver() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("elem1")
                    .nodeId("node1")
                    .elementName("Sign Task")
                    .collectionName("signList")
                    .collectionValue(Arrays.asList("user1"))
                    .build();
            Long variableId = 20L;

            runWithDbMock(SupporttedDatabaseEnum.MYSQL, () -> {
                adaptor.insertVariableSubs(elementVo, variableId);

                verify(bpmVariableMultiplayerPersonnelService).saveBatch(anyList());
                verify(bpmVariableMultiplayerPersonnelService, never()).save(any(BpmVariableMultiplayerPersonnel.class));
            });
        }
    }

    @Nested
    @DisplayName("BpmnInsertVariableSubsSingleAdp")
    class SingleAdpTest {

        @Spy
        @InjectMocks
        private BpmnInsertVariableSubsSingleAdp adaptor;

        @Mock
        private BpmVariableSingleServiceImpl bpmVariableSingleService;

        @Mock
        private BpmVariableSingleMapper bpmVariableSingleMapper;

        @BeforeEach
        void setUp() {
            lenient().doReturn(bpmVariableSingleMapper).when(bpmVariableSingleService).getBaseMapper();
        }

        @Test
        @DisplayName("should build BpmVariableSingle with correct fields")
        void shouldBuildSingleWithCorrectFields() {
            Map<String, String> assigneeMap = new HashMap<>();
            assigneeMap.put("user001", "Zhang San");

            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approval Task")
                    .nodeId("node1")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .assigneeMap(assigneeMap)
                    .build();
            Long variableId = 100L;

            adaptor.insertVariableSubs(elementVo, variableId);

            ArgumentCaptor<BpmVariableSingle> captor = ArgumentCaptor.forClass(BpmVariableSingle.class);
            verify(bpmVariableSingleMapper).insert(captor.capture());
            BpmVariableSingle inserted = captor.getValue();
            assertEquals(variableId, inserted.getVariableId());
            assertEquals("task1", inserted.getElementId());
            assertEquals("Approval Task", inserted.getElementName());
            assertEquals("node1", inserted.getNodeId());
            assertEquals("assignee1", inserted.getAssigneeParamName());
            assertEquals("user001", inserted.getAssignee());
            assertEquals("Zhang San", inserted.getAssigneeName());
        }

        @Test
        @DisplayName("should insert BpmVariableSingle via mapper")
        void shouldInsertSingleViaMapper() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approval Task")
                    .nodeId("node1")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .build();
            Long variableId = 100L;

            adaptor.insertVariableSubs(elementVo, variableId);

            verify(bpmVariableSingleMapper, times(1)).insert(any(BpmVariableSingle.class));
        }

        @Test
        @DisplayName("should use assignee name from assigneeMap when map is populated")
        void shouldUseAssigneeNameFromAssigneeMapWhenMapIsPopulated() {
            Map<String, String> assigneeMap = new HashMap<>();
            assigneeMap.put("user001", "Zhang San");

            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approval Task")
                    .nodeId("node1")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .assigneeMap(assigneeMap)
                    .build();
            Long variableId = 100L;

            adaptor.insertVariableSubs(elementVo, variableId);

            ArgumentCaptor<BpmVariableSingle> captor = ArgumentCaptor.forClass(BpmVariableSingle.class);
            verify(bpmVariableSingleMapper).insert(captor.capture());
            assertEquals("Zhang San", captor.getValue().getAssigneeName());
        }

        @Test
        @DisplayName("should use space character for assigneeName when assigneeMap is empty")
        void shouldUseSpaceCharacterForAssigneeNameWhenAssigneeMapIsEmpty() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approval Task")
                    .nodeId("node1")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .assigneeMap(Collections.emptyMap())
                    .build();
            Long variableId = 100L;

            adaptor.insertVariableSubs(elementVo, variableId);

            ArgumentCaptor<BpmVariableSingle> captor = ArgumentCaptor.forClass(BpmVariableSingle.class);
            verify(bpmVariableSingleMapper).insert(captor.capture());
            assertEquals(" ", captor.getValue().getAssigneeName());
        }

        @Test
        @DisplayName("should use space character for assigneeName when assigneeMap is null")
        void shouldUseSpaceCharacterForAssigneeNameWhenAssigneeMapIsNull() {
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approval Task")
                    .nodeId("node1")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .assigneeMap(null)
                    .build();
            Long variableId = 100L;

            adaptor.insertVariableSubs(elementVo, variableId);

            ArgumentCaptor<BpmVariableSingle> captor = ArgumentCaptor.forClass(BpmVariableSingle.class);
            verify(bpmVariableSingleMapper).insert(captor.capture());
            assertEquals(" ", captor.getValue().getAssigneeName());
        }

        @Test
        @DisplayName("should use space character for assigneeName when assigneeMap has no entry for the assignee")
        void shouldUseSpaceCharacterForAssigneeNameWhenNoEntryForAssignee() {
            Map<String, String> assigneeMap = new HashMap<>();
            assigneeMap.put("otherUser", "Other Name");

            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approval Task")
                    .nodeId("node1")
                    .assigneeParamName("assignee1")
                    .assigneeParamValue("user001")
                    .assigneeMap(assigneeMap)
                    .build();
            Long variableId = 100L;

            adaptor.insertVariableSubs(elementVo, variableId);

            ArgumentCaptor<BpmVariableSingle> captor = ArgumentCaptor.forClass(BpmVariableSingle.class);
            verify(bpmVariableSingleMapper).insert(captor.capture());
            assertEquals(" ", captor.getValue().getAssigneeName());
        }
    }
}
