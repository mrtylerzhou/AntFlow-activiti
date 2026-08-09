package org.openoa.engine.bpmnconf.service.tagparser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagParserTest extends BaseTest {

    @Nested
    @DisplayName("PersonnelTagParser")
    class PersonnelTagParserTest {
        @Test
        @DisplayName("should throw when data is null")
        void shouldThrowWhenDataNull() {
            PersonnelTagParser parser = new PersonnelTagParser();

            assertThrows(AFBizException.class, () -> parser.parseTag(null));
        }

        @Test
        @DisplayName("should return matching adaptor")
        void shouldReturnMatchingAdaptor() {
            PersonnelTagParser parser = new PersonnelTagParser();
            AbstractBpmnPersonnelAdaptor mockAdaptor = mock(AbstractBpmnPersonnelAdaptor.class);
            when(mockAdaptor.isSupportBusinessObject(PersonnelEnum.USERAPPOINTED_PERSONNEL)).thenReturn(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class))
                        .thenReturn(Arrays.asList(mockAdaptor));

                AbstractBpmnPersonnelAdaptor result = parser.parseTag(PersonnelEnum.USERAPPOINTED_PERSONNEL);

                assertNotNull(result);
                assertEquals(mockAdaptor, result);
            }
        }

        @Test
        @DisplayName("should return null when no adaptor matches")
        void shouldReturnNullWhenNoMatch() {
            PersonnelTagParser parser = new PersonnelTagParser();
            AbstractBpmnPersonnelAdaptor mockAdaptor = mock(AbstractBpmnPersonnelAdaptor.class);
            when(mockAdaptor.isSupportBusinessObject(PersonnelEnum.USERAPPOINTED_PERSONNEL)).thenReturn(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class))
                        .thenReturn(Arrays.asList(mockAdaptor));

                AbstractBpmnPersonnelAdaptor result = parser.parseTag(PersonnelEnum.USERAPPOINTED_PERSONNEL);

                assertNull(result);
            }
        }

        @Test
        @DisplayName("should return null when no beans available")
        void shouldReturnNullWhenNoBeans() {
            PersonnelTagParser parser = new PersonnelTagParser();

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class))
                        .thenReturn(Collections.emptyList());

                AbstractBpmnPersonnelAdaptor result = parser.parseTag(PersonnelEnum.USERAPPOINTED_PERSONNEL);

                assertNull(result);
            }
        }
    }

    @Nested
    @DisplayName("BpmnNodeAdaptorTagParser")
    class BpmnNodeAdaptorTagParserTest {
        @Test
        @DisplayName("should throw when data is null")
        void shouldThrowWhenDataNull() {
            BpmnNodeAdaptorTagParser parser = new BpmnNodeAdaptorTagParser();

            assertThrows(AFBizException.class, () -> parser.parseTag(null));
        }

        @Test
        @DisplayName("should return matching node adaptor")
        void shouldReturnMatchingAdaptor() {
            BpmnNodeAdaptorTagParser parser = new BpmnNodeAdaptorTagParser();
            BpmnNodeAdaptor mockAdaptor = mock(BpmnNodeAdaptor.class);
            BpmnNodeAdpConfEnum confEnum = BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_PERSONNEL;
            when(mockAdaptor.isSupportBusinessObject(confEnum)).thenReturn(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(BpmnNodeAdaptor.class))
                        .thenReturn(Arrays.asList(mockAdaptor));

                BpmnNodeAdaptor result = parser.parseTag(confEnum);

                assertNotNull(result);
                assertEquals(mockAdaptor, result);
            }
        }

        @Test
        @DisplayName("should return null when no node adaptor matches")
        void shouldReturnNullWhenNoMatch() {
            BpmnNodeAdaptorTagParser parser = new BpmnNodeAdaptorTagParser();
            BpmnNodeAdaptor mockAdaptor = mock(BpmnNodeAdaptor.class);
            when(mockAdaptor.isSupportBusinessObject(any())).thenReturn(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(BpmnNodeAdaptor.class))
                        .thenReturn(Arrays.asList(mockAdaptor));

                BpmnNodeAdaptor result = parser.parseTag(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_ROLE);

                assertNull(result);
            }
        }
    }
}
