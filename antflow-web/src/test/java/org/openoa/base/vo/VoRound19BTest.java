package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class VoRound19BTest extends BaseTest {

    @Nested
    @DisplayName("SendInfo")
    class SendInfoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            SendInfo si = new SendInfo();
            assertNull(si.getMail());
            assertNull(si.getMessageInfo());
            assertNull(si.getUserMessage());
            assertNull(si.getBaseMsgInfo());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            MailInfo mail = MailInfo.builder().title("m").build();
            MessageInfo msg = MessageInfo.builder().content("c").build();
            BaseMsgInfo baseMsg = BaseMsgInfo.builder().msgTitle("t").build();
            SendInfo si = SendInfo.builder()
                    .mail(mail)
                    .messageInfo(msg)
                    .baseMsgInfo(baseMsg)
                    .build();
            assertNotNull(si.getMail());
            assertEquals("m", si.getMail().getTitle());
            assertNotNull(si.getMessageInfo());
            assertEquals("c", si.getMessageInfo().getContent());
            assertNotNull(si.getBaseMsgInfo());
            assertEquals("t", si.getBaseMsgInfo().getMsgTitle());
        }
    }

    @Nested
    @DisplayName("BaseMsgInfo")
    class BaseMsgInfoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BaseMsgInfo bmi = new BaseMsgInfo();
            assertNull(bmi.getMsgTitle());
            assertNull(bmi.getUrl());
            assertNull(bmi.getUsername());
            assertNull(bmi.getContent());
            assertNull(bmi.getUserMessageStatus());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BaseMsgInfo bmi = BaseMsgInfo.builder()
                    .msgTitle("title")
                    .url("http://x")
                    .username("user1")
                    .content("cont")
                    .build();
            assertEquals("title", bmi.getMsgTitle());
            assertEquals("http://x", bmi.getUrl());
            assertEquals("user1", bmi.getUsername());
            assertEquals("cont", bmi.getContent());
        }
    }

    @Nested
    @DisplayName("ProcessActionButtonVo")
    class ProcessActionButtonVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            ProcessActionButtonVo vo = new ProcessActionButtonVo();
            assertNull(vo.getButtonType());
            assertNull(vo.getShow());
            assertNull(vo.getType());
            assertNull(vo.getName());
            assertNull(vo.getAppShow());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            ProcessActionButtonVo vo = ProcessActionButtonVo.builder()
                    .buttonType(1)
                    .show(1)
                    .type("primary")
                    .name("Agree")
                    .appShow(3)
                    .build();
            assertEquals(1, vo.getButtonType());
            assertEquals(1, vo.getShow());
            assertEquals("primary", vo.getType());
            assertEquals("Agree", vo.getName());
            assertEquals(3, vo.getAppShow());
        }

        @Test
        @DisplayName("all-arg constructor")
        void allArgConstructor() {
            ProcessActionButtonVo vo = new ProcessActionButtonVo(2, 2, "default", "Reject", 1);
            assertEquals(2, vo.getButtonType());
            assertEquals(2, vo.getShow());
            assertEquals("default", vo.getType());
            assertEquals("Reject", vo.getName());
            assertEquals(1, vo.getAppShow());
        }
    }

    @Nested
    @DisplayName("MqProcessEventVo")
    class MqProcessEventVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            MqProcessEventVo vo = new MqProcessEventVo();
            assertNull(vo.getProcessCode());
            assertNull(vo.getFormCode());
            assertNull(vo.getProcInstId());
            assertNull(vo.getBusinessId());
            assertNull(vo.getTaskId());
            assertNull(vo.getOpTime());
            assertNull(vo.getButtonOperationType());
            assertNull(vo.getOperationUserId());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            Date now = new Date();
            MqProcessEventVo vo = new MqProcessEventVo();
            vo.setProcessCode("PC001");
            vo.setFormCode("FC001");
            vo.setProcInstId("PI001");
            vo.setBusinessId("B001");
            vo.setTaskId("T001");
            vo.setOpTime(now);
            vo.setButtonOperationType(1);
            vo.setOperationUserId("U001");
            assertEquals("PC001", vo.getProcessCode());
            assertEquals("FC001", vo.getFormCode());
            assertEquals("PI001", vo.getProcInstId());
            assertEquals("B001", vo.getBusinessId());
            assertEquals("T001", vo.getTaskId());
            assertEquals(now, vo.getOpTime());
            assertEquals(1, vo.getButtonOperationType());
            assertEquals("U001", vo.getOperationUserId());
        }
    }

    @Nested
    @DisplayName("ExtraSignInfoVo")
    class ExtraSignInfoVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            ExtraSignInfoVo vo = new ExtraSignInfoVo();
            assertNull(vo.getPropertyType());
            assertNull(vo.getNodeProperty());
            assertNull(vo.getSignInfos());
            assertNull(vo.getOtherSignInfos());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            ExtraSignInfoVo vo = ExtraSignInfoVo.builder()
                    .propertyType(1)
                    .nodeProperty(2)
                    .signInfos(Arrays.asList())
                    .otherSignInfos(Arrays.asList())
                    .build();
            assertEquals(1, vo.getPropertyType());
            assertEquals(2, vo.getNodeProperty());
            assertNotNull(vo.getSignInfos());
            assertNotNull(vo.getOtherSignInfos());
        }
    }

    @Nested
    @DisplayName("BpmVerifyInfoVo")
    class BpmVerifyInfoVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVerifyInfoVo vo = new BpmVerifyInfoVo();
            assertNull(vo.getId());
            assertNull(vo.getRunInfoId());
            assertNull(vo.getVerifyUserId());
            assertNull(vo.getVerifyUserIds());
            assertNull(vo.getVerifyUserName());
            assertNull(vo.getVerifyStatus());
            assertNull(vo.getVerifyDesc());
            assertNull(vo.getVerifyDate());
            assertNull(vo.getTaskName());
            assertNull(vo.getBusinessType());
            assertNull(vo.getBusinessId());
            assertNull(vo.getVerifyStatusName());
            assertNull(vo.getOriginalId());
            assertNull(vo.getOriginalName());
            assertNull(vo.getProcessCode());
            assertNull(vo.getProcessCodeList());
            assertNull(vo.getElementId());
            assertNull(vo.getSort());
            assertNull(vo.getVerifyAttachments());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmVerifyInfoVo vo = BpmVerifyInfoVo.builder()
                    .id("1")
                    .runInfoId("ri1")
                    .verifyUserId("vu1")
                    .verifyUserName("VU Name")
                    .verifyStatus(1)
                    .verifyDesc("ok")
                    .verifyDate(now)
                    .taskName("task1")
                    .sort(0)
                    .build();
            assertEquals("1", vo.getId());
            assertEquals("ri1", vo.getRunInfoId());
            assertEquals("vu1", vo.getVerifyUserId());
            assertEquals("VU Name", vo.getVerifyUserName());
            assertEquals(1, vo.getVerifyStatus());
            assertEquals("ok", vo.getVerifyDesc());
            assertEquals(now, vo.getVerifyDate());
            assertEquals("task1", vo.getTaskName());
            assertEquals(0, vo.getSort());
        }
    }

    @Nested
    @DisplayName("BpmVerifyAttachmentVo")
    class BpmVerifyAttachmentVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmVerifyAttachmentVo vo = new BpmVerifyAttachmentVo();
            assertNull(vo.getId());
            assertNull(vo.getVerifyInfoId());
            assertNull(vo.getFilePath());
            assertNull(vo.getNewFileName());
            assertNull(vo.getOriginalFileName());
            assertNull(vo.getFileSize());
            assertNull(vo.getFileType());
            assertNull(vo.getFileUrl());
            assertNull(vo.getCreateTime());
            assertNull(vo.getRemark());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmVerifyAttachmentVo vo = BpmVerifyAttachmentVo.builder()
                    .id("a1")
                    .verifyInfoId(100L)
                    .filePath("/tmp/f.txt")
                    .newFileName("f_new.txt")
                    .originalFileName("f.txt")
                    .fileSize(1024)
                    .fileType("txt")
                    .fileUrl("http://x/f.txt")
                    .createTime(now)
                    .remark("test")
                    .build();
            assertEquals("a1", vo.getId());
            assertEquals(100L, vo.getVerifyInfoId());
            assertEquals("/tmp/f.txt", vo.getFilePath());
            assertEquals("f_new.txt", vo.getNewFileName());
            assertEquals("f.txt", vo.getOriginalFileName());
            assertEquals(1024, vo.getFileSize());
            assertEquals("txt", vo.getFileType());
            assertEquals("http://x/f.txt", vo.getFileUrl());
            assertEquals(now, vo.getCreateTime());
            assertEquals("test", vo.getRemark());
        }
    }

    @Nested
    @DisplayName("PrevEmployeeInfo")
    class PrevEmployeeInfoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            PrevEmployeeInfo info = new PrevEmployeeInfo();
            assertNull(info.getEmployeeId());
            assertNull(info.getEmployeeName());
            assertNull(info.getJobLevelId());
            assertNull(info.getJobLevel());
            assertNull(info.getDepartName());
            assertNull(info.getDepartId());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            PrevEmployeeInfo info = PrevEmployeeInfo.builder()
                    .employeeId(1)
                    .employeeName("Zhang")
                    .jobLevelId(10L)
                    .jobLevel("P5")
                    .departName("Dev")
                    .departId(2)
                    .build();
            assertEquals(1, info.getEmployeeId());
            assertEquals("Zhang", info.getEmployeeName());
            assertEquals(10L, info.getJobLevelId());
            assertEquals("P5", info.getJobLevel());
            assertEquals("Dev", info.getDepartName());
            assertEquals(2, info.getDepartId());
        }
    }
}
