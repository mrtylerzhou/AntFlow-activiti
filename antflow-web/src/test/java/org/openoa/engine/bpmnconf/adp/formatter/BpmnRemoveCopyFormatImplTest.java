package org.openoa.engine.bpmnconf.adp.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BpmnRemoveCopyFormatImplTest extends MockBaseTest {

    private BpmnRemoveCopyFormatImpl removeCopyFormat;

    @BeforeEach
    void setUp() {
        removeCopyFormat = new BpmnRemoveCopyFormatImpl();
    }

    @Nested
    @DisplayName("trueSuppliers")
    class TrueSuppliersTest {

        @Test
        @DisplayName("should return true for copy node when not preview")
        void shouldReturnTrueForCopyNodeWhenNotPreview() {
            BpmnNodeVo node = new BpmnNodeVo();
            node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setEmplList(new ArrayList<>());
            property.setEmplIds(new ArrayList<>());
            node.setProperty(property);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            List<?> suppliers = removeCopyFormat.trueSuppliers(node, startConditions);

            boolean anyTrue = false;
            for (Object s : suppliers) {
                java.util.function.Supplier<Boolean> supplier = (java.util.function.Supplier<Boolean>) s;
                if (supplier.get()) anyTrue = true;
            }
            assertTrue(anyTrue);
        }

        @Test
        @DisplayName("should return false for copy node when preview")
        void shouldReturnFalseForCopyNodeWhenPreview() {
            BpmnNodeVo node = new BpmnNodeVo();
            node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setEmplList(new ArrayList<>());
            property.setEmplIds(new ArrayList<>());
            node.setProperty(property);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(true);

            List<?> suppliers = removeCopyFormat.trueSuppliers(node, startConditions);

            boolean anyTrue = false;
            for (Object s : suppliers) {
                java.util.function.Supplier<Boolean> supplier = (java.util.function.Supplier<Boolean>) s;
                if (supplier.get()) anyTrue = true;
            }
            assertFalse(anyTrue);
        }

        @Test
        @DisplayName("should return false for approver node")
        void shouldReturnFalseForApproverNode() {
            BpmnNodeVo node = new BpmnNodeVo();
            node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            List<?> suppliers = removeCopyFormat.trueSuppliers(node, startConditions);

            boolean anyTrue = false;
            for (Object s : suppliers) {
                java.util.function.Supplier<Boolean> supplier = (java.util.function.Supplier<Boolean>) s;
                if (supplier.get()) anyTrue = true;
            }
            assertFalse(anyTrue);
        }

        @Test
        @DisplayName("should populate empToForwardList when copy node is removed")
        void shouldPopulateEmpToForwardListWhenCopyRemoved() {
            List<String> emplIds = Arrays.asList("user1", "user2");
            BpmnNodeVo node = new BpmnNodeVo();
            node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setEmplList(new ArrayList<>());
            property.setEmplIds(emplIds);
            node.setProperty(property);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.trueSuppliers(node, startConditions);

            assertNotNull(startConditions.getEmpToForwardList());
            assertEquals(2, startConditions.getEmpToForwardList().size());
            assertTrue(startConditions.getEmpToForwardList().containsAll(emplIds));
        }

        @Test
        @DisplayName("should initialize empToForwardList when null")
        void shouldInitializeEmpToForwardListWhenNull() {
            BpmnNodeVo node = new BpmnNodeVo();
            node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setEmplList(new ArrayList<>());
            property.setEmplIds(Arrays.asList("user1"));
            node.setProperty(property);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);
            startConditions.setEmpToForwardList(null);

            removeCopyFormat.trueSuppliers(node, startConditions);

            assertNotNull(startConditions.getEmpToForwardList());
        }
    }

    @Nested
    @DisplayName("order")
    class OrderTest {

        @Test
        @DisplayName("should return order 2")
        void shouldReturnOrder2() {
            assertEquals(2, removeCopyFormat.order());
        }
    }
}
