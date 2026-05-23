package org.openoa.base.entity.jsonconf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeApproverConfJsonTest extends BaseTest {

    @Nested
    @DisplayName("PersonnelConf")
    class PersonnelConfTest {
        @Test
        @DisplayName("should build with signType and employees")
        void buildWithEmployees() {
            BpmnNodeApproverConfJson.EmployeeInfo emp = BpmnNodeApproverConfJson.EmployeeInfo.builder()
                    .emplId("E001").emplName("Alice").build();
            BpmnNodeApproverConfJson.PersonnelConf conf = BpmnNodeApproverConfJson.PersonnelConf.builder()
                    .signType(1).employees(Arrays.asList(emp)).build();
            assertEquals(1, conf.getSignType());
            assertEquals(1, conf.getEmployees().size());
            assertEquals("E001", conf.getEmployees().get(0).getEmplId());
            assertEquals("Alice", conf.getEmployees().get(0).getEmplName());
        }
    }

    @Nested
    @DisplayName("RoleConf")
    class RoleConfTest {
        @Test
        @DisplayName("should build with outsideEmployees")
        void buildWithOutsideEmployees() {
            BpmnNodeApproverConfJson.EmployeeInfo emp = BpmnNodeApproverConfJson.EmployeeInfo.builder()
                    .emplId("E001").emplName("Bob").build();
            BpmnNodeApproverConfJson.RoleConf rc = BpmnNodeApproverConfJson.RoleConf.builder()
                    .roleId("R001").roleName("Admin").signType(2).outsideEmployees(Arrays.asList(emp)).build();
            assertEquals("R001", rc.getRoleId());
            assertEquals("Admin", rc.getRoleName());
            assertEquals(2, rc.getSignType());
            assertNotNull(rc.getOutsideEmployees());
            assertEquals(1, rc.getOutsideEmployees().size());
        }
    }

    @Nested
    @DisplayName("LoopConf")
    class LoopConfTest {
        @Test
        @DisplayName("should build all fields")
        void buildAllFields() {
            BpmnNodeApproverConfJson.LoopConf lc = BpmnNodeApproverConfJson.LoopConf.builder()
                    .loopEndType(1).loopNumberPlies(3).loopEndPerson("P001,P002")
                    .noparticipatingStaffIds("S001").loopEndGrade(5).build();
            assertEquals(1, lc.getLoopEndType());
            assertEquals(3, lc.getLoopNumberPlies());
            assertEquals("P001,P002", lc.getLoopEndPerson());
            assertEquals("S001", lc.getNoparticipatingStaffIds());
            assertEquals(5, lc.getLoopEndGrade());
        }
    }

    @Nested
    @DisplayName("AssignLevelConf")
    class AssignLevelConfTest {
        @Test
        @DisplayName("should build with type and grade")
        void buildWithTypeAndGrade() {
            BpmnNodeApproverConfJson.AssignLevelConf alc = BpmnNodeApproverConfJson.AssignLevelConf.builder()
                    .assignLevelType(1).assignLevelGrade(3).build();
            assertEquals(1, alc.getAssignLevelType());
            assertEquals(3, alc.getAssignLevelGrade());
        }
    }

    @Nested
    @DisplayName("HrbpConf")
    class HrbpConfTest {
        @Test
        @DisplayName("should build with hrbpConfType")
        void buildWithConfType() {
            BpmnNodeApproverConfJson.HrbpConf hc = BpmnNodeApproverConfJson.HrbpConf.builder()
                    .hrbpConfType(2).build();
            assertEquals(2, hc.getHrbpConfType());
        }
    }

    @Nested
    @DisplayName("UDRConf")
    class UDRConfTest {
        @Test
        @DisplayName("should build with all fields including ext1-ext4")
        void buildWithAllFields() {
            BpmnNodeApproverConfJson.UDRConf udr = BpmnNodeApproverConfJson.UDRConf.builder()
                    .valueJson("{\"k\":\"v\"}").signType(1).udrProperty("P1")
                    .udrPropertyName("Prop1").ext1("e1").ext2("e2").ext3("e3").ext4("e4").build();
            assertEquals("{\"k\":\"v\"}", udr.getValueJson());
            assertEquals(1, udr.getSignType());
            assertEquals("P1", udr.getUdrProperty());
            assertEquals("Prop1", udr.getUdrPropertyName());
            assertEquals("e1", udr.getExt1());
            assertEquals("e2", udr.getExt2());
            assertEquals("e3", udr.getExt3());
            assertEquals("e4", udr.getExt4());
        }
    }

    @Nested
    @DisplayName("FormRelatedUserConf")
    class FormRelatedUserConfTest {
        @Test
        @DisplayName("should build with valueJson and valueType")
        void buildWithValueJson() {
            BpmnNodeApproverConfJson.FormRelatedUserConf fr = BpmnNodeApproverConfJson.FormRelatedUserConf.builder()
                    .valueJson("[{\"id\":1}]").signType(2).valueType(1).valueTypeName("表单人员").build();
            assertEquals("[{\"id\":1}]", fr.getValueJson());
            assertEquals(2, fr.getSignType());
            assertEquals(1, fr.getValueType());
            assertEquals("表单人员", fr.getValueTypeName());
        }
    }

    @Nested
    @DisplayName("OutSideAccessConf")
    class OutSideAccessConfTest {
        @Test
        @DisplayName("should build with nodeMark and signType")
        void buildWithNodeMark() {
            BpmnNodeApproverConfJson.OutSideAccessConf oac = BpmnNodeApproverConfJson.OutSideAccessConf.builder()
                    .nodeMark("MARK01").signType(1).build();
            assertEquals("MARK01", oac.getNodeMark());
            assertEquals(1, oac.getSignType());
        }
    }

    @Nested
    @DisplayName("BusinessTableConf")
    class BusinessTableConfTest {
        @Test
        @DisplayName("should build with configurationTableType and tableFieldType")
        void buildWithTableTypes() {
            BpmnNodeApproverConfJson.BusinessTableConf btc = BpmnNodeApproverConfJson.BusinessTableConf.builder()
                    .configurationTableType(1).tableFieldType(2).signType(3).build();
            assertEquals(1, btc.getConfigurationTableType());
            assertEquals(2, btc.getTableFieldType());
            assertEquals(3, btc.getSignType());
        }
    }

    @Nested
    @DisplayName("top-level BpmnNodeApproverConfJson")
    class TopLevelTest {
        @Test
        @DisplayName("should build with all conf sections")
        void buildWithAllSections() {
            BpmnNodeApproverConfJson json = BpmnNodeApproverConfJson.builder()
                    .personnelConf(BpmnNodeApproverConfJson.PersonnelConf.builder().signType(1).build())
                    .loopConf(BpmnNodeApproverConfJson.LoopConf.builder().loopEndType(2).build())
                    .hrbpConf(BpmnNodeApproverConfJson.HrbpConf.builder().hrbpConfType(1).build())
                    .build();
            assertNotNull(json.getPersonnelConf());
            assertNotNull(json.getLoopConf());
            assertNotNull(json.getHrbpConf());
            assertNull(json.getRoleConfList());
            assertNull(json.getUdrConfList());
        }

        @Test
        @DisplayName("no-arg constructor should have null fields")
        void noArgConstructor() {
            BpmnNodeApproverConfJson json = new BpmnNodeApproverConfJson();
            assertNull(json.getPersonnelConf());
            assertNull(json.getRoleConfList());
            assertNull(json.getLoopConf());
            assertNull(json.getAssignLevelConf());
            assertNull(json.getHrbpConf());
            assertNull(json.getCustomizeConf());
            assertNull(json.getUdrConfList());
            assertNull(json.getFormRelatedUserConfList());
            assertNull(json.getOutSideAccessConf());
            assertNull(json.getBusinessTableConf());
        }
    }
}
