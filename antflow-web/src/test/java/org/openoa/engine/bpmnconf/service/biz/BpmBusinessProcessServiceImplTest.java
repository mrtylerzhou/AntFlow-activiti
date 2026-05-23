package org.openoa.engine.bpmnconf.service.biz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.MockBaseTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BpmBusinessProcessServiceImplTest extends MockBaseTest {

    @Mock
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Nested
    @DisplayName("isOutSideProcess")
    class IsOutSideProcessTest {
        @Test
        @DisplayName("should return true when process is outside process")
        void shouldReturnTrueWhenOutside() {
            BpmBusinessProcess process = new BpmBusinessProcess();
            process.setIsOutSideProcess(1);
            when(bpmBusinessProcessService.getOne(any(), anyBoolean())).thenReturn(process);

            BpmBusinessProcess result = bpmBusinessProcessService.getOne(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<BpmBusinessProcess>lambdaQuery()
                            .eq(BpmBusinessProcess::getProcInstId, "test_process"), false);

            assertNotNull(result);
            assertEquals(1, result.getIsOutSideProcess());
        }

        @Test
        @DisplayName("should return false when process is not outside process")
        void shouldReturnFalseWhenNotOutside() {
            BpmBusinessProcess process = new BpmBusinessProcess();
            process.setIsOutSideProcess(0);
            when(bpmBusinessProcessService.getOne(any(), anyBoolean())).thenReturn(process);

            BpmBusinessProcess result = bpmBusinessProcessService.getOne(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<BpmBusinessProcess>lambdaQuery()
                            .eq(BpmBusinessProcess::getProcInstId, "test_process"), false);

            assertNotNull(result);
            assertEquals(0, result.getIsOutSideProcess());
        }
    }
}
