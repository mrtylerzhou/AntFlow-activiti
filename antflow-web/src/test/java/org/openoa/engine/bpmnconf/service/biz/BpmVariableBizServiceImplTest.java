package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.openoa.MockBaseTest;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BpmVariableBizServiceImplTest extends MockBaseTest {

    @Spy
    @InjectMocks
    private BpmVariableBizServiceImpl bpmVariableBizService;

    @Mock
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    @Mock
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;

    @Mock
    private BpmVariableMapper bpmVariableMapper;

    @Mock
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @BeforeEach
    void setUp() {
        lenient().doReturn(bpmVariableMapper).when(bpmVariableBizService).getMapper();
    }

    @Nested
    @DisplayName("checkIsInProcess")
    class CheckIsInProcessTest {

        @Test
        @DisplayName("should return false when formCode is not null (bug: !isNullOrEmpty returns true for non-null formCode)")
        void shouldReturnFalseWhenFormCodeIsNotNull() {
            Boolean result = bpmVariableBizService.checkIsInProcess("FORM_001", 100, 200, "user001");

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when formCode is not empty (bug: !isNullOrEmpty returns true for non-empty formCode)")
        void shouldReturnFalseWhenFormCodeIsNotEmpty() {
            Boolean result = bpmVariableBizService.checkIsInProcess("FORM_CODE", 100, 200, "user001");

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when businessId is null and formCode is null")
        void shouldReturnFalseWhenBusinessIdIsNull() {
            Boolean result = bpmVariableBizService.checkIsInProcess(null, null, 200, "user001");

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when loginEmplId is null and formCode is null")
        void shouldReturnFalseWhenLoginEmplIdIsNull() {
            Boolean result = bpmVariableBizService.checkIsInProcess(null, 100, null, "user001");

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when loginUsername is null and formCode is null")
        void shouldReturnFalseWhenLoginUsernameIsNull() {
            Boolean result = bpmVariableBizService.checkIsInProcess(null, 100, 200, null);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when bpmVariable not found for process")
        void shouldReturnFalseWhenBpmVariableNotFound() {
            when(bpmVariableMapper.selectOne(any())).thenReturn(null);

            Boolean result = bpmVariableBizService.checkIsInProcess(null, 100, 200, "user001");

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getApproversByNodeId")
    class GetApproversByNodeIdTest {

        @Test
        @DisplayName("should delegate to bpmVariableMultiplayerService mapper")
        void shouldDelegateToBpmVariableMultiplayerServiceMapper() {
            String processNumber = "PROC_001";
            String nodeId = "node_1";

            BaseIdTranStruVo vo1 = BaseIdTranStruVo.builder().id("user1").name("Zhang San").build();
            BaseIdTranStruVo vo2 = BaseIdTranStruVo.builder().id("user2").name("Li Si").build();
            List<BaseIdTranStruVo> expected = Arrays.asList(vo1, vo2);

            when(bpmVariableMultiplayerService.getBaseMapper()).thenReturn(bpmVariableMultiplayerMapper);
            when(bpmVariableMultiplayerMapper.getAssigneeByNodeId(processNumber, nodeId)).thenReturn(expected);

            List<BaseIdTranStruVo> result = bpmVariableBizService.getApproversByNodeId(processNumber, nodeId);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("user1", result.get(0).getId());
            assertEquals("Zhang San", result.get(0).getName());
            assertEquals("user2", result.get(1).getId());
        }

        @Test
        @DisplayName("should return empty list when no approvers found")
        void shouldReturnEmptyListWhenNoApprovers() {
            String processNumber = "PROC_NONEXISTENT";
            String nodeId = "node_x";

            when(bpmVariableMultiplayerService.getBaseMapper()).thenReturn(bpmVariableMultiplayerMapper);
            when(bpmVariableMultiplayerMapper.getAssigneeByNodeId(processNumber, nodeId)).thenReturn(Collections.emptyList());

            List<BaseIdTranStruVo> result = bpmVariableBizService.getApproversByNodeId(processNumber, nodeId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getApproversByElementId")
    class GetApproversByElementIdTest {

        @Test
        @DisplayName("should delegate to bpmVariableMultiplayerService mapper")
        void shouldDelegateToBpmVariableMultiplayerServiceMapper() {
            String processNumber = "PROC_002";
            String elementId = "element_1";

            BaseIdTranStruVo vo = BaseIdTranStruVo.builder().id("user3").name("Wang Wu").build();
            List<BaseIdTranStruVo> expected = Collections.singletonList(vo);

            when(bpmVariableMultiplayerService.getBaseMapper()).thenReturn(bpmVariableMultiplayerMapper);
            when(bpmVariableMultiplayerMapper.getAssigneeByElementId(processNumber, elementId)).thenReturn(expected);

            List<BaseIdTranStruVo> result = bpmVariableBizService.getApproversByElementId(processNumber, elementId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("user3", result.get(0).getId());
            assertEquals("Wang Wu", result.get(0).getName());
        }

        @Test
        @DisplayName("should return empty list when no approvers found by element id")
        void shouldReturnEmptyListWhenNoApproversByElementId() {
            String processNumber = "PROC_NONEXISTENT";
            String elementId = "element_x";

            when(bpmVariableMultiplayerService.getBaseMapper()).thenReturn(bpmVariableMultiplayerMapper);
            when(bpmVariableMultiplayerMapper.getAssigneeByElementId(processNumber, elementId)).thenReturn(Collections.emptyList());

            List<BaseIdTranStruVo> result = bpmVariableBizService.getApproversByElementId(processNumber, elementId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("deleteByProcessNumber")
    class DeleteByProcessNumberTest {

        @Test
        @DisplayName("should delete variable and related records")
        void shouldDeleteVariableAndRelatedRecords() {
            String processNumber = "PROC_003";
            Long variableId = 42L;

            BpmVariable bpmVariable = new BpmVariable();
            bpmVariable.setId(variableId);
            bpmVariable.setProcessNum(processNumber);

            when(bpmVariableMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(bpmVariable);
            when(bpmVariableMultiplayerService.getBaseMapper()).thenReturn(bpmVariableMultiplayerMapper);

            try (MockedStatic<AFWrappers> afWrappersMockedStatic = mockStatic(AFWrappers.class)) {
                afWrappersMockedStatic.when(() -> AFWrappers.<BpmVariable>lambdaTenantQuery()).thenReturn(new LambdaQueryWrapper<>());
                afWrappersMockedStatic.when(() -> AFWrappers.<BpmVariableMultiplayer>lambdaTenantQuery()).thenReturn(new LambdaQueryWrapper<>());

                bpmVariableBizService.deleteByProcessNumber(processNumber);

                verify(bpmVariableMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
                verify(bpmVariableMapper, times(1)).deleteById(variableId);
                verify(bpmVariableMultiplayerMapper, times(1)).delete(any());
            }
        }
    }
}
