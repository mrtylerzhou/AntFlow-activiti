package org.openoa.engine.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EngineVoRound20Test extends BaseTest {

    @Nested
    @DisplayName("GenericEmployee")
    class GenericEmployeeTest {

        @Test
        @DisplayName("no-arg constructor - Builder.Default values")
        void noArgDefaults() {
            GenericEmployee emp = new GenericEmployee();
            assertNull(emp.getUserId());
            assertEquals("", emp.getUsername());
            assertEquals("", emp.getGivenName());
            assertNull(emp.getJobNum());
            assertNull(emp.getJobName());
            assertNull(emp.getJobLevelName());
            assertNull(emp.getPhotoPath());
            assertNull(emp.getHeadImg());
            assertNull(emp.getMail());
            assertNull(emp.getMobile());
            assertNull(emp.getIsMaster());
            assertNull(emp.getCompanyId());
            assertNull(emp.getDirectLeader());
            assertNull(emp.getPath());
            assertNotNull(emp.getPermissions());
            assertTrue(emp.getPermissions().contains("3060101"));
        }

        @Test
        @DisplayName("builder pattern with defaults")
        void builder() {
            GenericEmployee leader = GenericEmployee.builder()
                    .userId("L001")
                    .givenName("Boss")
                    .build();
            GenericEmployee emp = GenericEmployee.builder()
                    .userId("U001")
                    .username("zhangsan")
                    .givenName("Zhang San")
                    .jobNum("J001")
                    .jobName("Engineer")
                    .jobLevelName("P5")
                    .mail("z@test.com")
                    .mobile("13800001111")
                    .isMaster(false)
                    .companyId(1L)
                    .directLeader(leader)
                    .path("/root/dept1")
                    .build();
            assertEquals("U001", emp.getUserId());
            assertEquals("zhangsan", emp.getUsername());
            assertEquals("Zhang San", emp.getGivenName());
            assertEquals("J001", emp.getJobNum());
            assertEquals("Engineer", emp.getJobName());
            assertEquals("P5", emp.getJobLevelName());
            assertEquals("z@test.com", emp.getMail());
            assertEquals("13800001111", emp.getMobile());
            assertFalse(emp.getIsMaster());
            assertEquals(1L, emp.getCompanyId());
            assertNotNull(emp.getDirectLeader());
            assertEquals("L001", emp.getDirectLeader().getUserId());
            assertEquals("/root/dept1", emp.getPath());
        }

        @Test
        @DisplayName("permissions set is mutable")
        void permissionsMutable() {
            GenericEmployee emp = new GenericEmployee();
            emp.getPermissions().add("custom_perm");
            assertTrue(emp.getPermissions().contains("custom_perm"));
            assertTrue(emp.getPermissions().contains("3060101"));
        }
    }

    @Nested
    @DisplayName("PsPreCheckVO")
    class PsPreCheckVOTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            PsPreCheckVO vo = new PsPreCheckVO();
            assertNull(vo.getFormCode());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            PsPreCheckVO vo = new PsPreCheckVO();
            vo.setFormCode("FC001");
            assertEquals("FC001", vo.getFormCode());
        }
    }

    @Nested
    @DisplayName("PsPreRespVO")
    class PsPreRespVOTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            PsPreRespVO vo = new PsPreRespVO();
            assertNull(vo.getStartUserChooseNodes());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            PsPreRespVO vo = new PsPreRespVO();
            vo.setStartUserChooseNodes(Arrays.asList(
                    new org.openoa.base.vo.ProcessNodeVo(1L, "sid-A", "Approve")
            ));
            assertNotNull(vo.getStartUserChooseNodes());
            assertEquals(1, vo.getStartUserChooseNodes().size());
        }
    }

    @Nested
    @DisplayName("NodeRolePersonVo")
    class NodeRolePersonVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            NodeRolePersonVo vo = new NodeRolePersonVo();
            assertNull(vo.getRoleId());
            assertNull(vo.getRoleName());
            assertNull(vo.getUserList());
        }

        @Test
        @DisplayName("all-arg constructor")
        void allArgConstructor() {
            BaseIdTranStruVo user = new BaseIdTranStruVo();
            NodeRolePersonVo vo = new NodeRolePersonVo("R001", "Admin", Arrays.asList(user));
            assertEquals("R001", vo.getRoleId());
            assertEquals("Admin", vo.getRoleName());
            assertEquals(1, vo.getUserList().size());
        }
    }

    @Nested
    @DisplayName("AppVersionVo")
    class AppVersionVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            AppVersionVo vo = new AppVersionVo();
            assertNull(vo.getId());
            assertNull(vo.getCurVersion());
            assertNull(vo.getVersion());
            assertNull(vo.getIsLatest());
            assertNull(vo.getIsForce());
            assertNull(vo.getDownloadUrl());
            assertNull(vo.getDescription());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            AppVersionVo vo = AppVersionVo.builder()
                    .id(1L)
                    .curVersion("1.0.0")
                    .version("2.0.0")
                    .isLatest(1)
                    .isForce(0)
                    .downloadUrl("http://x/app.apk")
                    .description("New version")
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals("1.0.0", vo.getCurVersion());
            assertEquals("2.0.0", vo.getVersion());
            assertEquals(1, vo.getIsLatest());
            assertEquals(0, vo.getIsForce());
            assertEquals("http://x/app.apk", vo.getDownloadUrl());
            assertEquals("New version", vo.getDescription());
        }
    }

    @Nested
    @DisplayName("DataAccessVo")
    class DataAccessVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            DataAccessVo vo = new DataAccessVo();
            assertNull(vo.getColumns());
            assertNull(vo.getDepartmentIds());
            assertNull(vo.getJoinColumns());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            DataAccessVo vo = new DataAccessVo();
            vo.setColumns("id,name");
            vo.setDepartmentIds(Arrays.asList(1, 2, 3));
            vo.setJoinColumns("dept_id");
            assertEquals("id,name", vo.getColumns());
            assertEquals(3, vo.getDepartmentIds().size());
            assertEquals("dept_id", vo.getJoinColumns());
        }
    }

    @Nested
    @DisplayName("ProcessTypeInforVo")
    class ProcessTypeInforVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            ProcessTypeInforVo vo = new ProcessTypeInforVo();
            assertNull(vo.getProcessTypeName());
            assertNull(vo.getApplicationList());
            assertNull(vo.getIconList());
            assertNull(vo.getCommonFunction());
            assertNull(vo.getTypeId());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            ProcessTypeInforVo vo = new ProcessTypeInforVo();
            vo.setProcessTypeName("Leave");
            vo.setTypeId(1);
            assertEquals("Leave", vo.getProcessTypeName());
            assertEquals(1, vo.getTypeId());
        }
    }

    @Nested
    @DisplayName("IconInforVo")
    class IconInforVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            IconInforVo vo = new IconInforVo();
            assertNull(vo.getCommonFunction());
            assertNull(vo.getApplicationList());
            assertNull(vo.getSonApplicationList());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            IconInforVo vo = new IconInforVo();
            ProcessTypeInforVo ptVo = new ProcessTypeInforVo();
            ptVo.setProcessTypeName("Leave");
            vo.setCommonFunction(ptVo);
            vo.setApplicationList(Arrays.asList(ptVo));
            vo.setSonApplicationList(ptVo);
            assertNotNull(vo.getCommonFunction());
            assertEquals("Leave", vo.getCommonFunction().getProcessTypeName());
            assertEquals(1, vo.getApplicationList().size());
            assertNotNull(vo.getSonApplicationList());
        }
    }
}
