package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EntityRound20ATest extends BaseTest {

    @Nested
    @DisplayName("BpmnNodeTo")
    class BpmnNodeToTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNodeTo entity = new BpmnNodeTo();
            assertNull(entity.getId());
            assertNull(entity.getBpmnNodeId());
            assertNull(entity.getNodeTo());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
            assertNull(entity.getCreateUser());
            assertNull(entity.getCreateTime());
            assertNull(entity.getUpdateUser());
            assertNull(entity.getUpdateTime());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmnNodeTo entity = BpmnNodeTo.builder()
                    .id(1L)
                    .bpmnNodeId(10L)
                    .nodeTo("sid-ABC")
                    .remark("test")
                    .isDel(0)
                    .tenantId("t1")
                    .createUser("admin")
                    .createTime(now)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getBpmnNodeId());
            assertEquals("sid-ABC", entity.getNodeTo());
            assertEquals("test", entity.getRemark());
            assertEquals(0, entity.getIsDel());
            assertEquals("t1", entity.getTenantId());
            assertEquals("admin", entity.getCreateUser());
            assertEquals(now, entity.getCreateTime());
        }
    }

    @Nested
    @DisplayName("BpmnTemplate")
    class BpmnTemplateTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnTemplate entity = new BpmnTemplate();
            assertNull(entity.getId());
            assertNull(entity.getConfId());
            assertNull(entity.getNodeId());
            assertNull(entity.getEvent());
            assertNull(entity.getInforms());
            assertNull(entity.getEmps());
            assertNull(entity.getRoles());
            assertNull(entity.getFuncs());
            assertNull(entity.getTemplateId());
            assertNull(entity.getMessageSendType());
            assertNull(entity.getFormCode());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnTemplate entity = BpmnTemplate.builder()
                    .id(1L)
                    .confId(10L)
                    .nodeId(20L)
                    .event(1)
                    .informs("1,2")
                    .emps("e1,e2")
                    .roles("r1")
                    .funcs("f1")
                    .templateId(30L)
                    .messageSendType("1,2,3")
                    .formCode("FC001")
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getConfId());
            assertEquals(20L, entity.getNodeId());
            assertEquals(1, entity.getEvent());
            assertEquals("1,2", entity.getInforms());
            assertEquals("e1,e2", entity.getEmps());
            assertEquals("r1", entity.getRoles());
            assertEquals("f1", entity.getFuncs());
            assertEquals(30L, entity.getTemplateId());
            assertEquals("1,2,3", entity.getMessageSendType());
            assertEquals("FC001", entity.getFormCode());
            assertEquals(0, entity.getIsDel());
        }
    }



    @Nested
    @DisplayName("BpmnNodeLabel")
    class BpmnNodeLabelTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNodeLabel entity = new BpmnNodeLabel();
            assertNull(entity.getId());
            assertNull(entity.getNodeId());
            assertNull(entity.getLabelName());
            assertNull(entity.getLabelValue());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
            assertNull(entity.getCreateUser());
            assertNull(entity.getCreateTime());
            assertNull(entity.getUpdateUser());
            assertNull(entity.getUpdateTime());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            Date now = new Date();
            BpmnNodeLabel entity = new BpmnNodeLabel();
            entity.setId(1L);
            entity.setNodeId(10L);
            entity.setLabelName("dept");
            entity.setLabelValue("Dev");
            entity.setRemark("test");
            entity.setIsDel(0);
            entity.setTenantId("t1");
            entity.setCreateUser("admin");
            entity.setCreateTime(now);
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getNodeId());
            assertEquals("dept", entity.getLabelName());
            assertEquals("Dev", entity.getLabelValue());
            assertEquals(0, entity.getIsDel());
        }
    }

    @Nested
    @DisplayName("BpmnNodeSignUpConf")
    class BpmnNodeSignUpConfTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNodeSignUpConf entity = new BpmnNodeSignUpConf();
            assertNull(entity.getId());
            assertNull(entity.getBpmnNodeId());
            assertNull(entity.getAfterSignUpWay());
            assertNull(entity.getSignUpType());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnNodeSignUpConf entity = BpmnNodeSignUpConf.builder()
                    .id(1L)
                    .bpmnNodeId(10L)
                    .afterSignUpWay(1)
                    .signUpType(2)
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getBpmnNodeId());
            assertEquals(1, entity.getAfterSignUpWay());
            assertEquals(2, entity.getSignUpType());
        }
    }

}
