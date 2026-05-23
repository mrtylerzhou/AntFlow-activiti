package org.openoa.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openoa.MockBaseTest;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BpmVariableMultiplayerServiceImplTest extends MockBaseTest {

    @Spy
    @InjectMocks
    private BpmVariableMultiplayerServiceImpl service;

    @Mock
    private BpmVariableMultiplayerMapper mapper;

    @BeforeEach
    void setUp() {
        doReturn(mapper).when(service).getBaseMapper();
    }

    @Nested
    @DisplayName("isMoreNode")
    class IsMoreNodeTest {

        @Test
        @DisplayName("Should return null when mapper returns empty list")
        void shouldReturnNullWhenMapperReturnsEmptyList() {
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(Collections.emptyList());

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when mapper returns null")
        void shouldReturnNullWhenMapperReturnsNull() {
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(null);

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNull(result);
        }

        @Test
        @DisplayName("Should filter records with underTakeStatus==0")
        void shouldFilterRecordsWithUnderTakeStatusZero() {
            BpmVariableMultiplayer player1 = BpmVariableMultiplayer.builder().id(1L).underTakeStatus(0).build();
            BpmVariableMultiplayer player2 = BpmVariableMultiplayer.builder().id(2L).underTakeStatus(1).build();
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(Arrays.asList(player1, player2));

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(0, result.get(0).getUnderTakeStatus());
            assertEquals(1L, result.get(0).getId());
        }

        @Test
        @DisplayName("Should exclude records with underTakeStatus!=0")
        void shouldExcludeRecordsWithNonZeroUnderTakeStatus() {
            BpmVariableMultiplayer player1 = BpmVariableMultiplayer.builder().id(1L).underTakeStatus(1).build();
            BpmVariableMultiplayer player2 = BpmVariableMultiplayer.builder().id(2L).underTakeStatus(2).build();
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(Arrays.asList(player1, player2));

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should exclude records with null underTakeStatus")
        void shouldExcludeRecordsWithNullUnderTakeStatus() {
            BpmVariableMultiplayer player1 = BpmVariableMultiplayer.builder().id(1L).underTakeStatus(null).build();
            BpmVariableMultiplayer player2 = BpmVariableMultiplayer.builder().id(2L).underTakeStatus(null).build();
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(Arrays.asList(player1, player2));

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return mixed results (only underTakeStatus==0)")
        void shouldReturnMixedResultsOnlyUnderTakeStatusZero() {
            BpmVariableMultiplayer player1 = BpmVariableMultiplayer.builder().id(1L).underTakeStatus(0).build();
            BpmVariableMultiplayer player2 = BpmVariableMultiplayer.builder().id(2L).underTakeStatus(1).build();
            BpmVariableMultiplayer player3 = BpmVariableMultiplayer.builder().id(3L).underTakeStatus(null).build();
            BpmVariableMultiplayer player4 = BpmVariableMultiplayer.builder().id(4L).underTakeStatus(0).build();
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(Arrays.asList(player1, player2, player3, player4));

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(4L, result.get(1).getId());
            assertTrue(result.stream().allMatch(a -> a.getUnderTakeStatus() == 0));
        }

        @Test
        @DisplayName("Should return all records when all have underTakeStatus==0")
        void shouldReturnAllRecordsWhenAllHaveUnderTakeStatusZero() {
            BpmVariableMultiplayer player1 = BpmVariableMultiplayer.builder().id(1L).underTakeStatus(0).build();
            BpmVariableMultiplayer player2 = BpmVariableMultiplayer.builder().id(2L).underTakeStatus(0).build();
            BpmVariableMultiplayer player3 = BpmVariableMultiplayer.builder().id(3L).underTakeStatus(0).build();
            when(mapper.isMoreNode("proc1", "elem1")).thenReturn(Arrays.asList(player1, player2, player3));

            List<BpmVariableMultiplayer> result = service.isMoreNode("proc1", "elem1");

            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.stream().allMatch(a -> a.getUnderTakeStatus() == 0));
        }
    }

    @Nested
    @DisplayName("queryVariableNameByElementId")
    class QueryVariableNameByElementIdTest {

        @Test
        @DisplayName("Should return variable name from mapper")
        void shouldReturnVariableNameFromMapper() {
            when(mapper.getVarNameByElementId("proc1", "elem1")).thenReturn("collectionVar");

            String result = service.queryVariableNameByElementId("proc1", "elem1");

            assertEquals("collectionVar", result);
        }

        @Test
        @DisplayName("Should return null when mapper returns null")
        void shouldReturnNullWhenMapperReturnsNull() {
            when(mapper.getVarNameByElementId("proc1", "elem1")).thenReturn(null);

            String result = service.queryVariableNameByElementId("proc1", "elem1");

            assertNull(result);
        }

        @Test
        @DisplayName("Should delegate to mapper.getVarNameByElementId with correct arguments")
        void shouldDelegateToMapperWithCorrectArguments() {
            when(mapper.getVarNameByElementId("procNum123", "elemId456")).thenReturn("varName");

            service.queryVariableNameByElementId("procNum123", "elemId456");

            verify(mapper, times(1)).getVarNameByElementId("procNum123", "elemId456");
        }
    }
}
