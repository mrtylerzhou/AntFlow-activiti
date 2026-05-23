package org.openoa.engine.bpmnconf.adp.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

class BpmnStartFormatFactoryTest extends MockBaseTest {

    private final BpmnStartFormatFactory factory = new BpmnStartFormatFactory();

    @Nested
    @DisplayName("formatBpmnConf")
    class FormatBpmnConfTest {

        @Test
        @DisplayName("should call formatBpmnConf on all BpmnStartFormat beans")
        void shouldCallFormatBpmnConfOnAllBeans() {
            BpmnStartFormat format1 = mock(BpmnStartFormat.class);
            BpmnStartFormat format2 = mock(BpmnStartFormat.class);
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(BpmnStartFormat.class)).thenReturn(Arrays.asList(format1, format2));

                factory.formatBpmnConf(confVo, startConditions);

                verify(format1).formatBpmnConf(confVo, startConditions);
                verify(format2).formatBpmnConf(confVo, startConditions);
            }
        }

        @Test
        @DisplayName("should handle empty bean collection gracefully")
        void shouldHandleEmptyBeanCollection() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(BpmnStartFormat.class)).thenReturn(Collections.emptyList());

                factory.formatBpmnConf(confVo, startConditions);
            }
        }

        @Test
        @DisplayName("should handle single bean")
        void shouldHandleSingleBean() {
            BpmnStartFormat format = mock(BpmnStartFormat.class);
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(BpmnStartFormat.class)).thenReturn(Collections.singletonList(format));

                factory.formatBpmnConf(confVo, startConditions);

                verify(format, times(1)).formatBpmnConf(confVo, startConditions);
            }
        }

        @Test
        @DisplayName("should handle multiple beans and call all of them")
        void shouldHandleMultipleBeansAndCallAll() {
            BpmnStartFormat format1 = mock(BpmnStartFormat.class);
            BpmnStartFormat format2 = mock(BpmnStartFormat.class);
            BpmnStartFormat format3 = mock(BpmnStartFormat.class);
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(BpmnStartFormat.class)).thenReturn(Arrays.asList(format1, format2, format3));

                factory.formatBpmnConf(confVo, startConditions);

                verify(format1).formatBpmnConf(confVo, startConditions);
                verify(format2).formatBpmnConf(confVo, startConditions);
                verify(format3).formatBpmnConf(confVo, startConditions);
            }
        }
    }
}
