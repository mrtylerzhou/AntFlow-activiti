package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BpmnTemplateVoTest extends BaseTest {

    @Nested
    @DisplayName("setEmpList")
    class SetEmpListTest {
        @Test
        @DisplayName("should set empList and derive empIdList")
        void shouldDeriveEmpIdList() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            BaseIdTranStruVo emp1 = BaseIdTranStruVo.builder().id("E001").name("Alice").build();
            BaseIdTranStruVo emp2 = BaseIdTranStruVo.builder().id("E002").name("Bob").build();
            vo.setEmpList(Arrays.asList(emp1, emp2));
            assertEquals(2, vo.getEmpIdList().size());
            assertEquals("E001", vo.getEmpIdList().get(0));
            assertEquals("E002", vo.getEmpIdList().get(1));
        }

        @Test
        @DisplayName("should set empList to null without NPE")
        void nullEmpList() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setEmpList(null);
            assertNull(vo.getEmpIdList());
        }

        @Test
        @DisplayName("should set empList to empty list without NPE")
        void emptyEmpList() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setEmpList(Arrays.asList());
            assertNull(vo.getEmpIdList());
        }
    }

    @Nested
    @DisplayName("setRoleList")
    class SetRoleListTest {
        @Test
        @DisplayName("should set roleList and derive roleIdList")
        void shouldDeriveRoleIdList() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            BaseIdTranStruVo role1 = BaseIdTranStruVo.builder().id("R001").name("Admin").build();
            BaseIdTranStruVo role2 = BaseIdTranStruVo.builder().id("R002").name("User").build();
            vo.setRoleList(Arrays.asList(role1, role2));
            assertEquals(2, vo.getRoleIdList().size());
            assertEquals("R001", vo.getRoleIdList().get(0));
            assertEquals("R002", vo.getRoleIdList().get(1));
        }

        @Test
        @DisplayName("should set roleList to null without NPE")
        void nullRoleList() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setRoleList(null);
            assertNull(vo.getRoleIdList());
        }

        @Test
        @DisplayName("should set roleList to empty list without NPE")
        void emptyRoleList() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setRoleList(Arrays.asList());
            assertNull(vo.getRoleIdList());
        }
    }

    @Nested
    @DisplayName("builder")
    class BuilderTest {
        @Test
        @DisplayName("should build with all fields")
        void shouldBuildWithAllFields() {
            BpmnTemplateVo vo = BpmnTemplateVo.builder()
                    .id(1L)
                    .confId(10L)
                    .nodeId(100L)
                    .event(1)
                    .eventValue("submit")
                    .templateId(999L)
                    .templateName("TestTemplate")
                    .formCode("FORM001")
                    .isDel(0)
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals(10L, vo.getConfId());
            assertEquals(100L, vo.getNodeId());
            assertEquals(1, vo.getEvent());
            assertEquals("submit", vo.getEventValue());
            assertEquals(999L, vo.getTemplateId());
            assertEquals("TestTemplate", vo.getTemplateName());
            assertEquals("FORM001", vo.getFormCode());
            assertEquals(0, vo.getIsDel());
        }
    }

    @Nested
    @DisplayName("no-arg constructor")
    class NoArgConstructorTest {
        @Test
        @DisplayName("should create instance with null fields")
        void shouldCreateWithNullFields() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            assertNull(vo.getId());
            assertNull(vo.getEmpList());
            assertNull(vo.getEmpIdList());
            assertNull(vo.getRoleList());
            assertNull(vo.getRoleIdList());
        }
    }
}
