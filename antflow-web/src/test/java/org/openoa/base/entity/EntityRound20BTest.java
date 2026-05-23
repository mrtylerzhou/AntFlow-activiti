package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EntityRound20BTest extends BaseTest {

    @Nested
    @DisplayName("UserMessageStatus")
    class UserMessageStatusTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            UserMessageStatus entity = new UserMessageStatus();
            assertNull(entity.getId());
            assertNull(entity.getUserId());
            assertNull(entity.getMessageStatus());
            assertNull(entity.getMailStatus());
            assertNull(entity.getShock());
            assertNull(entity.getSound());
            assertNull(entity.getOpenPhone());
            assertNull(entity.getNotTrouble());
            assertNull(entity.getNotTroubleTimeBegin());
            assertNull(entity.getNotTroubleTimeEnd());
            assertNull(entity.getCreateTime());
            assertNull(entity.getUpdateTime());
            assertNull(entity.getCreateUser());
            assertNull(entity.getUpdateUser());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            UserMessageStatus entity = UserMessageStatus.builder()
                    .id(1)
                    .userId("U001")
                    .messageStatus(true)
                    .mailStatus(false)
                    .shock(true)
                    .sound(true)
                    .openPhone(false)
                    .notTrouble(true)
                    .notTroubleTimeBegin(now)
                    .notTroubleTimeEnd(now)
                    .isDel(0)
                    .tenantId("t1")
                    .build();
            assertEquals(1, entity.getId());
            assertEquals("U001", entity.getUserId());
            assertTrue(entity.getMessageStatus());
            assertFalse(entity.getMailStatus());
            assertTrue(entity.getShock());
            assertTrue(entity.getSound());
            assertFalse(entity.getOpenPhone());
            assertTrue(entity.getNotTrouble());
        }
    }

    @Nested
    @DisplayName("UserMessage")
    class UserMessageTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            UserMessage entity = new UserMessage();
            assertNull(entity.getId());
            assertNull(entity.getUserId());
            assertNull(entity.getTitle());
            assertNull(entity.getContent());
            assertNull(entity.getUrl());
            assertNull(entity.getNode());
            assertNull(entity.getParams());
            assertNull(entity.getUrlParams());
            assertNull(entity.getIsRead());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
            assertNull(entity.getAppUrl());
            assertNull(entity.getSource());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            UserMessage entity = UserMessage.builder()
                    .id(1L)
                    .userId("U001")
                    .title("New approval")
                    .content("Please approve")
                    .url("http://x/approve")
                    .node("sid-A")
                    .params("p1")
                    .isRead(false)
                    .isDel(0)
                    .appUrl("app://approve")
                    .source(1)
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals("U001", entity.getUserId());
            assertEquals("New approval", entity.getTitle());
            assertFalse(entity.getIsRead());
            assertEquals(0, entity.getIsDel());
            assertEquals("app://approve", entity.getAppUrl());
            assertEquals(1, entity.getSource());
        }
    }

    @Nested
    @DisplayName("BpmnNode")
    class BpmnNodeTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNode entity = new BpmnNode();
            assertNull(entity.getId());
            assertNull(entity.getConfId());
            assertNull(entity.getNodeId());
            assertNull(entity.getNodeType());
            assertNull(entity.getNodeProperty());
            assertNull(entity.getNodeFrom());
            assertNull(entity.getBatchStatus());
            assertNull(entity.getApprovalStandard());
            assertNull(entity.getNodeName());
            assertNull(entity.getNodeDisplayName());
            assertNull(entity.getAnnotation());
            assertNull(entity.getIsDeduplication());
            assertFalse(entity.isDeduplicationExclude());
            assertNull(entity.getIsSignUp());
            assertNull(entity.getNoHeaderAction());
            assertNull(entity.getExtraFlags());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
            assertNull(entity.getNodeFroms());
            assertNull(entity.getIsDynamicCondition());
            assertNull(entity.getIsParallel());
            assertNull(entity.getNodeConfigJson());
            assertNull(entity.getIsOutSideProcess());
            assertNull(entity.getIsLowCodeFlow());
            assertNull(entity.getConfExtraFlags());
            assertNull(entity.getLabelList());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmnNode entity = BpmnNode.builder()
                    .id(1L)
                    .confId(10L)
                    .nodeId("sid-ABC")
                    .nodeType(4)
                    .nodeProperty(1)
                    .nodeFrom("sid-START")
                    .batchStatus(0)
                    .approvalStandard(2)
                    .nodeName("Approve")
                    .nodeDisplayName("Approval Node")
                    .isDeduplication(1)
                    .deduplicationExclude(false)
                    .isSignUp(0)
                    .extraFlags(0)
                    .isDel(0)
                    .isDynamicCondition(false)
                    .isParallel(false)
                    .nodeConfigJson("{\"key\":\"val\"}")
                    .build();
            assertEquals(1L, entity.getId());
            assertEquals(10L, entity.getConfId());
            assertEquals("sid-ABC", entity.getNodeId());
            assertEquals(4, entity.getNodeType());
            assertEquals(1, entity.getIsDeduplication());
            assertFalse(entity.isDeduplicationExclude());
            assertFalse(entity.getIsDynamicCondition());
            assertFalse(entity.getIsParallel());
            assertEquals("{\"key\":\"val\"}", entity.getNodeConfigJson());
        }
    }

    @Nested
    @DisplayName("BpmnNodeAdditionalSignConf")
    class BpmnNodeAdditionalSignConfTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNodeAdditionalSignConf entity = new BpmnNodeAdditionalSignConf();
            assertNull(entity.getId());
            assertNull(entity.getBpmnNodeId());
            assertNull(entity.getSignInfos());
            assertNull(entity.getSignProperty());
            assertNull(entity.getSignPropertyType());
            assertNull(entity.getSignType());
            assertNull(entity.getRemark());
            assertNull(entity.getIsDel());
            assertNull(entity.getTenantId());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            BpmnNodeAdditionalSignConf entity = new BpmnNodeAdditionalSignConf();
            entity.setId(1);
            entity.setBpmnNodeId(10L);
            entity.setSignInfos("[{\"id\":1}]");
            entity.setSignProperty(1);
            entity.setSignPropertyType(1);
            entity.setSignType(2);
            entity.setIsDel(0);
            assertEquals(Integer.valueOf(1), entity.getId());
            assertEquals(10L, entity.getBpmnNodeId());
            assertEquals("[{\"id\":1}]", entity.getSignInfos());
            assertEquals(1, entity.getSignProperty());
            assertEquals(1, entity.getSignPropertyType());
            assertEquals(2, entity.getSignType());
        }
    }

    @Nested
    @DisplayName("BpmFlowrunEntrust")
    class BpmFlowrunEntrustTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmFlowrunEntrust entity = new BpmFlowrunEntrust();
            assertNull(entity.getId());
            assertNull(entity.getRuninfoid());
            assertNull(entity.getRuntaskid());
            assertNull(entity.getOriginal());
            assertNull(entity.getOriginalName());
            assertNull(entity.getActual());
            assertNull(entity.getActualName());
            assertNull(entity.getType());
            assertNull(entity.getIsRead());
            assertNull(entity.getProcDefId());
            assertNull(entity.getIsView());
            assertNull(entity.isDel);
            assertNull(entity.getTenantId());
            assertNull(entity.getNodeId());
            assertNull(entity.getActionType());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmFlowrunEntrust entity = BpmFlowrunEntrust.builder()
                    .id(1)
                    .runinfoid("RI001")
                    .runtaskid("RT001")
                    .original("U001")
                    .originalName("Zhang")
                    .actual("U002")
                    .actualName("Li")
                    .type(1)
                    .isRead(0)
                    .procDefId("PD001")
                    .isView(0)
                    .isDel(0)
                    .nodeId("sid-A")
                    .actionType(0)
                    .build();
            assertEquals(Integer.valueOf(1), entity.getId());
            assertEquals("RI001", entity.getRuninfoid());
            assertEquals("RT001", entity.getRuntaskid());
            assertEquals("U001", entity.getOriginal());
            assertEquals("Zhang", entity.getOriginalName());
            assertEquals("U002", entity.getActual());
            assertEquals("Li", entity.getActualName());
            assertEquals(Integer.valueOf(1), entity.getType());
            assertEquals(Integer.valueOf(0), entity.isDel);
            assertEquals(Integer.valueOf(0), entity.getActionType());
        }
    }
}
