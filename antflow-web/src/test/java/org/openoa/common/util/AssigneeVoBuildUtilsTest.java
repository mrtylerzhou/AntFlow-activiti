package org.openoa.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.DuplicationProcessStrategyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.MockBaseTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssigneeVoBuildUtilsTest extends MockBaseTest {

    @InjectMocks
    private AssigneeVoBuildUtils assigneeVoBuildUtils;

    @Mock
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Nested
    @DisplayName("buildVOs")
    class BuildVOsFromBaseIdTest {
        @Test
        @DisplayName("should build assignee VOs from BaseIdTranStruVo collection with suffix")
        void shouldBuildVOsWithSuffix() {
            List<BaseIdTranStruVo> assigneeInfos = new ArrayList<>();
            BaseIdTranStruVo vo1 = new BaseIdTranStruVo();
            vo1.setId("user1");
            vo1.setName("Zhang San");
            BaseIdTranStruVo vo2 = new BaseIdTranStruVo();
            vo2.setId("user2");
            vo2.setName("Li Si");
            assigneeInfos.add(vo1);
            assigneeInfos.add(vo2);

            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVOs(assigneeInfos, "Approve", true);

            assertEquals(2, result.size());
            assertEquals("user1", result.get(0).getAssignee());
            assertEquals("Zhang San", result.get(0).getAssigneeName());
            assertEquals("Approve_1", result.get(0).getElementName());
            assertEquals("user2", result.get(1).getAssignee());
            assertEquals("Approve_2", result.get(1).getElementName());
        }

        @Test
        @DisplayName("should build assignee VOs without suffix")
        void shouldBuildVOsWithoutSuffix() {
            List<BaseIdTranStruVo> assigneeInfos = new ArrayList<>();
            BaseIdTranStruVo vo1 = new BaseIdTranStruVo();
            vo1.setId("user1");
            vo1.setName("Zhang San");
            assigneeInfos.add(vo1);

            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVOs(assigneeInfos, "Review", false);

            assertEquals(1, result.size());
            assertEquals("Review", result.get(0).getElementName());
        }

        @Test
        @DisplayName("should use default name when nodeName is null")
        void shouldUseDefaultNameWhenNull() {
            List<BaseIdTranStruVo> assigneeInfos = new ArrayList<>();
            BaseIdTranStruVo vo1 = new BaseIdTranStruVo();
            vo1.setId("user1");
            vo1.setName("Zhang San");
            assigneeInfos.add(vo1);

            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVOs(assigneeInfos, null, false);

            assertEquals("层层审批", result.get(0).getElementName());
        }

        @Test
        @DisplayName("should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            List<BaseIdTranStruVo> assigneeInfos = new ArrayList<>();

            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVOs(assigneeInfos, "Approve", true);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("buildVos")
    class BuildVosTest {
        @Test
        @DisplayName("should build VOs from assignee id list")
        void shouldBuildVosFromIdList() {
            Map<String, String> employeeInfo = new HashMap<>();
            employeeInfo.put("user1", "Zhang San");
            employeeInfo.put("user2", "Li Si");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            List<String> assignees = Arrays.asList("user1", "user2");
            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVos(assignees, "Approve", true);

            assertEquals(2, result.size());
            assertEquals("Zhang San", result.get(0).getAssigneeName());
            assertEquals("Li Si", result.get(1).getAssigneeName());
        }

        @Test
        @DisplayName("should throw exception when employee info not found")
        void shouldThrowWhenEmployeeInfoNotFound() {
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(null);

            assertThrows(AFBizException.class, () ->
                    assigneeVoBuildUtils.buildVos(Arrays.asList("user1"), "Approve", true));
        }

        @Test
        @DisplayName("should skip assignee when name is empty")
        void shouldSkipAssigneeWhenNameEmpty() {
            Map<String, String> employeeInfo = new HashMap<>();
            employeeInfo.put("user1", "Zhang San");
            employeeInfo.put("user2", "");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            List<String> assignees = Arrays.asList("user1", "user2");
            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVos(assignees, "Approve", true);

            assertEquals(1, result.size());
            assertEquals("user1", result.get(0).getAssignee());
        }
    }

    @Nested
    @DisplayName("buildVo")
    class BuildVoTest {
        @Test
        @DisplayName("should build single assignee VO")
        void shouldBuildSingleAssigneeVo() {
            Map<String, String> employeeInfo = new HashMap<>();
            employeeInfo.put("user1", "Zhang San");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            BpmnNodeParamsAssigneeVo result = assigneeVoBuildUtils.buildVo("user1", "Review");

            assertEquals("user1", result.getAssignee());
            assertEquals("Zhang San", result.getAssigneeName());
            assertEquals("Review", result.getElementName());
        }

        @Test
        @DisplayName("should throw exception when employee info not found for single vo")
        void shouldThrowWhenNotFoundForSingleVo() {
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(null);

            assertThrows(AFBizException.class, () ->
                    assigneeVoBuildUtils.buildVo("user1", "Review"));
        }
    }

    @Nested
    @DisplayName("buildZeroVo")
    class BuildZeroVoTest {
        @Test
        @DisplayName("should build zero vo with special assignee")
        void shouldBuildZeroVo() {
            BpmnNodeParamsAssigneeVo result = assigneeVoBuildUtils.buildZeroVo();

            assertEquals(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId(), result.getAssignee());
            assertEquals(0, result.getIsDeduplication());
        }
    }

    @Nested
    @DisplayName("dealingWithMultiPlayerNodeDuplication")
    class DealingWithMultiPlayerNodeDuplicationTest {
        @Test
        @DisplayName("should include non-deduplication assignees")
        void shouldIncludeNonDeduplicationAssignees() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
            BpmnNodeParamsAssigneeVo vo1 = new BpmnNodeParamsAssigneeVo();
            vo1.setAssignee("user1");
            vo1.setAssigneeName("Zhang San");
            vo1.setIsDeduplication(0);
            assigneeList.add(vo1);
            params.setAssigneeList(assigneeList);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            Map<String, String> result = AssigneeVoBuildUtils.dealingWithMultiPlayerNodeDuplication(params, startConditions);

            assertEquals(1, result.size());
            assertTrue(result.containsKey("user1"));
        }

        @Test
        @DisplayName("should include deduplication assignees when strategy is SKIP")
        void shouldIncludeDeduplicationWhenSkip() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
            BpmnNodeParamsAssigneeVo vo1 = new BpmnNodeParamsAssigneeVo();
            vo1.setAssignee("user1");
            vo1.setAssigneeName("Zhang San");
            vo1.setIsDeduplication(1);
            assigneeList.add(vo1);
            params.setAssigneeList(assigneeList);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.SKIP.getCode());

            Map<String, String> result = AssigneeVoBuildUtils.dealingWithMultiPlayerNodeDuplication(params, startConditions);

            assertEquals(1, result.size());
            assertTrue(result.containsKey("user1"));
        }

        @Test
        @DisplayName("should exclude deduplication assignees when strategy is not SKIP")
        void shouldExcludeDeduplicationWhenNotSkip() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
            BpmnNodeParamsAssigneeVo vo1 = new BpmnNodeParamsAssigneeVo();
            vo1.setAssignee("user1");
            vo1.setAssigneeName("Zhang San");
            vo1.setIsDeduplication(1);
            assigneeList.add(vo1);
            params.setAssigneeList(assigneeList);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.REMOVE.getCode());

            Map<String, String> result = AssigneeVoBuildUtils.dealingWithMultiPlayerNodeDuplication(params, startConditions);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should handle mixed dedup and non-dedup assignees with non-SKIP strategy")
        void shouldHandleMixedAssigneesWithNonSkipStrategy() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
            BpmnNodeParamsAssigneeVo vo1 = new BpmnNodeParamsAssigneeVo();
            vo1.setAssignee("user1");
            vo1.setAssigneeName("Zhang San");
            vo1.setIsDeduplication(0);
            BpmnNodeParamsAssigneeVo vo2 = new BpmnNodeParamsAssigneeVo();
            vo2.setAssignee("user2");
            vo2.setAssigneeName("Li Si");
            vo2.setIsDeduplication(1);
            assigneeList.add(vo1);
            assigneeList.add(vo2);
            params.setAssigneeList(assigneeList);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            Map<String, String> result = AssigneeVoBuildUtils.dealingWithMultiPlayerNodeDuplication(params, startConditions);

            assertEquals(1, result.size());
            assertTrue(result.containsKey("user1"));
            assertFalse(result.containsKey("user2"));
        }

        @Test
        @DisplayName("should handle empty assignee list")
        void shouldHandleEmptyAssigneeList() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setAssigneeList(Collections.emptyList());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            Map<String, String> result = AssigneeVoBuildUtils.dealingWithMultiPlayerNodeDuplication(params, startConditions);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should handle null assignee list")
        void shouldHandleNullAssigneeList() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setAssigneeList(null);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(NullPointerException.class, () -> AssigneeVoBuildUtils.dealingWithMultiPlayerNodeDuplication(params, startConditions));
        }
    }

    @Nested
    @DisplayName("buildVOs edge cases")
    class BuildVOsFromBaseIdEdgeCasesTest {

        @Test
        @DisplayName("should handle single assignee with suffix")
        void shouldHandleSingleAssigneeWithSuffix() {
            List<BaseIdTranStruVo> assigneeInfos = new ArrayList<>();
            BaseIdTranStruVo vo1 = new BaseIdTranStruVo();
            vo1.setId("user1");
            vo1.setName("Zhang San");
            assigneeInfos.add(vo1);

            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVOs(assigneeInfos, "Approve", true);

            assertEquals(1, result.size());
            assertEquals("Approve_1", result.get(0).getElementName());
        }

        @Test
        @DisplayName("should handle assignee with null name")
        void shouldHandleAssigneeWithNullName() {
            List<BaseIdTranStruVo> assigneeInfos = new ArrayList<>();
            BaseIdTranStruVo vo1 = new BaseIdTranStruVo();
            vo1.setId("user1");
            vo1.setName(null);
            assigneeInfos.add(vo1);

            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVOs(assigneeInfos, "Approve", false);

            assertEquals(1, result.size());
            assertNull(result.get(0).getAssigneeName());
        }
    }

    @Nested
    @DisplayName("buildVos edge cases")
    class BuildVosEdgeCasesTest {

        @Test
        @DisplayName("should throw when empty assignee list and employee info is empty")
        void shouldThrowWhenEmptyAssigneeListAndEmployeeInfoEmpty() {
            Map<String, String> employeeInfo = new HashMap<>();
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            assertThrows(AFBizException.class, () -> assigneeVoBuildUtils.buildVos(Collections.emptyList(), "Approve", true));
        }

        @Test
        @DisplayName("should handle assignee not found in employee info")
        void shouldHandleAssigneeNotFoundInEmployeeInfo() {
            Map<String, String> employeeInfo = new HashMap<>();
            employeeInfo.put("user2", "Li Si");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            List<String> assignees = Arrays.asList("user1");
            List<BpmnNodeParamsAssigneeVo> result = assigneeVoBuildUtils.buildVos(assignees, "Approve", true);

            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("buildVo edge cases")
    class BuildVoEdgeCasesTest {

        @Test
        @DisplayName("should build VO with null nodeName")
        void shouldBuildVoWithNullNodeName() {
            Map<String, String> employeeInfo = new HashMap<>();
            employeeInfo.put("user1", "Zhang San");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            BpmnNodeParamsAssigneeVo result = assigneeVoBuildUtils.buildVo("user1", null);

            assertEquals("user1", result.getAssignee());
            assertEquals("Zhang San", result.getAssigneeName());
        }

        @Test
        @DisplayName("should build VO with empty nodeName")
        void shouldBuildVoWithEmptyNodeName() {
            Map<String, String> employeeInfo = new HashMap<>();
            employeeInfo.put("user1", "Zhang San");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(employeeInfo);

            BpmnNodeParamsAssigneeVo result = assigneeVoBuildUtils.buildVo("user1", "");

            assertEquals("user1", result.getAssignee());
        }
    }
}
