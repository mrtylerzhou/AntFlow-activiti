package org.openoa.engine.bpmnconf.service.biz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openoa.MockBaseTest;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.service.ConditionFilterService;

import static org.mockito.Mockito.*;

class BpmnStartFormatImplTest extends MockBaseTest {

    @InjectMocks
    private BpmnStartFormatImpl bpmnStartFormat;

    @Mock
    private ConditionFilterService conditionFilterService;

    @Nested
    @DisplayName("formatBpmnConf")
    class FormatBpmnConfTest {

        @Test
        @DisplayName("should delegate to conditionFilterService")
        void shouldDelegateToConditionFilterService() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            bpmnStartFormat.formatBpmnConf(confVo, startConditions);

            verify(conditionFilterService, times(1)).conditionfilterNode(confVo, startConditions);
        }

        @Test
        @DisplayName("should pass same confVo and startConditions objects")
        void shouldPassSameObjects() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            bpmnStartFormat.formatBpmnConf(confVo, startConditions);

            verify(conditionFilterService).conditionfilterNode(confVo, startConditions);
        }
    }
}
