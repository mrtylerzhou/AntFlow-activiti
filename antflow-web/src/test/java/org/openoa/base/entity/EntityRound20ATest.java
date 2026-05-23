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
    @DisplayName("BpmVerifyAttachment")
    class BpmVerifyAttachmentTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVerifyAttachment entity = new BpmVerifyAttachment();
            assertNull(entity.getId());
            assertNull(entity.getVerifyInfoId());
            assertNull(entity.getFilePath());
            assertNull(entity.getNewFileName());
            assertNull(entity.getOriginalFileName());
            assertNull(entity.getFileSize());
            assertNull(entity.getFileType());
            assertNull(entity.getFileUrl());
            assertNull(entity.getCreateTime());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmVerifyAttachment entity = BpmVerifyAttachment.builder()
                    .id(1L)
                    .verifyInfoId(100L)
                    .filePath("/tmp/f.pdf")
                    .newFileName("f_new.pdf")
                    .originalFileName("f.pdf")
                    .fileSize(2048)
                    .fileType("pdf")
                    .fileUrl("http://x/f.pdf")
                    .createTime(now)
                    .remark("test")
                    .isDel(0)
                    .tenantId("t1")
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(100L, entity.getVerifyInfoId());
            assertEquals("/tmp/f.pdf", entity.getFilePath());
            assertEquals("f_new.pdf", entity.getNewFileName());
            assertEquals("f.pdf", entity.getOriginalFileName());
            assertEquals(2048, entity.getFileSize());
            assertEquals("pdf", entity.getFileType());
            assertEquals(0, entity.getIsDel());
        }
    }

    @Nested
    @DisplayName("BpmVariableApproveRemind")
    class BpmVariableApproveRemindTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVariableApproveRemind entity = new BpmVariableApproveRemind();
            assertNull(entity.getId());
            assertNull(entity.getVariableId());
            assertNull(entity.getElementId());
            assertNull(entity.getContent());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmVariableApproveRemind entity = BpmVariableApproveRemind.builder()
                    .id(1L)
                    .variableId(10L)
                    .elementId("sid-X")
                    .content("Please approve")
                    .remark("remind")
                    .isDel(0)
                    .tenantId("t1")
                    .createUser("admin")
                    .createTime(now)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getVariableId());
            assertEquals("sid-X", entity.getElementId());
            assertEquals("Please approve", entity.getContent());
            assertEquals(0, entity.getIsDel());
        }
    }

    @Nested
    @DisplayName("BpmVariableSequenceFlow")
    class BpmVariableSequenceFlowTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVariableSequenceFlow entity = new BpmVariableSequenceFlow();
            assertNull(entity.getId());
            assertNull(entity.getVariableId());
            assertNull(entity.getElementId());
            assertNull(entity.getElementName());
            assertNull(entity.getElementFromId());
            assertNull(entity.getElementToId());
            assertNull(entity.getSequenceFlowType());
            assertNull(entity.getSequenceFlowConditions());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmVariableSequenceFlow entity = BpmVariableSequenceFlow.builder()
                    .id(1L)
                    .variableId(10L)
                    .elementId("flow1")
                    .elementName("to approve")
                    .elementFromId("sid-A")
                    .elementToId("sid-B")
                    .sequenceFlowType(2)
                    .sequenceFlowConditions("amount>100")
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getVariableId());
            assertEquals("flow1", entity.getElementId());
            assertEquals("to approve", entity.getElementName());
            assertEquals("sid-A", entity.getElementFromId());
            assertEquals("sid-B", entity.getElementToId());
            assertEquals(2, entity.getSequenceFlowType());
            assertEquals("amount>100", entity.getSequenceFlowConditions());
        }
    }

    @Nested
    @DisplayName("BpmVariableButton")
    class BpmVariableButtonTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVariableButton entity = new BpmVariableButton();
            assertNull(entity.getId());
            assertNull(entity.getVariableId());
            assertNull(entity.getElementId());
            assertNull(entity.getButtonPageType());
            assertNull(entity.getButtonType());
            assertNull(entity.getButtonName());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmVariableButton entity = BpmVariableButton.builder()
                    .id(1L)
                    .variableId(10L)
                    .elementId("sid-A")
                    .buttonPageType(1)
                    .buttonType(3)
                    .buttonName("Agree")
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getVariableId());
            assertEquals("sid-A", entity.getElementId());
            assertEquals(1, entity.getButtonPageType());
            assertEquals(3, entity.getButtonType());
            assertEquals("Agree", entity.getButtonName());
        }
    }

    @Nested
    @DisplayName("BpmVariableSignUp")
    class BpmVariableSignUpTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVariableSignUp entity = new BpmVariableSignUp();
            assertNull(entity.getId());
            assertNull(entity.getVariableId());
            assertNull(entity.getElementId());
            assertNull(entity.getNodeId());
            assertNull(entity.getAfterSignUpWay());
            assertNull(entity.getSubElements());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmVariableSignUp entity = BpmVariableSignUp.builder()
                    .id(1L)
                    .variableId(10L)
                    .elementId("sid-A")
                    .nodeId("N1")
                    .afterSignUpWay(1)
                    .subElements("[{}]")
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getVariableId());
            assertEquals("sid-A", entity.getElementId());
            assertEquals("N1", entity.getNodeId());
            assertEquals(1, entity.getAfterSignUpWay());
            assertEquals("[{}]", entity.getSubElements());
        }
    }

    @Nested
    @DisplayName("BpmVariableSignUpPersonnel")
    class BpmVariableSignUpPersonnelTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVariableSignUpPersonnel entity = new BpmVariableSignUpPersonnel();
            assertNull(entity.getId());
            assertNull(entity.getVariableId());
            assertNull(entity.getElementId());
            assertNull(entity.getAssignee());
            assertNull(entity.getAssigneeName());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmVariableSignUpPersonnel entity = BpmVariableSignUpPersonnel.builder()
                    .id(1L)
                    .variableId(10L)
                    .elementId("sid-A")
                    .assignee("u001")
                    .assigneeName("Zhang")
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals("u001", entity.getAssignee());
            assertEquals("Zhang", entity.getAssigneeName());
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

    @Nested
    @DisplayName("BpmnNodeLoopConf")
    class BpmnNodeLoopConfTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNodeLoopConf entity = new BpmnNodeLoopConf();
            assertNull(entity.getId());
            assertNull(entity.getBpmnNodeId());
            assertNull(entity.getLoopEndType());
            assertNull(entity.getLoopNumberPlies());
            assertNull(entity.getLoopEndPerson());
            assertNull(entity.getNoparticipatingStaffIds());
            assertNull(entity.getLoopEndGrade());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnNodeLoopConf entity = BpmnNodeLoopConf.builder()
                    .id(1L)
                    .bpmnNodeId(10L)
                    .loopEndType(1)
                    .loopNumberPlies(3)
                    .loopEndPerson("u001")
                    .noparticipatingStaffIds("u002,u003")
                    .loopEndGrade(5)
                    .isDel(0)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getBpmnNodeId());
            assertEquals(1, entity.getLoopEndType());
            assertEquals(3, entity.getLoopNumberPlies());
            assertEquals("u001", entity.getLoopEndPerson());
            assertEquals("u002,u003", entity.getNoparticipatingStaffIds());
            assertEquals(5, entity.getLoopEndGrade());
        }
    }
}
