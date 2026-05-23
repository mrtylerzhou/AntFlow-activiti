package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class PersonnelEnumTest extends BaseTest {

    @Nested
    @DisplayName("fromNodePropertyEnum")
    class FromNodePropertyEnumTest {

        @Test
        @DisplayName("returns NODE_LOOP_PERSONNEL for NODE_PROPERTY_LOOP")
        void returnsLoopPersonnel() {
            assertEquals(PersonnelEnum.NODE_LOOP_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_LOOP));
        }

        @Test
        @DisplayName("returns NODE_LEVEL_PERSONNEL for NODE_PROPERTY_LEVEL")
        void returnsLevelPersonnel() {
            assertEquals(PersonnelEnum.NODE_LEVEL_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_LEVEL));
        }

        @Test
        @DisplayName("returns ROLE_PERSONNEL for NODE_PROPERTY_ROLE")
        void returnsRolePersonnel() {
            assertEquals(PersonnelEnum.ROLE_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_ROLE));
        }

        @Test
        @DisplayName("returns USERAPPOINTED_PERSONNEL for NODE_PROPERTY_PERSONNEL")
        void returnsUserAppointedPersonnel() {
            assertEquals(PersonnelEnum.USERAPPOINTED_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_PERSONNEL));
        }

        @Test
        @DisplayName("returns CUSTOMIZABLE_PERSONNEL for NODE_PROPERTY_CUSTOMIZE")
        void returnsCustomizablePersonnel() {
            assertEquals(PersonnelEnum.CUSTOMIZABLE_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE));
        }

        @Test
        @DisplayName("returns HRBP_PERSONNEL for NODE_PROPERTY_HRBP")
        void returnsHrbpPersonnel() {
            assertEquals(PersonnelEnum.HRBP_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_HRBP));
        }

        @Test
        @DisplayName("returns OUT_SIDE_ACCESS_PERSONNEL for NODE_PROPERTY_OUT_SIDE_ACCESS")
        void returnsOutSideAccessPersonnel() {
            assertEquals(PersonnelEnum.OUT_SIDE_ACCESS_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_OUT_SIDE_ACCESS));
        }

        @Test
        @DisplayName("returns START_USER_PERSONNEL for NODE_PROPERTY_START_USER")
        void returnsStartUserPersonnel() {
            assertEquals(PersonnelEnum.START_USER_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_START_USER));
        }

        @Test
        @DisplayName("returns DIRECT_LEADER_PERSONNEL for NODE_PROPERTY_DIRECT_LEADER")
        void returnsDirectLeaderPersonnel() {
            assertEquals(PersonnelEnum.DIRECT_LEADER_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_DIRECT_LEADER));
        }

        @Test
        @DisplayName("returns BUSINESS_TABLE_PERSONNEL for NODE_PROPERTY_BUSINESSTABLE")
        void returnsBusinessTablePersonnel() {
            assertEquals(PersonnelEnum.BUSINESS_TABLE_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE));
        }

        @Test
        @DisplayName("returns DEPARTMENT_LEADER_PERSONNEL for NODE_PROPERTY_DEPARTMENT_LEADER")
        void returnsDepartmentLeaderPersonnel() {
            assertEquals(PersonnelEnum.DEPARTMENT_LEADER_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_DEPARTMENT_LEADER));
        }

        @Test
        @DisplayName("returns APPROVED_USERS_PERSONNEL for NODE_PROPERTY_APPROVED_USERS")
        void returnsApprovedUsersPersonnel() {
            assertEquals(PersonnelEnum.APPROVED_USERS_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_APPROVED_USERS));
        }

        @Test
        @DisplayName("returns FORM_USERS_PERSONNEL for NODE_PROPERTY_FORM_RELATED")
        void returnsFormUsersPersonnel() {
            assertEquals(PersonnelEnum.FORM_USERS_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_FORM_RELATED));
        }

        @Test
        @DisplayName("returns UDR_USERS_PERSONNEL for NODE_PROPERTY_ZDY_RULES")
        void returnsUdrUsersPersonnel() {
            assertEquals(PersonnelEnum.UDR_USERS_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_ZDY_RULES));
        }

        @Test
        @DisplayName("returns null for null input")
        void returnsNullForNull() {
            assertNull(PersonnelEnum.fromNodePropertyEnum(null));
        }

        @Test
        @DisplayName("returns null for unmatched value")
        void returnsNullForUnmatched() {
            assertNull(PersonnelEnum.fromNodePropertyEnum(null));
        }
    }

    @Nested
    @DisplayName("desc")
    class DescTest {

        @Test
        @DisplayName("NODE_LOOP_PERSONNEL has correct desc")
        void nodeLoopDesc() {
            assertEquals("层层审批", PersonnelEnum.NODE_LOOP_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("NODE_LEVEL_PERSONNEL has correct desc")
        void nodeLevelDesc() {
            assertEquals("指定层级审批", PersonnelEnum.NODE_LEVEL_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("ROLE_PERSONNEL has correct desc")
        void roleDesc() {
            assertEquals("指定角色", PersonnelEnum.ROLE_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("USERAPPOINTED_PERSONNEL has correct desc")
        void userAppointedDesc() {
            assertEquals("指定人员", PersonnelEnum.USERAPPOINTED_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("CUSTOMIZABLE_PERSONNEL has correct desc")
        void customizableDesc() {
            assertEquals("发起人自选", PersonnelEnum.CUSTOMIZABLE_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("HRBP_PERSONNEL has correct desc")
        void hrbpDesc() {
            assertEquals("HRBP", PersonnelEnum.HRBP_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("OUT_SIDE_ACCESS_PERSONNEL has correct desc")
        void outSideAccessDesc() {
            assertEquals("外部传入人员", PersonnelEnum.OUT_SIDE_ACCESS_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("START_USER_PERSONNEL has correct desc")
        void startUserDesc() {
            assertEquals("发起人自己", PersonnelEnum.START_USER_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("DIRECT_LEADER_PERSONNEL has correct desc")
        void directLeaderDesc() {
            assertEquals("直属领导", PersonnelEnum.DIRECT_LEADER_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("BUSINESS_TABLE_PERSONNEL has correct desc")
        void businessTableDesc() {
            assertEquals("关联业务表", PersonnelEnum.BUSINESS_TABLE_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("DEPARTMENT_LEADER_PERSONNEL has correct desc")
        void departmentLeaderDesc() {
            assertEquals("部门负责人", PersonnelEnum.DEPARTMENT_LEADER_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("APPROVED_USERS_PERSONNEL has correct desc")
        void approvedUsersDesc() {
            assertEquals("被审批人自己", PersonnelEnum.APPROVED_USERS_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("FORM_USERS_PERSONNEL has correct desc")
        void formUsersDesc() {
            assertEquals("表单上下文人员", PersonnelEnum.FORM_USERS_PERSONNEL.getDesc());
        }

        @Test
        @DisplayName("UDR_USERS_PERSONNEL has correct desc")
        void udrUsersDesc() {
            assertEquals("用户自定义规则人员", PersonnelEnum.UDR_USERS_PERSONNEL.getDesc());
        }
    }

    @Nested
    @DisplayName("nodePropertyEnum reference")
    class NodePropertyEnumReferenceTest {

        @Test
        @DisplayName("NODE_LOOP_PERSONNEL references NODE_PROPERTY_LOOP")
        void nodeLoopRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_LOOP, PersonnelEnum.NODE_LOOP_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("NODE_LEVEL_PERSONNEL references NODE_PROPERTY_LEVEL")
        void nodeLevelRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_LEVEL, PersonnelEnum.NODE_LEVEL_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("ROLE_PERSONNEL references NODE_PROPERTY_ROLE")
        void roleRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_ROLE, PersonnelEnum.ROLE_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("USERAPPOINTED_PERSONNEL references NODE_PROPERTY_PERSONNEL")
        void userAppointedRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_PERSONNEL, PersonnelEnum.USERAPPOINTED_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("CUSTOMIZABLE_PERSONNEL references NODE_PROPERTY_CUSTOMIZE")
        void customizableRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE, PersonnelEnum.CUSTOMIZABLE_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("HRBP_PERSONNEL references NODE_PROPERTY_HRBP")
        void hrbpRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_HRBP, PersonnelEnum.HRBP_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("OUT_SIDE_ACCESS_PERSONNEL references NODE_PROPERTY_OUT_SIDE_ACCESS")
        void outSideAccessRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_OUT_SIDE_ACCESS, PersonnelEnum.OUT_SIDE_ACCESS_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("START_USER_PERSONNEL references NODE_PROPERTY_START_USER")
        void startUserRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_START_USER, PersonnelEnum.START_USER_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("DIRECT_LEADER_PERSONNEL references NODE_PROPERTY_DIRECT_LEADER")
        void directLeaderRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_DIRECT_LEADER, PersonnelEnum.DIRECT_LEADER_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("BUSINESS_TABLE_PERSONNEL references NODE_PROPERTY_BUSINESSTABLE")
        void businessTableRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE, PersonnelEnum.BUSINESS_TABLE_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("DEPARTMENT_LEADER_PERSONNEL references NODE_PROPERTY_DEPARTMENT_LEADER")
        void departmentLeaderRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_DEPARTMENT_LEADER, PersonnelEnum.DEPARTMENT_LEADER_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("APPROVED_USERS_PERSONNEL references NODE_PROPERTY_APPROVED_USERS")
        void approvedUsersRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_APPROVED_USERS, PersonnelEnum.APPROVED_USERS_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("FORM_USERS_PERSONNEL references NODE_PROPERTY_FORM_RELATED")
        void formUsersRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_FORM_RELATED, PersonnelEnum.FORM_USERS_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("UDR_USERS_PERSONNEL references NODE_PROPERTY_ZDY_RULES")
        void udrUsersRef() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_ZDY_RULES, PersonnelEnum.UDR_USERS_PERSONNEL.getNodePropertyEnum());
        }
    }
}
