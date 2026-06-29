package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VoRound19CTest extends BaseTest {

    @Nested
    @DisplayName("DataVo")
    class DataVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            DataVo vo = new DataVo();
            assertNull(vo.getIds());
            assertNull(vo.getSender());
            assertNull(vo.getReceiverId());
            assertNull(vo.getReceiverName());
            assertNull(vo.getBeginTime());
            assertNull(vo.getEndTime());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            Date now = new Date();
            DataVo vo = new DataVo();
            vo.setIds(Arrays.asList(new IdsVo()));
            vo.setSender("s1");
            vo.setReceiverId("r1");
            vo.setReceiverName("RName");
            vo.setBeginTime(now);
            vo.setEndTime(now);
            assertNotNull(vo.getIds());
            assertEquals(1, vo.getIds().size());
            assertEquals("s1", vo.getSender());
            assertEquals("r1", vo.getReceiverId());
            assertEquals("RName", vo.getReceiverName());
            assertEquals(now, vo.getBeginTime());
            assertEquals(now, vo.getEndTime());
        }
    }

    @Nested
    @DisplayName("FieldAttributeInfoVO")
    class FieldAttributeInfoVOTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            FieldAttributeInfoVO vo = new FieldAttributeInfoVO();
            assertNull(vo.getFieldName());
            assertNull(vo.getFieldType());
            assertNull(vo.getFieldLabel());
            assertNull(vo.getFieldValue());
            assertNull(vo.getChoiceMaxValue());
            assertFalse(vo.getIsMultiChoice());
            assertNull(vo.getSort());
            assertNull(vo.getValue());
        }

        @Test
        @DisplayName("isMultiChoice default is false, can be set true")
        void isMultiChoiceDefault() {
            FieldAttributeInfoVO vo = new FieldAttributeInfoVO();
            assertFalse(vo.getIsMultiChoice());
            vo.setIsMultiChoice(true);
            assertTrue(vo.getIsMultiChoice());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            FieldAttributeInfoVO vo = new FieldAttributeInfoVO();
            vo.setFieldName("amount");
            vo.setFieldType("number");
            vo.setFieldLabel("Amount");
            vo.setFieldValue("100");
            vo.setChoiceMaxValue(5);
            vo.setSort(1);
            vo.setValue(42);
            assertEquals("amount", vo.getFieldName());
            assertEquals("number", vo.getFieldType());
            assertEquals("Amount", vo.getFieldLabel());
            assertEquals("100", vo.getFieldValue());
            assertEquals(5, vo.getChoiceMaxValue());
            assertEquals(1, vo.getSort());
            assertEquals(42, vo.getValue());
        }
    }

    @Nested
    @DisplayName("DefaultTemplateVo")
    class DefaultTemplateVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            DefaultTemplateVo vo = new DefaultTemplateVo();
            assertNull(vo.getId());
            assertNull(vo.getEvent());
            assertNull(vo.getEventValue());
            assertNull(vo.getTemplateId());
            assertNull(vo.getTemplateName());
            assertNull(vo.getIsDel());
            assertNull(vo.getCreateTime());
            assertNull(vo.getCreateUser());
            assertNull(vo.getUpdateTime());
            assertNull(vo.getUpdateUser());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            DefaultTemplateVo vo = DefaultTemplateVo.builder()
                    .id(1L)
                    .event(2)
                    .eventValue("EVT")
                    .templateId(10L)
                    .templateName("Tmpl")
                    .isDel(0)
                    .createTime(now)
                    .createUser("admin")
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals(2, vo.getEvent());
            assertEquals("EVT", vo.getEventValue());
            assertEquals(10L, vo.getTemplateId());
            assertEquals("Tmpl", vo.getTemplateName());
            assertEquals(0, vo.getIsDel());
            assertEquals(now, vo.getCreateTime());
            assertEquals("admin", vo.getCreateUser());
        }
    }

    @Nested
    @DisplayName("BpmnNodeButtonConfBaseVo")
    class BpmnNodeButtonConfBaseVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnNodeButtonConfBaseVo vo = new BpmnNodeButtonConfBaseVo();
            assertNull(vo.getStartPage());
            assertNull(vo.getApprovalPage());
            assertNull(vo.getViewPage());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnNodeButtonConfBaseVo vo = BpmnNodeButtonConfBaseVo.builder()
                    .startPage(Arrays.asList(1, 2))
                    .approvalPage(Arrays.asList(3))
                    .viewPage(Arrays.asList(4, 5))
                    .build();
            assertEquals(Arrays.asList(1, 2), vo.getStartPage());
            assertEquals(Arrays.asList(3), vo.getApprovalPage());
            assertEquals(Arrays.asList(4, 5), vo.getViewPage());
        }
    }

    @Nested
    @DisplayName("BpmnConfCommonButtonPropertyVo")
    class BpmnConfCommonButtonPropertyVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnConfCommonButtonPropertyVo vo = new BpmnConfCommonButtonPropertyVo();
            assertNull(vo.getButtonType());
            assertNull(vo.getButtonName());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnConfCommonButtonPropertyVo vo = BpmnConfCommonButtonPropertyVo.builder()
                    .buttonType(1)
                    .buttonName("Agree")
                    .build();
            assertEquals(1, vo.getButtonType());
            assertEquals("Agree", vo.getButtonName());
        }
    }

    @Nested
    @DisplayName("BpmnConfCommonButtonsVo")
    class BpmnConfCommonButtonsVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnConfCommonButtonsVo vo = new BpmnConfCommonButtonsVo();
            assertNull(vo.getStartPage());
            assertNull(vo.getApprovalPage());
            assertNull(vo.getViewPage());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnConfCommonButtonPropertyVo btn = BpmnConfCommonButtonPropertyVo.builder()
                    .buttonType(1).buttonName("A").build();
            BpmnConfCommonButtonsVo vo = BpmnConfCommonButtonsVo.builder()
                    .startPage(Arrays.asList(btn))
                    .approvalPage(Arrays.asList(btn))
                    .viewPage(Arrays.asList(btn))
                    .build();
            assertEquals(1, vo.getStartPage().size());
            assertEquals(1, vo.getApprovalPage().size());
            assertEquals(1, vo.getViewPage().size());
        }
    }

    @Nested
    @DisplayName("BpmnConfViewPageButtonVo")
    class BpmnConfViewPageButtonVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnConfViewPageButtonVo vo = new BpmnConfViewPageButtonVo();
            assertNull(vo.getViewPageStart());
            assertNull(vo.getViewPageOther());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnConfCommonButtonPropertyVo btn = BpmnConfCommonButtonPropertyVo.builder()
                    .buttonType(2).buttonName("B").build();
            BpmnConfViewPageButtonVo vo = BpmnConfViewPageButtonVo.builder()
                    .viewPageStart(Arrays.asList(btn))
                    .viewPageOther(Arrays.asList(btn))
                    .build();
            assertNotNull(vo.getViewPageStart());
            assertNotNull(vo.getViewPageOther());
        }
    }

    @Nested
    @DisplayName("BpmnViewPageButtonBaseVo")
    class BpmnViewPageButtonBaseVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnViewPageButtonBaseVo vo = new BpmnViewPageButtonBaseVo();
            assertNull(vo.getViewPageStart());
            assertNull(vo.getViewPageOther());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnViewPageButtonBaseVo vo = BpmnViewPageButtonBaseVo.builder()
                    .viewPageStart(Arrays.asList(1))
                    .viewPageOther(Arrays.asList(2, 3))
                    .build();
            assertEquals(Arrays.asList(1), vo.getViewPageStart());
            assertEquals(Arrays.asList(2, 3), vo.getViewPageOther());
        }
    }

    @Nested
    @DisplayName("ContansDataVo")
    class ContansDataVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            ContansDataVo vo = new ContansDataVo();
            assertNull(vo.getId());
            assertNull(vo.getName());
            assertNull(vo.getTaskId());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            ContansDataVo vo = ContansDataVo.builder()
                    .id(1)
                    .name("test")
                    .taskId("t1")
                    .build();
            assertEquals(1, vo.getId());
            assertEquals("test", vo.getName());
            assertEquals("t1", vo.getTaskId());
        }
    }

    @Nested
    @DisplayName("BpmnNodeParamsVo")
    class BpmnNodeParamsVoTest {

        @Test
        @DisplayName("no-arg constructor defaults - isNodeDeduplication is 0")
        void noArgDefaults() {
            BpmnNodeParamsVo vo = new BpmnNodeParamsVo();
            assertNull(vo.getNodeTo());
            assertNull(vo.getParamType());
            assertNull(vo.getAssignee());
            assertNull(vo.getAssigneeList());
            assertEquals(0, vo.getIsNodeDeduplication());
        }

        @Test
        @DisplayName("builder does not preserve isNodeDeduplication default")
        void builderDefault() {
            BpmnNodeParamsVo vo = BpmnNodeParamsVo.builder()
                    .nodeTo("n1")
                    .paramType(1)
                    .build();
            assertEquals("n1", vo.getNodeTo());
            assertEquals(1, vo.getParamType());
        }

        @Test
        @DisplayName("setter overrides isNodeDeduplication")
        void setterOverride() {
            BpmnNodeParamsVo vo = new BpmnNodeParamsVo();
            assertEquals(0, vo.getIsNodeDeduplication());
            vo.setIsNodeDeduplication(1);
            assertEquals(1, vo.getIsNodeDeduplication());
        }
    }

    @Nested
    @DisplayName("BpmnNodeParamsAssigneeVo")
    class BpmnNodeParamsAssigneeVoTest {

        @Test
        @DisplayName("no-arg constructor defaults - isDeduplication is 0")
        void noArgDefaults() {
            BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
            assertNull(vo.getAssignee());
            assertNull(vo.getAssigneeName());
            assertNull(vo.getElementName());
            assertEquals(0, vo.getIsDeduplication());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnNodeParamsAssigneeVo vo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("a1")
                    .assigneeName("A1")
                    .elementName("E1")
                    .isDeduplication(1)
                    .build();
            assertEquals("a1", vo.getAssignee());
            assertEquals("A1", vo.getAssigneeName());
            assertEquals("E1", vo.getElementName());
            assertEquals(1, vo.getIsDeduplication());
        }
    }

    @Nested
    @DisplayName("ProcessNodeVo")
    class ProcessNodeVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            ProcessNodeVo vo = new ProcessNodeVo();
            assertNull(vo.getId());
            assertNull(vo.getNodeId());
            assertNull(vo.getNodeName());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            ProcessNodeVo vo = ProcessNodeVo.builder()
                    .id(1L)
                    .nodeId("sid-ABC")
                    .nodeName("Approve")
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals("sid-ABC", vo.getNodeId());
            assertEquals("Approve", vo.getNodeName());
        }
    }

    @Nested
    @DisplayName("InformationTemplateVo")
    class InformationTemplateVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            InformationTemplateVo vo = new InformationTemplateVo();
            assertNull(vo.getId());
            assertNull(vo.getName());
            assertNull(vo.getNum());
            assertNull(vo.getSystemTitle());
            assertNull(vo.getSystemContent());
            assertNull(vo.getMailTitle());
            assertNull(vo.getMailContent());
            assertNull(vo.getNoteContent());
            assertNull(vo.getJumpUrl());
            assertNull(vo.getJumpUrlValue());
            assertNull(vo.getRemark());
            assertNull(vo.getStatus());
            assertNull(vo.getStatusValue());
            assertNull(vo.getEvent());
            assertNull(vo.getEventName());
            assertNull(vo.getIsDel());
            assertNull(vo.getCreateTime());
            assertNull(vo.getCreateUser());
            assertNull(vo.getUpdateTime());
            assertNull(vo.getUpdateUser());
            assertNull(vo.getWildcardCharacterMap());
        }

        @Test
        @DisplayName("builder pattern with wildcardCharacterMap")
        void builderWithMap() {
            Map<Integer, String> map = new HashMap<>();
            map.put(1, "{流程类型}");
            map.put(2, "{发起人}");
            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .name("tmpl1")
                    .systemTitle("title")
                    .systemContent("content")
                    .jumpUrl(1)
                    .status(0)
                    .wildcardCharacterMap(map)
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals("tmpl1", vo.getName());
            assertEquals("title", vo.getSystemTitle());
            assertEquals(1, vo.getJumpUrl());
            assertEquals(0, vo.getStatus());
            assertEquals(2, vo.getWildcardCharacterMap().size());
            assertEquals("{流程类型}", vo.getWildcardCharacterMap().get(1));
        }
    }

    @Nested
    @DisplayName("BpmnStartConditionsExtendVo")
    class BpmnStartConditionsExtendVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnStartConditionsExtendVo vo = new BpmnStartConditionsExtendVo();
            assertNull(vo.getBpmnCode());
        }

        @Test
        @DisplayName("inherits from BpmnStartConditionsVo")
        void inheritance() {
            BpmnStartConditionsExtendVo vo = new BpmnStartConditionsExtendVo();
            vo.setBpmnCode("BC001");
            vo.setProcessNum("PN001");
            assertEquals("BC001", vo.getBpmnCode());
            assertEquals("PN001", vo.getProcessNum());
        }

        @Test
        @DisplayName("all-arg constructor")
        void allArgConstructor() {
            BpmnStartConditionsExtendVo vo = new BpmnStartConditionsExtendVo("BC002");
            assertEquals("BC002", vo.getBpmnCode());
        }
    }

    @Nested
    @DisplayName("BpmnApproveRemindVo")
    class BpmnApproveRemindVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnApproveRemindVo vo = new BpmnApproveRemindVo();
            assertNull(vo.getId());
            assertNull(vo.getConfId());
            assertNull(vo.getNodeId());
            assertNull(vo.getIsInuse());
            assertNull(vo.getTemplateId());
            assertNull(vo.getTemplateName());
            assertNull(vo.getDays());
            assertNull(vo.getDayList());
            assertNull(vo.getIsDel());
            assertNull(vo.getCreateTime());
            assertNull(vo.getCreateUser());
            assertNull(vo.getUpdateTime());
            assertNull(vo.getUpdateUser());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BpmnApproveRemindVo vo = BpmnApproveRemindVo.builder()
                    .id(1L)
                    .confId(10L)
                    .nodeId(20L)
                    .isInuse(true)
                    .templateId(30L)
                    .templateName("Tmpl")
                    .days("1,2,3")
                    .dayList(Arrays.asList(1, 2, 3))
                    .isDel(0)
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals(10L, vo.getConfId());
            assertEquals(20L, vo.getNodeId());
            assertTrue(vo.getIsInuse());
            assertEquals(30L, vo.getTemplateId());
            assertEquals("Tmpl", vo.getTemplateName());
            assertEquals("1,2,3", vo.getDays());
            assertEquals(Arrays.asList(1, 2, 3), vo.getDayList());
            assertEquals(0, vo.getIsDel());
        }
    }

    @Nested
    @DisplayName("BpmnTimeoutReminderTaskVo")
    class BpmnTimeoutReminderTaskVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BpmnTimeoutReminderTaskVo vo = new BpmnTimeoutReminderTaskVo();
            assertNull(vo.getProcInstId());
            assertNull(vo.getTaskId());
            assertNull(vo.getElementId());
            assertNull(vo.getAssignee());
            assertNull(vo.getCreateTime());
            assertNull(vo.getStandbyDay());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            Date now = new Date();
            BpmnTimeoutReminderTaskVo vo = BpmnTimeoutReminderTaskVo.builder()
                    .procInstId("pi1")
                    .taskId("t1")
                    .elementId("e1")
                    .assignee("a1")
                    .createTime(now)
                    .standbyDay(3)
                    .build();
            assertEquals("pi1", vo.getProcInstId());
            assertEquals("t1", vo.getTaskId());
            assertEquals("e1", vo.getElementId());
            assertEquals("a1", vo.getAssignee());
            assertEquals(now, vo.getCreateTime());
            assertEquals(3, vo.getStandbyDay());
        }
    }
}
