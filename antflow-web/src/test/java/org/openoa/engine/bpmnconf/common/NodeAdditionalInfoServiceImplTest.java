package org.openoa.engine.bpmnconf.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;

import static org.junit.jupiter.api.Assertions.*;

class NodeAdditionalInfoServiceImplTest extends BaseTest {

    @Nested
    @DisplayName("getBpmnNodeAdpConfEnum - static method")
    class GetBpmnNodeAdpConfEnumTest {

        @Test
        @DisplayName("should return PERSONNEL conf when nodeType is APPROVER and nodeProperty is PERSONNEL")
        void shouldReturnPersonnelForApproverWithPersonnelProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_PERSONNEL, result);
        }

        @Test
        @DisplayName("should return ROLE conf when nodeType is APPROVER and nodeProperty is ROLE")
        void shouldReturnRoleForApproverWithRoleProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_ROLE.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_ROLE, result);
        }

        @Test
        @DisplayName("should return LOOP conf when nodeType is APPROVER and nodeProperty is LOOP")
        void shouldReturnLoopForApproverWithLoopProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_LOOP.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_LOOP, result);
        }

        @Test
        @DisplayName("should return CONDITIONS conf when nodeType is CONDITIONS")
        void shouldReturnConditionsForConditionsNodeType() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode())
                    .nodeProperty(0)
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_CONDITIONS, result);
        }

        @Test
        @DisplayName("should return COPY conf when nodeType is COPY")
        void shouldReturnCopyForCopyNodeType() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode())
                    .nodeProperty(0)
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_COPY, result);
        }

        @Test
        @DisplayName("should return DIRECT_LEADER conf when nodeType is APPROVER and nodeProperty is DIRECT_LEADER")
        void shouldReturnDirectLeaderForApproverWithDirectLeaderProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_DIRECT_LEADER.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_DIRECT_LEADER, result);
        }

        @Test
        @DisplayName("should return DEPARTMENT_LEADER conf when nodeType is APPROVER and nodeProperty is DEPARTMENT_LEADER")
        void shouldReturnDepartmentLeaderForApproverWithDepartmentLeaderProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_DEPARTMENT_LEADER.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_DEPARTMENT_LEADER, result);
        }

        @Test
        @DisplayName("should return HRBP conf when nodeType is APPROVER and nodeProperty is HRBP")
        void shouldReturnHrbpForApproverWithHrbpProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_HRBP.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_HRBP, result);
        }

        @Test
        @DisplayName("should return START_USER conf when nodeType is APPROVER and nodeProperty is START_USER")
        void shouldReturnStartUserForApproverWithStartUserProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_START_USER.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_START_USER, result);
        }

        @Test
        @DisplayName("should fall back to nodeProperty when nodeType is unknown")
        void shouldFallBackToNodePropertyWhenNodeTypeUnknown() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(999)
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_PERSONNEL, result);
        }

        @Test
        @DisplayName("should return OUT_SIDE_CONDITIONS conf when nodeType is OUT_SIDE_CONDITIONS")
        void shouldReturnOutSideConditionsForOutSideConditionsNodeType() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_OUT_SIDE_CONDITIONS.getCode())
                    .nodeProperty(0)
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_OUT_SIDE_CONDITIONS, result);
        }

        @Test
        @DisplayName("should return APPROVED_USERS conf when nodeType is APPROVER and nodeProperty is APPROVED_USERS")
        void shouldReturnApprovedUsersForApproverWithApprovedUsersProperty() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .nodeProperty(NodePropertyEnum.NODE_PROPERTY_APPROVED_USERS.getCode())
                    .build();

            BpmnNodeAdpConfEnum result = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(node);

            assertNotNull(result);
            assertEquals(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_APPROVED_USERS, result);
        }
    }
}
