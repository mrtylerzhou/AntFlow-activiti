package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openoa.MockBaseTest;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BpmVerifyInfoBizServiceImplTest extends MockBaseTest {

    @Spy
    @InjectMocks
    private BpmVerifyInfoBizServiceImpl bpmVerifyInfoBizService;

    @Mock
    private BpmVerifyInfoMapper bpmVerifyInfoMapper;

    @Mock
    private BpmFlowrunEntrustService bpmFlowrunEntrustService;

    @BeforeEach
    void setUp() {
        lenient().doReturn(bpmVerifyInfoMapper).when(bpmVerifyInfoBizService).getMapper();
    }

    @Nested
    @DisplayName("getVerifyInfoList")
    class GetVerifyInfoListTest {

        @Test
        @DisplayName("should call verifyInfoList and return result")
        void shouldCallVerifyInfoListAndReturnResult() {
            String processCode = "PROC_001";

            BpmVerifyInfoVo vo1 = BpmVerifyInfoVo.builder()
                    .id("1")
                    .taskName("Manager Approval")
                    .verifyUserName("Zhang San")
                    .verifyStatus(2)
                    .build();
            BpmVerifyInfoVo vo2 = BpmVerifyInfoVo.builder()
                    .id("2")
                    .taskName("Director Approval")
                    .verifyUserName("Li Si")
                    .verifyStatus(1)
                    .build();
            List<BpmVerifyInfoVo> expectedList = Arrays.asList(vo1, vo2);

            when(bpmVerifyInfoMapper.getVerifyInfo(any(BpmVerifyInfoVo.class))).thenReturn(expectedList);

            List<BpmVerifyInfoVo> result = bpmVerifyInfoBizService.getVerifyInfoList(processCode);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Manager Approval", result.get(0).getTaskName());
            assertEquals("Director Approval", result.get(1).getTaskName());
            verify(bpmVerifyInfoMapper, times(1)).getVerifyInfo(any(BpmVerifyInfoVo.class));
        }

        @Test
        @DisplayName("should return empty list when no verify info found")
        void shouldReturnEmptyListWhenNoVerifyInfo() {
            String processCode = "PROC_NONEXISTENT";

            when(bpmVerifyInfoMapper.getVerifyInfo(any(BpmVerifyInfoVo.class))).thenReturn(Collections.emptyList());

            List<BpmVerifyInfoVo> result = bpmVerifyInfoBizService.getVerifyInfoList(processCode);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(bpmVerifyInfoMapper, times(1)).getVerifyInfo(any(BpmVerifyInfoVo.class));
        }
    }

    @Nested
    @DisplayName("addVerifyInfo")
    class AddVerifyInfoTest {

        @Test
        @DisplayName("should insert verify info when no entrust found")
        void shouldInsertVerifyInfoWhenNoEntrustFound() {
            BpmVerifyInfo verifyInfo = BpmVerifyInfo.builder()
                    .businessId("BIZ_001")
                    .verifyUserId("user001")
                    .runInfoId("run001")
                    .taskId("task001")
                    .verifyStatus(2)
                    .build();

            when(bpmFlowrunEntrustService.getMapper()).thenReturn(mock(org.openoa.engine.bpmnconf.mapper.BpmFlowrunEntrustMapper.class));
            when(bpmFlowrunEntrustService.getMapper().getEntrustByTaskId("user001", "run001", "task001")).thenReturn(null);

            bpmVerifyInfoBizService.addVerifyInfo(verifyInfo);

            assertNull(verifyInfo.getOriginalId());
            verify(bpmVerifyInfoMapper, times(1)).insert(verifyInfo);
        }

        @Test
        @DisplayName("should set originalId when entrust found")
        void shouldSetOriginalIdWhenEntrustFound() {
            BpmVerifyInfo verifyInfo = BpmVerifyInfo.builder()
                    .businessId("BIZ_002")
                    .verifyUserId("user002")
                    .runInfoId("run002")
                    .taskId("task002")
                    .verifyStatus(2)
                    .build();

            BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
            entrust.setOriginal("originalUser001");

            when(bpmFlowrunEntrustService.getMapper()).thenReturn(mock(org.openoa.engine.bpmnconf.mapper.BpmFlowrunEntrustMapper.class));
            when(bpmFlowrunEntrustService.getMapper().getEntrustByTaskId("user002", "run002", "task002")).thenReturn(entrust);

            bpmVerifyInfoBizService.addVerifyInfo(verifyInfo);

            assertEquals("originalUser001", verifyInfo.getOriginalId());
            verify(bpmVerifyInfoMapper, times(1)).insert(verifyInfo);
        }
    }

    @Nested
    @DisplayName("getSignUpNodeCollectionNameMap")
    class GetSignUpNodeCollectionNameMapTest {

        @Test
        @DisplayName("should return empty map when no sign-up variables found")
        void shouldReturnEmptyMapWhenNoSignUpVariables() {
            Long variableId = 999L;

            // mock bpmVariableService to return variable with no JSON config
            org.openoa.base.service.BpmVariableService mockVarService = mock(org.openoa.base.service.BpmVariableService.class);
            org.openoa.engine.bpmnconf.mapper.BpmVariableMapper mockVarMapper = mock(org.openoa.engine.bpmnconf.mapper.BpmVariableMapper.class);
            when(mockVarService.getBaseMapper()).thenReturn(mockVarMapper);
            when(mockVarMapper.selectById(variableId)).thenReturn(null);

            // use reflection to set the mocked service
            org.springframework.test.util.ReflectionTestUtils.setField(bpmVerifyInfoBizService, "bpmVariableService", mockVarService);

            Map<String, String> result = bpmVerifyInfoBizService.getSignUpNodeCollectionNameMap(variableId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
