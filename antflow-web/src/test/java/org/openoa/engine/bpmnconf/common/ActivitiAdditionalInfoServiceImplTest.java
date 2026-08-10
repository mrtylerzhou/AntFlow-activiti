package org.openoa.engine.bpmnconf.common;

import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.dto.ParallelPair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivitiAdditionalInfoServiceImplTest extends MockBaseTest {

    private ActivitiAdditionalInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ActivitiAdditionalInfoServiceImpl();
    }

    @Nested
    @DisplayName("isParallelFork")
    class IsParallelForkTest {
        @Test
        @DisplayName("should return true for parallel gateway with 1 incoming and multiple outgoing")
        void shouldReturnTrueForParallelFork() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            lenient().when(act.getIncomingTransitions()).thenReturn(Collections.singletonList(mock(PvmTransition.class)));
            lenient().when(act.getOutgoingTransitions()).thenReturn(
                    Arrays.asList(mock(PvmTransition.class), mock(PvmTransition.class)));

            boolean result = invokeIsParallelFork(act);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false for non-parallel gateway")
        void shouldReturnFalseForNonParallelGateway() {
            ActivityImpl act = mock(ActivityImpl.class);
            when(act.getProperty("type")).thenReturn("userTask");

            boolean result = invokeIsParallelFork(act);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for parallel gateway with multiple incoming (join)")
        void shouldReturnFalseForParallelJoin() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            lenient().when(act.getIncomingTransitions()).thenReturn(
                    Arrays.asList(mock(PvmTransition.class), mock(PvmTransition.class)));
            lenient().when(act.getOutgoingTransitions()).thenReturn(Collections.singletonList(mock(PvmTransition.class)));

            boolean result = invokeIsParallelFork(act);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for parallel gateway with single outgoing")
        void shouldReturnFalseForSingleOutgoing() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            lenient().when(act.getIncomingTransitions()).thenReturn(Collections.singletonList(mock(PvmTransition.class)));
            lenient().when(act.getOutgoingTransitions()).thenReturn(Collections.singletonList(mock(PvmTransition.class)));

            boolean result = invokeIsParallelFork(act);

            assertFalse(result);
        }

        private boolean invokeIsParallelFork(ActivityImpl act) {
            try {
                Method method = ActivitiAdditionalInfoServiceImpl.class.getDeclaredMethod("isParallelFork", ActivityImpl.class);
                method.setAccessible(true);
                return (boolean) method.invoke(service, act);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("isParallelJoin")
    class IsParallelJoinTest {
        @Test
        @DisplayName("should return true for parallel gateway with multiple incoming and 1 outgoing")
        void shouldReturnTrueForParallelJoin() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            lenient().when(act.getIncomingTransitions()).thenReturn(
                    Arrays.asList(mock(PvmTransition.class), mock(PvmTransition.class)));
            lenient().when(act.getOutgoingTransitions()).thenReturn(Collections.singletonList(mock(PvmTransition.class)));

            boolean result = invokeIsParallelJoin(act);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false for non-parallel gateway")
        void shouldReturnFalseForNonParallelGateway() {
            ActivityImpl act = mock(ActivityImpl.class);
            when(act.getProperty("type")).thenReturn("userTask");

            boolean result = invokeIsParallelJoin(act);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for parallel gateway with 1 incoming (fork)")
        void shouldReturnFalseForParallelFork() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            lenient().when(act.getIncomingTransitions()).thenReturn(Collections.singletonList(mock(PvmTransition.class)));
            lenient().when(act.getOutgoingTransitions()).thenReturn(
                    Arrays.asList(mock(PvmTransition.class), mock(PvmTransition.class)));

            boolean result = invokeIsParallelJoin(act);

            assertFalse(result);
        }

        private boolean invokeIsParallelJoin(ActivityImpl act) {
            try {
                Method method = ActivitiAdditionalInfoServiceImpl.class.getDeclaredMethod("isParallelJoin", ActivityImpl.class);
                method.setAccessible(true);
                return (boolean) method.invoke(service, act);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("canReach")
    class CanReachTest {
        @Test
        @DisplayName("should return true when source equals target")
        void shouldReturnTrueWhenSourceEqualsTarget() {
            ActivityImpl node = mock(ActivityImpl.class);

            assertTrue(service.canReach(node, node));
        }

        @Test
        @DisplayName("should return true when target is directly reachable")
        void shouldReturnTrueWhenDirectlyReachable() {
            ActivityImpl source = mock(ActivityImpl.class);
            ActivityImpl target = mock(ActivityImpl.class);
            PvmTransition transition = mock(PvmTransition.class);

            when(source.getOutgoingTransitions()).thenReturn(Collections.singletonList(transition));
            when(transition.getDestination()).thenReturn(target);

            assertTrue(service.canReach(source, target));
        }

        @Test
        @DisplayName("should return true when target is reachable through intermediate node")
        void shouldReturnTrueWhenReachableThroughIntermediate() {
            ActivityImpl source = mock(ActivityImpl.class);
            ActivityImpl intermediate = mock(ActivityImpl.class);
            ActivityImpl target = mock(ActivityImpl.class);

            PvmTransition t1 = mock(PvmTransition.class);
            PvmTransition t2 = mock(PvmTransition.class);

            when(source.getOutgoingTransitions()).thenReturn(Collections.singletonList(t1));
            when(t1.getDestination()).thenReturn(intermediate);
            when(intermediate.getOutgoingTransitions()).thenReturn(Collections.singletonList(t2));
            when(t2.getDestination()).thenReturn(target);

            assertTrue(service.canReach(source, target));
        }

        @Test
        @DisplayName("should return false when target is not reachable")
        void shouldReturnFalseWhenNotReachable() {
            ActivityImpl source = mock(ActivityImpl.class);
            ActivityImpl deadEnd = mock(ActivityImpl.class);
            ActivityImpl target = mock(ActivityImpl.class);

            PvmTransition t1 = mock(PvmTransition.class);

            when(source.getOutgoingTransitions()).thenReturn(Collections.singletonList(t1));
            when(t1.getDestination()).thenReturn(deadEnd);
            when(deadEnd.getOutgoingTransitions()).thenReturn(Collections.emptyList());

            assertFalse(service.canReach(source, target));
        }

        @Test
        @DisplayName("should handle cycles without infinite loop")
        void shouldHandleCyclesWithoutInfiniteLoop() {
            ActivityImpl a = mock(ActivityImpl.class);
            ActivityImpl b = mock(ActivityImpl.class);
            ActivityImpl target = mock(ActivityImpl.class);

            PvmTransition ab = mock(PvmTransition.class);
            PvmTransition ba = mock(PvmTransition.class);
            PvmTransition bt = mock(PvmTransition.class);

            when(a.getOutgoingTransitions()).thenReturn(Collections.singletonList(ab));
            when(ab.getDestination()).thenReturn(b);
            when(b.getOutgoingTransitions()).thenReturn(Arrays.asList(ba, bt));
            when(ba.getDestination()).thenReturn(a);
            when(bt.getDestination()).thenReturn(target);

            assertTrue(service.canReach(a, target));
        }

        @Test
        @DisplayName("should return false when source has no outgoing transitions")
        void shouldReturnFalseWhenNoOutgoingTransitions() {
            ActivityImpl source = mock(ActivityImpl.class);
            ActivityImpl target = mock(ActivityImpl.class);

            when(source.getOutgoingTransitions()).thenReturn(Collections.emptyList());

            assertFalse(service.canReach(source, target));
        }
    }

    @Nested
    @DisplayName("findNearestParallelGateway")
    class FindNearestParallelGatewayTest {
        @Test
        @DisplayName("should return null when no parallel gateways exist")
        void shouldReturnNullWhenNoParallelGateways() {
            ActivityImpl node = mock(ActivityImpl.class);
            when(node.getProperty("type")).thenReturn("userTask");

            ProcessDefinitionEntity def = mock(ProcessDefinitionEntity.class);
            when(def.getActivities()).thenReturn(Collections.singletonList(node));

            ParallelPair result = service.findNearestParallelGateway(node, def);

            assertNull(result);
        }

        @Test
        @DisplayName("should return null when node is not reachable from any fork")
        void shouldReturnNullWhenNodeNotReachableFromFork() {
            ActivityImpl fork = createParallelFork();
            ActivityImpl node = createUserTask();
            ActivityImpl join = createParallelJoin();

            PvmTransition forkToOther = mock(PvmTransition.class);
            ActivityImpl otherNode = mock(ActivityImpl.class);
            when(forkToOther.getDestination()).thenReturn(otherNode);
            PvmTransition forkToOther2 = mock(PvmTransition.class);
            when(forkToOther2.getDestination()).thenReturn(mock(ActivityImpl.class));
            when(fork.getOutgoingTransitions()).thenReturn(Arrays.asList(forkToOther, forkToOther2));
            when(otherNode.getOutgoingTransitions()).thenReturn(Collections.emptyList());

            ProcessDefinitionEntity def = mock(ProcessDefinitionEntity.class);
            when(def.getActivities()).thenReturn(Arrays.asList(fork, node, join));

            ParallelPair result = service.findNearestParallelGateway(node, def);

            assertNull(result);
        }

        @Test
        @DisplayName("should verify canReach integration for parallel gateway scenario")
        void shouldVerifyCanReachIntegrationForParallelGatewayScenario() {
            ActivityImpl fork = createParallelFork();
            ActivityImpl node = createUserTask();
            ActivityImpl join = createParallelJoin();

            PvmTransition forkToNode = mock(PvmTransition.class);
            when(forkToNode.getDestination()).thenReturn(node);
            when(fork.getOutgoingTransitions()).thenReturn(Arrays.asList(forkToNode, mock(PvmTransition.class)));

            PvmTransition nodeToJoin = mock(PvmTransition.class);
            when(nodeToJoin.getDestination()).thenReturn(join);
            when(node.getOutgoingTransitions()).thenReturn(Collections.singletonList(nodeToJoin));

            when(join.getOutgoingTransitions()).thenReturn(Collections.emptyList());

            assertTrue(service.canReach(fork, node), "fork should reach node");
            assertTrue(service.canReach(node, join), "node should reach join");
            assertFalse(service.canReach(node, fork), "node should not reach fork (no backward edge)");
        }

        private ActivityImpl createParallelFork() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            List<PvmTransition> incomingList = Collections.singletonList(mock(PvmTransition.class));
            List<PvmTransition> outgoingList = Arrays.asList(mock(PvmTransition.class), mock(PvmTransition.class));
            lenient().when(act.getIncomingTransitions()).thenReturn(incomingList);
            lenient().when(act.getOutgoingTransitions()).thenReturn(outgoingList);
            return act;
        }

        private ActivityImpl createParallelJoin() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("parallelGateway");
            List<PvmTransition> incomingList = Arrays.asList(mock(PvmTransition.class), mock(PvmTransition.class));
            List<PvmTransition> outgoingList = Collections.singletonList(mock(PvmTransition.class));
            lenient().when(act.getIncomingTransitions()).thenReturn(incomingList);
            lenient().when(act.getOutgoingTransitions()).thenReturn(outgoingList);
            return act;
        }

        private ActivityImpl createUserTask() {
            ActivityImpl act = mock(ActivityImpl.class);
            lenient().when(act.getProperty("type")).thenReturn("userTask");
            lenient().when(act.getIncomingTransitions()).thenReturn(Collections.emptyList());
            lenient().when(act.getOutgoingTransitions()).thenReturn(Collections.emptyList());
            return act;
        }
    }
}
