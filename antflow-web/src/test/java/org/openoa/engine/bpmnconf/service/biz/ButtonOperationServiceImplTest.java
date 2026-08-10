package org.openoa.engine.bpmnconf.service.biz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.service.ProcessorFactory;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.MockBaseTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ButtonOperationServiceImplTest extends MockBaseTest {

    @InjectMocks
    private ButtonOperationServiceImpl buttonOperationService;

    @Mock
    private IAdaptorFactory adaptorFactory;

    @Mock
    private ProcessOperationAdaptor processOperationAdaptor;

    @Nested
    @DisplayName("buttonsOperationTransactional")
    class ButtonsOperationTransactionalTest {
        @Test
        @DisplayName("should execute process button and post processors successfully")
        void shouldExecuteSuccessfully() {
            BusinessDataVo vo = new BusinessDataVo();
            when(adaptorFactory.getProcessOperation(vo)).thenReturn(processOperationAdaptor);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class);
                 MockedStatic<ProcessorFactory> processorFactoryMockedStatic = mockStatic(ProcessorFactory.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getOrderedBeans(any())).thenReturn(Collections.emptyList());
                processorFactoryMockedStatic.when(() -> ProcessorFactory.executePostProcessors(vo)).then(invocation -> null);

                BusinessDataVo result = buttonOperationService.buttonsOperationTransactional(vo);

                assertNotNull(result);
                verify(adaptorFactory).getProcessOperation(vo);
                verify(processOperationAdaptor).doProcessButton(vo);
            }
        }

        @Test
        @DisplayName("should re-throw exception when process operation fails")
        void shouldRethrowExceptionOnFailure() {
            BusinessDataVo vo = new BusinessDataVo();
            when(adaptorFactory.getProcessOperation(vo)).thenReturn(processOperationAdaptor);
            doThrow(new RuntimeException("Process error")).when(processOperationAdaptor).doProcessButton(vo);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getOrderedBeans(any())).thenReturn(Collections.emptyList());

                assertThrows(RuntimeException.class, () ->
                        buttonOperationService.buttonsOperationTransactional(vo));

                verify(processOperationAdaptor).doProcessButton(vo);
            }
        }
    }
}
