package org.openoa.common.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BpmVariableEntityTest extends BaseTest {

    @Nested
    @DisplayName("BpmVariableSingle")
    class BpmVariableSingleTest {

        @Test
        @DisplayName("Builder creates object with correct field values")
        void builderCreatesObjectWithCorrectFieldValues() {
            Date now = new Date();
            BpmVariableSingle single = BpmVariableSingle.builder()
                    .id(1L)
                    .variableId(100L)
                    .elementId("element1")
                    .nodeId("node1")
                    .elementName("Element One")
                    .assigneeParamName("assignee1")
                    .assignee("user001")
                    .assigneeName("Zhang San")
                    .remark("test remark")
                    .isDel(0)
                    .tenantId("tenant1")
                    .createUser("creator")
                    .createTime(now)
                    .updateUser("updater")
                    .updateTime(now)
                    .build();

            assertEquals(1L, single.getId());
            assertEquals(100L, single.getVariableId());
            assertEquals("element1", single.getElementId());
            assertEquals("node1", single.getNodeId());
            assertEquals("Element One", single.getElementName());
            assertEquals("assignee1", single.getAssigneeParamName());
            assertEquals("user001", single.getAssignee());
            assertEquals("Zhang San", single.getAssigneeName());
            assertEquals("test remark", single.getRemark());
            assertEquals(0, single.getIsDel());
            assertEquals("tenant1", single.getTenantId());
            assertEquals("creator", single.getCreateUser());
            assertEquals(now, single.getCreateTime());
            assertEquals("updater", single.getUpdateUser());
            assertEquals(now, single.getUpdateTime());
        }

        @Test
        @DisplayName("Getter/setter work correctly")
        void getterSetterWorkCorrectly() {
            BpmVariableSingle single = new BpmVariableSingle();
            Date now = new Date();

            single.setId(2L);
            single.setVariableId(200L);
            single.setElementId("element2");
            single.setNodeId("node2");
            single.setElementName("Element Two");
            single.setAssigneeParamName("assignee2");
            single.setAssignee("user002");
            single.setAssigneeName("Li Si");
            single.setRemark("another remark");
            single.setIsDel(1);
            single.setTenantId("tenant2");
            single.setCreateUser("creator2");
            single.setCreateTime(now);
            single.setUpdateUser("updater2");
            single.setUpdateTime(now);

            assertEquals(2L, single.getId());
            assertEquals(200L, single.getVariableId());
            assertEquals("element2", single.getElementId());
            assertEquals("node2", single.getNodeId());
            assertEquals("Element Two", single.getElementName());
            assertEquals("assignee2", single.getAssigneeParamName());
            assertEquals("user002", single.getAssignee());
            assertEquals("Li Si", single.getAssigneeName());
            assertEquals("another remark", single.getRemark());
            assertEquals(1, single.getIsDel());
            assertEquals("tenant2", single.getTenantId());
            assertEquals("creator2", single.getCreateUser());
            assertEquals(now, single.getCreateTime());
            assertEquals("updater2", single.getUpdateUser());
            assertEquals(now, single.getUpdateTime());
        }

        @Test
        @DisplayName("Default constructor creates object with null fields")
        void defaultConstructorCreatesObjectWithNullFields() {
            BpmVariableSingle single = new BpmVariableSingle();

            assertNull(single.getId());
            assertNull(single.getVariableId());
            assertNull(single.getElementId());
            assertNull(single.getNodeId());
            assertNull(single.getElementName());
            assertNull(single.getAssigneeParamName());
            assertNull(single.getAssignee());
            assertNull(single.getAssigneeName());
            assertNull(single.getRemark());
            assertNull(single.getIsDel());
            assertNull(single.getTenantId());
            assertNull(single.getCreateUser());
            assertNull(single.getCreateTime());
            assertNull(single.getUpdateUser());
            assertNull(single.getUpdateTime());
        }
    }

    @Nested
    @DisplayName("BpmVariableMultiplayer")
    class BpmVariableMultiplayerTest {

        @Test
        @DisplayName("Builder creates object with correct field values")
        void builderCreatesObjectWithCorrectFieldValues() {
            Date now = new Date();
            BpmVariableMultiplayer multiplayer = BpmVariableMultiplayer.builder()
                    .id(1L)
                    .variableId(100L)
                    .elementId("element1")
                    .nodeId("node1")
                    .elementName("Element One")
                    .collectionName("userList")
                    .signType(1)
                    .remark("test remark")
                    .isDel(0)
                    .tenantId("tenant1")
                    .createUser("creator")
                    .createTime(now)
                    .updateUser("updater")
                    .updateTime(now)
                    .underTakeStatus(0)
                    .build();

            assertEquals(1L, multiplayer.getId());
            assertEquals(100L, multiplayer.getVariableId());
            assertEquals("element1", multiplayer.getElementId());
            assertEquals("node1", multiplayer.getNodeId());
            assertEquals("Element One", multiplayer.getElementName());
            assertEquals("userList", multiplayer.getCollectionName());
            assertEquals(1, multiplayer.getSignType());
            assertEquals("test remark", multiplayer.getRemark());
            assertEquals(0, multiplayer.getIsDel());
            assertEquals("tenant1", multiplayer.getTenantId());
            assertEquals("creator", multiplayer.getCreateUser());
            assertEquals(now, multiplayer.getCreateTime());
            assertEquals("updater", multiplayer.getUpdateUser());
            assertEquals(now, multiplayer.getUpdateTime());
            assertEquals(0, multiplayer.getUnderTakeStatus());
        }

        @Test
        @DisplayName("Getter/setter work correctly")
        void getterSetterWorkCorrectly() {
            BpmVariableMultiplayer multiplayer = new BpmVariableMultiplayer();
            Date now = new Date();

            multiplayer.setId(2L);
            multiplayer.setVariableId(200L);
            multiplayer.setElementId("element2");
            multiplayer.setNodeId("node2");
            multiplayer.setElementName("Element Two");
            multiplayer.setCollectionName("approverList");
            multiplayer.setSignType(2);
            multiplayer.setRemark("another remark");
            multiplayer.setIsDel(1);
            multiplayer.setTenantId("tenant2");
            multiplayer.setCreateUser("creator2");
            multiplayer.setCreateTime(now);
            multiplayer.setUpdateUser("updater2");
            multiplayer.setUpdateTime(now);
            multiplayer.setUnderTakeStatus(1);

            assertEquals(2L, multiplayer.getId());
            assertEquals(200L, multiplayer.getVariableId());
            assertEquals("element2", multiplayer.getElementId());
            assertEquals("node2", multiplayer.getNodeId());
            assertEquals("Element Two", multiplayer.getElementName());
            assertEquals("approverList", multiplayer.getCollectionName());
            assertEquals(2, multiplayer.getSignType());
            assertEquals("another remark", multiplayer.getRemark());
            assertEquals(1, multiplayer.getIsDel());
            assertEquals("tenant2", multiplayer.getTenantId());
            assertEquals("creator2", multiplayer.getCreateUser());
            assertEquals(now, multiplayer.getCreateTime());
            assertEquals("updater2", multiplayer.getUpdateUser());
            assertEquals(now, multiplayer.getUpdateTime());
            assertEquals(1, multiplayer.getUnderTakeStatus());
        }

        @Test
        @DisplayName("Default constructor creates object with null fields")
        void defaultConstructorCreatesObjectWithNullFields() {
            BpmVariableMultiplayer multiplayer = new BpmVariableMultiplayer();

            assertNull(multiplayer.getId());
            assertNull(multiplayer.getVariableId());
            assertNull(multiplayer.getElementId());
            assertNull(multiplayer.getNodeId());
            assertNull(multiplayer.getElementName());
            assertNull(multiplayer.getCollectionName());
            assertNull(multiplayer.getSignType());
            assertNull(multiplayer.getRemark());
            assertNull(multiplayer.getIsDel());
            assertNull(multiplayer.getTenantId());
            assertNull(multiplayer.getCreateUser());
            assertNull(multiplayer.getCreateTime());
            assertNull(multiplayer.getUpdateUser());
            assertNull(multiplayer.getUpdateTime());
            assertNull(multiplayer.getUnderTakeStatus());
        }
    }

    @Nested
    @DisplayName("BpmVariableMultiplayerPersonnel")
    class BpmVariableMultiplayerPersonnelTest {

        @Test
        @DisplayName("Builder creates object with correct field values")
        void builderCreatesObjectWithCorrectFieldValues() {
            Date now = new Date();
            BpmVariableMultiplayerPersonnel personnel = BpmVariableMultiplayerPersonnel.builder()
                    .id(1L)
                    .variableMultiplayerId(100L)
                    .assignee("user001")
                    .assigneeName("Zhang San")
                    .undertakeStatus(0)
                    .remark("test remark")
                    .isDel(0)
                    .tenantId("tenant1")
                    .createUser("creator")
                    .createTime(now)
                    .updateUser("updater")
                    .updateTime(now)
                    .build();

            assertEquals(1L, personnel.getId());
            assertEquals(100L, personnel.getVariableMultiplayerId());
            assertEquals("user001", personnel.getAssignee());
            assertEquals("Zhang San", personnel.getAssigneeName());
            assertEquals(0, personnel.getUndertakeStatus());
            assertEquals("test remark", personnel.getRemark());
            assertEquals(0, personnel.getIsDel());
            assertEquals("tenant1", personnel.getTenantId());
            assertEquals("creator", personnel.getCreateUser());
            assertEquals(now, personnel.getCreateTime());
            assertEquals("updater", personnel.getUpdateUser());
            assertEquals(now, personnel.getUpdateTime());
        }

        @Test
        @DisplayName("Getter/setter work correctly")
        void getterSetterWorkCorrectly() {
            BpmVariableMultiplayerPersonnel personnel = new BpmVariableMultiplayerPersonnel();
            Date now = new Date();

            personnel.setId(2L);
            personnel.setVariableMultiplayerId(200L);
            personnel.setAssignee("user002");
            personnel.setAssigneeName("Li Si");
            personnel.setUndertakeStatus(1);
            personnel.setRemark("another remark");
            personnel.setIsDel(1);
            personnel.setTenantId("tenant2");
            personnel.setCreateUser("creator2");
            personnel.setCreateTime(now);
            personnel.setUpdateUser("updater2");
            personnel.setUpdateTime(now);

            assertEquals(2L, personnel.getId());
            assertEquals(200L, personnel.getVariableMultiplayerId());
            assertEquals("user002", personnel.getAssignee());
            assertEquals("Li Si", personnel.getAssigneeName());
            assertEquals(1, personnel.getUndertakeStatus());
            assertEquals("another remark", personnel.getRemark());
            assertEquals(1, personnel.getIsDel());
            assertEquals("tenant2", personnel.getTenantId());
            assertEquals("creator2", personnel.getCreateUser());
            assertEquals(now, personnel.getCreateTime());
            assertEquals("updater2", personnel.getUpdateUser());
            assertEquals(now, personnel.getUpdateTime());
        }

        @Test
        @DisplayName("Default constructor creates object with null fields")
        void defaultConstructorCreatesObjectWithNullFields() {
            BpmVariableMultiplayerPersonnel personnel = new BpmVariableMultiplayerPersonnel();

            assertNull(personnel.getId());
            assertNull(personnel.getVariableMultiplayerId());
            assertNull(personnel.getAssignee());
            assertNull(personnel.getAssigneeName());
            assertNull(personnel.getUndertakeStatus());
            assertNull(personnel.getRemark());
            assertNull(personnel.getIsDel());
            assertNull(personnel.getTenantId());
            assertNull(personnel.getCreateUser());
            assertNull(personnel.getCreateTime());
            assertNull(personnel.getUpdateUser());
            assertNull(personnel.getUpdateTime());
        }
    }
}
