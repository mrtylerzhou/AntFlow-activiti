package org.openoa.engine.bpmnconf.adp.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.formatter.BpmnRemoveFormat;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

class BpmnRemoveConfFormatFactoryTest extends MockBaseTest {

    private final BpmnRemoveConfFormatFactory factory = new BpmnRemoveConfFormatFactory();

    @Nested
    @DisplayName("removeBpmnConf")
    class RemoveBpmnConfTest {

        @Test
        @DisplayName("should call removeBpmnConf on all BpmnRemoveFormat beans in order")
        void shouldCallRemoveBpmnConfOnAllBeansInOrder() {
            BpmnRemoveFormat format1 = mock(BpmnRemoveFormat.class);
            BpmnRemoveFormat format2 = mock(BpmnRemoveFormat.class);
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getOrderedBeans(BpmnRemoveFormat.class)).thenReturn(Arrays.asList(format1, format2));

                factory.removeBpmnConf(confVo, startConditions);

                InOrder inOrder = inOrder(format1, format2);
                inOrder.verify(format1).removeBpmnConf(confVo, startConditions);
                inOrder.verify(format2).removeBpmnConf(confVo, startConditions);
            }
        }

        @Test
        @DisplayName("should handle empty bean list gracefully")
        void shouldHandleEmptyBeanList() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getOrderedBeans(BpmnRemoveFormat.class)).thenReturn(Collections.emptyList());

                factory.removeBpmnConf(confVo, startConditions);
            }
        }

        @Test
        @DisplayName("should handle single bean")
        void shouldHandleSingleBean() {
            BpmnRemoveFormat format = mock(BpmnRemoveFormat.class);
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getOrderedBeans(BpmnRemoveFormat.class)).thenReturn(Collections.singletonList(format));

                factory.removeBpmnConf(confVo, startConditions);

                verify(format, times(1)).removeBpmnConf(confVo, startConditions);
            }
        }

        @Test
        @DisplayName("should handle multiple beans in correct order")
        void shouldHandleMultipleBeansInCorrectOrder() {
            BpmnRemoveFormat format1 = mock(BpmnRemoveFormat.class);
            BpmnRemoveFormat format2 = mock(BpmnRemoveFormat.class);
            BpmnRemoveFormat format3 = mock(BpmnRemoveFormat.class);
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getOrderedBeans(BpmnRemoveFormat.class)).thenReturn(Arrays.asList(format1, format2, format3));

                factory.removeBpmnConf(confVo, startConditions);

                InOrder inOrder = inOrder(format1, format2, format3);
                inOrder.verify(format1).removeBpmnConf(confVo, startConditions);
                inOrder.verify(format2).removeBpmnConf(confVo, startConditions);
                inOrder.verify(format3).removeBpmnConf(confVo, startConditions);
            }
        }
    }
}
