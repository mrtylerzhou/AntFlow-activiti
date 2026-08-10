package org.openoa.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.openoa.MockBaseTest;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.mapper.BpmVariableMultiplayerPersonnelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class BpmVariableMultiplayerPersonnelServiceImplTest extends MockBaseTest {

    @Spy
    @InjectMocks
    private BpmVariableMultiplayerPersonnelServiceImpl service;

    @Mock
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @Mock
    private BpmVariableMultiplayerPersonnelMapper bpmVariableMultiplayerPersonnelMapper;

    @BeforeEach
    void setUp() {
        lenient().doReturn(bpmVariableMultiplayerPersonnelMapper).when(service).getBaseMapper();
    }

    @Nested
    @DisplayName("undertake")
    class UndertakeTest {

        @Test
        @DisplayName("should not call undertake when bpmVariableMultiplayerList is empty")
        void shouldNotCallUndertake_whenListIsEmpty() {
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Collections.emptyList());

            service.undertake("proc1", "elem1");

            verify(bpmVariableMultiplayerPersonnelMapper, never()).undertake(any());
        }

        @Test
        @DisplayName("should not call undertake when bpmVariableMultiplayerList is null")
        void shouldNotCallUndertake_whenListIsNull() {
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(null);

            service.undertake("proc1", "elem1");

            verify(bpmVariableMultiplayerPersonnelMapper, never()).undertake(any());
        }

        @Test
        @DisplayName("should not call undertake when signType is not 2")
        void shouldNotCallUndertake_whenSignTypeIsNot2() {
            BpmVariableMultiplayer entity = BpmVariableMultiplayer.builder()
                    .id(1L)
                    .signType(1)
                    .build();
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Arrays.asList(entity));

            service.undertake("proc1", "elem1");

            verify(bpmVariableMultiplayerPersonnelMapper, never()).undertake(any());
        }

        @Test
        @DisplayName("should call mapper undertake when signType is 2 and user is logged in")
        void shouldCallMapperUndertake_whenSignTypeIs2AndUserLoggedIn() {
            BpmVariableMultiplayer entity = BpmVariableMultiplayer.builder()
                    .id(1L)
                    .signType(2)
                    .build();
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Arrays.asList(entity));

            try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
                securityUtilsMockedStatic.when(SecurityUtils::getLogInEmpId).thenReturn("emp123");

                service.undertake("proc1", "elem1");

                verify(bpmVariableMultiplayerPersonnelMapper).undertake(any(BpmVariableMultiplayerPersonnel.class));
            }
        }

        @Test
        @DisplayName("should pass correct variableMultiplayerId to undertake")
        void shouldPassCorrectVariableMultiplayerId() {
            BpmVariableMultiplayer entity = BpmVariableMultiplayer.builder()
                    .id(99L)
                    .signType(2)
                    .build();
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Arrays.asList(entity));

            try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
                securityUtilsMockedStatic.when(SecurityUtils::getLogInEmpId).thenReturn("emp123");

                service.undertake("proc1", "elem1");

                verify(bpmVariableMultiplayerPersonnelMapper).undertake(
                        argThat(p -> p.getVariableMultiplayerId().equals(99L))
                );
            }
        }

        @Test
        @DisplayName("should throw AFBizException when signType is 2 and empId is empty")
        void shouldThrowAFBizException_whenSignTypeIs2AndEmpIdIsEmpty() {
            BpmVariableMultiplayer entity = BpmVariableMultiplayer.builder()
                    .id(1L)
                    .signType(2)
                    .build();
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Arrays.asList(entity));

            try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
                securityUtilsMockedStatic.when(SecurityUtils::getLogInEmpId).thenReturn("");

                assertThatThrownBy(() -> service.undertake("proc1", "elem1"))
                        .isInstanceOf(AFBizException.class)
                        .hasMessage("current user is not login");

                verify(bpmVariableMultiplayerPersonnelMapper, never()).undertake(any());
            }
        }

        @Test
        @DisplayName("should throw AFBizException when signType is 2 and empId is null")
        void shouldThrowAFBizException_whenSignTypeIs2AndEmpIdIsNull() {
            BpmVariableMultiplayer entity = BpmVariableMultiplayer.builder()
                    .id(1L)
                    .signType(2)
                    .build();
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Arrays.asList(entity));

            try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
                securityUtilsMockedStatic.when(SecurityUtils::getLogInEmpId).thenReturn(null);

                assertThatThrownBy(() -> service.undertake("proc1", "elem1"))
                        .isInstanceOf(AFBizException.class)
                        .hasMessage("current user is not login");

                verify(bpmVariableMultiplayerPersonnelMapper, never()).undertake(any());
            }
        }

        @Test
        @DisplayName("should use first element signType from the list")
        void shouldUseFirstElementSignType() {
            BpmVariableMultiplayer first = BpmVariableMultiplayer.builder()
                    .id(1L)
                    .signType(1)
                    .build();
            BpmVariableMultiplayer second = BpmVariableMultiplayer.builder()
                    .id(2L)
                    .signType(2)
                    .build();
            when(bpmVariableMultiplayerMapper.isMoreNode("proc1", "elem1"))
                    .thenReturn(Arrays.asList(first, second));

            service.undertake("proc1", "elem1");

            verify(bpmVariableMultiplayerPersonnelMapper, never()).undertake(any());
        }
    }
}
