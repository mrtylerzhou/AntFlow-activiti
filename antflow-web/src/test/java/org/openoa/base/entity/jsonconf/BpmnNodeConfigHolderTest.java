package org.openoa.base.entity.jsonconf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeButtonConfBaseVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeConfigHolderTest extends BaseTest {

    private BpmnNodeVo createBasicVo() {
        BpmnNodeVo vo = new BpmnNodeVo();
        vo.setProperty(new BpmnNodePropertysVo());
        return vo;
    }

    @Nested
    @DisplayName("setPersonnelConf")
    class SetPersonnelConfTest {
        @Test
        @DisplayName("should do nothing when property is null")
        void nullProperty() {
            BpmnNodeVo vo = new BpmnNodeVo();
            BpmnNodeConfigHolder.setPersonnelConf(vo);
            BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
            assertNull(config.getApproverConf());
        }

        @Test
        @DisplayName("should create personnelConf with emplIds only")
        void emplIdsOnly() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setEmplIds(Arrays.asList("E001", "E002"));
            BpmnNodeConfigHolder.setPersonnelConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertNotNull(conf.getPersonnelConf());
            assertEquals(2, conf.getPersonnelConf().getEmployees().size());
            assertEquals("E001", conf.getPersonnelConf().getEmployees().get(0).getEmplId());
            assertNull(conf.getPersonnelConf().getEmployees().get(0).getEmplName());
        }

        @Test
        @DisplayName("should create personnelConf with emplList (names included)")
        void emplListWithNames() {
            BpmnNodeVo vo = createBasicVo();
            BaseIdTranStruVo emp1 = BaseIdTranStruVo.builder().id("E001").name("Alice").build();
            BaseIdTranStruVo emp2 = BaseIdTranStruVo.builder().id("E002").name("Bob").build();
            vo.getProperty().setEmplIds(Arrays.asList("E001", "E002"));
            vo.getProperty().setEmplList(Arrays.asList(emp1, emp2));
            BpmnNodeConfigHolder.setPersonnelConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertEquals(2, conf.getPersonnelConf().getEmployees().size());
            assertEquals("E001", conf.getPersonnelConf().getEmployees().get(0).getEmplId());
            assertEquals("Alice", conf.getPersonnelConf().getEmployees().get(0).getEmplName());
        }

        @Test
        @DisplayName("should set signType from property")
        void signType() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setEmplIds(Arrays.asList("E001"));
            vo.getProperty().setSignType(2);
            BpmnNodeConfigHolder.setPersonnelConf(vo);
            assertEquals(2, vo.getOrCreateNodeConfigJson().getApproverConf().getPersonnelConf().getSignType());
        }

        @Test
        @DisplayName("should create empty employees when emplIds is empty")
        void emptyEmplIds() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setEmplIds(Arrays.asList());
            BpmnNodeConfigHolder.setPersonnelConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertNotNull(conf.getPersonnelConf());
            assertTrue(conf.getPersonnelConf().getEmployees().isEmpty());
        }
    }

    @Nested
    @DisplayName("setRoleConf")
    class SetRoleConfTest {
        @Test
        @DisplayName("should do nothing when roleList is empty")
        void emptyRoleList() {
            BpmnNodeVo vo = createBasicVo();
            BpmnNodeConfigHolder.setRoleConf(vo);
            assertNull(vo.getOrCreateNodeConfigJson().getApproverConf());
        }

        @Test
        @DisplayName("should create roleConfList from roleList")
        void createRoleConfList() {
            BpmnNodeVo vo = createBasicVo();
            BaseIdTranStruVo role = BaseIdTranStruVo.builder().id("R001").name("Admin").build();
            vo.getProperty().setRoleList(Arrays.asList(role));
            vo.getProperty().setSignType(1);
            BpmnNodeConfigHolder.setRoleConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertNotNull(conf.getRoleConfList());
            assertEquals(1, conf.getRoleConfList().size());
            assertEquals("R001", conf.getRoleConfList().get(0).getRoleId());
            assertEquals("Admin", conf.getRoleConfList().get(0).getRoleName());
            assertEquals(1, conf.getRoleConfList().get(0).getSignType());
        }

        @Test
        @DisplayName("should add outsideEmployees when isOutSideProcess is 1")
        void outsideEmployees() {
            BpmnNodeVo vo = createBasicVo();
            BaseIdTranStruVo role = BaseIdTranStruVo.builder().id("R001").name("Admin").build();
            BaseIdTranStruVo emp = BaseIdTranStruVo.builder().id("E001").name("Alice").build();
            vo.getProperty().setRoleList(Arrays.asList(role));
            vo.getProperty().setEmplList(Arrays.asList(emp));
            vo.setIsOutSideProcess(1);
            BpmnNodeConfigHolder.setRoleConf(vo);
            BpmnNodeApproverConfJson.RoleConf rc = vo.getOrCreateNodeConfigJson().getApproverConf().getRoleConfList().get(0);
            assertNotNull(rc.getOutsideEmployees());
            assertEquals(1, rc.getOutsideEmployees().size());
            assertEquals("E001", rc.getOutsideEmployees().get(0).getEmplId());
        }

        @Test
        @DisplayName("should not add outsideEmployees when isOutSideProcess is 0")
        void noOutsideEmployees() {
            BpmnNodeVo vo = createBasicVo();
            BaseIdTranStruVo role = BaseIdTranStruVo.builder().id("R001").name("Admin").build();
            vo.getProperty().setRoleList(Arrays.asList(role));
            vo.setIsOutSideProcess(0);
            BpmnNodeConfigHolder.setRoleConf(vo);
            BpmnNodeApproverConfJson.RoleConf rc = vo.getOrCreateNodeConfigJson().getApproverConf().getRoleConfList().get(0);
            assertNull(rc.getOutsideEmployees());
        }
    }

    @Nested
    @DisplayName("setLoopConf")
    class SetLoopConfTest {
        @Test
        @DisplayName("should create loopConf from property")
        void createLoopConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setLoopEndType(1);
            vo.getProperty().setLoopNumberPlies(3);
            vo.getProperty().setLoopEndGrade(5);
            BpmnNodeConfigHolder.setLoopConf(vo);
            BpmnNodeApproverConfJson.LoopConf lc = vo.getOrCreateNodeConfigJson().getApproverConf().getLoopConf();
            assertNotNull(lc);
            assertEquals(1, lc.getLoopEndType());
            assertEquals(3, lc.getLoopNumberPlies());
            assertEquals(5, lc.getLoopEndGrade());
        }

        @Test
        @DisplayName("should do nothing when property is null")
        void nullProperty() {
            BpmnNodeVo vo = new BpmnNodeVo();
            BpmnNodeConfigHolder.setLoopConf(vo);
            assertNull(vo.getOrCreateNodeConfigJson().getApproverConf());
        }
    }

    @Nested
    @DisplayName("setAssignLevelConf")
    class SetAssignLevelConfTest {
        @Test
        @DisplayName("should create assignLevelConf")
        void createAssignLevelConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setAssignLevelType(2);
            vo.getProperty().setAssignLevelGrade(3);
            BpmnNodeConfigHolder.setAssignLevelConf(vo);
            BpmnNodeApproverConfJson.AssignLevelConf alc = vo.getOrCreateNodeConfigJson().getApproverConf().getAssignLevelConf();
            assertNotNull(alc);
            assertEquals(2, alc.getAssignLevelType());
            assertEquals(3, alc.getAssignLevelGrade());
        }
    }

    @Nested
    @DisplayName("setHrbpConf")
    class SetHrbpConfTest {
        @Test
        @DisplayName("should create hrbpConf")
        void createHrbpConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setHrbpConfType(1);
            BpmnNodeConfigHolder.setHrbpConf(vo);
            BpmnNodeApproverConfJson.HrbpConf hc = vo.getOrCreateNodeConfigJson().getApproverConf().getHrbpConf();
            assertNotNull(hc);
            assertEquals(1, hc.getHrbpConfType());
        }
    }

    @Nested
    @DisplayName("setCustomizeConf")
    class SetCustomizeConfTest {
        @Test
        @DisplayName("should create customizeConf")
        void createCustomizeConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setSignType(3);
            BpmnNodeConfigHolder.setCustomizeConf(vo);
            BpmnNodeApproverConfJson.CustomizeConf cc = vo.getOrCreateNodeConfigJson().getApproverConf().getCustomizeConf();
            assertNotNull(cc);
            assertEquals(3, cc.getSignType());
        }
    }

    @Nested
    @DisplayName("setUdrConf")
    class SetUdrConfTest {
        @Test
        @DisplayName("should create udrConf and append to list")
        void createUdrConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setUdrValueJson("{\"key\":\"val\"}");
            vo.getProperty().setSignType(1);
            BaseIdTranStruVo udrProp = BaseIdTranStruVo.builder().id("PROP1").name("Property1").build();
            vo.getProperty().setUdrAssigneeProperty(udrProp);
            BpmnNodeConfigHolder.setUdrConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertNotNull(conf.getUdrConfList());
            assertEquals(1, conf.getUdrConfList().size());
            assertEquals("{\"key\":\"val\"}", conf.getUdrConfList().get(0).getValueJson());
            assertEquals("PROP1", conf.getUdrConfList().get(0).getUdrProperty());
            assertEquals("Property1", conf.getUdrConfList().get(0).getUdrPropertyName());
        }

        @Test
        @DisplayName("should append multiple UDR configs")
        void appendMultiple() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setSignType(1);
            BpmnNodeConfigHolder.setUdrConf(vo);
            BpmnNodeConfigHolder.setUdrConf(vo);
            assertEquals(2, vo.getOrCreateNodeConfigJson().getApproverConf().getUdrConfList().size());
        }

        @Test
        @DisplayName("should handle null udrAssigneeProperty")
        void nullUdrProperty() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setSignType(1);
            BpmnNodeConfigHolder.setUdrConf(vo);
            BpmnNodeApproverConfJson.UDRConf udr = vo.getOrCreateNodeConfigJson().getApproverConf().getUdrConfList().get(0);
            assertNull(udr.getUdrProperty());
            assertNull(udr.getUdrPropertyName());
        }
    }

    @Nested
    @DisplayName("setOutSideAccessConf")
    class SetOutSideAccessConfTest {
        @Test
        @DisplayName("should create outSideAccessConf")
        void createOutSideAccessConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setNodeMark("MARK01");
            vo.getProperty().setSignType(2);
            BpmnNodeConfigHolder.setOutSideAccessConf(vo);
            BpmnNodeApproverConfJson.OutSideAccessConf oac = vo.getOrCreateNodeConfigJson().getApproverConf().getOutSideAccessConf();
            assertNotNull(oac);
            assertEquals("MARK01", oac.getNodeMark());
            assertEquals(2, oac.getSignType());
        }
    }

    @Nested
    @DisplayName("setBusinessTableConf")
    class SetBusinessTableConfTest {
        @Test
        @DisplayName("should create businessTableConf")
        void createBusinessTableConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setConfigurationTableType(1);
            vo.getProperty().setTableFieldType(2);
            vo.getProperty().setSignType(1);
            BpmnNodeConfigHolder.setBusinessTableConf(vo);
            BpmnNodeApproverConfJson.BusinessTableConf btc = vo.getOrCreateNodeConfigJson().getApproverConf().getBusinessTableConf();
            assertNotNull(btc);
            assertEquals(1, btc.getConfigurationTableType());
            assertEquals(2, btc.getTableFieldType());
            assertEquals(1, btc.getSignType());
        }
    }

    @Nested
    @DisplayName("setButtonSignConf")
    class SetButtonSignConfTest {
        @Test
        @DisplayName("should create buttonSignConf with buttons")
        void withButtons() {
            BpmnNodeVo vo = createBasicVo();
            BpmnNodeButtonConfBaseVo btns = BpmnNodeButtonConfBaseVo.builder()
                    .startPage(Arrays.asList(1, 3))
                    .approvalPage(Arrays.asList(4))
                    .viewPage(Arrays.asList(0))
                    .build();
            vo.setButtons(btns);
            BpmnNodeConfigHolder.setButtonSignConf(vo);
            BpmnNodeButtonSignConfJson bs = vo.getOrCreateNodeConfigJson().getButtonSignConf();
            assertNotNull(bs);
            assertEquals(4, bs.getButtonConfList().size());
            assertEquals(1, bs.getButtonConfList().get(0).getButtonPageType());
            assertEquals(1, bs.getButtonConfList().get(0).getButtonType());
            assertEquals(2, bs.getButtonConfList().get(2).getButtonPageType());
            assertEquals(3, bs.getButtonConfList().get(3).getButtonPageType());
        }

        @Test
        @DisplayName("should handle null buttons")
        void nullButtons() {
            BpmnNodeVo vo = createBasicVo();
            BpmnNodeConfigHolder.setButtonSignConf(vo);
            BpmnNodeButtonSignConfJson bs = vo.getOrCreateNodeConfigJson().getButtonSignConf();
            assertNotNull(bs);
            assertNull(bs.getButtonConfList());
        }

        @Test
        @DisplayName("should set signUpConf when isSignUp is 1")
        void signUpConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.setIsSignUp(1);
            vo.getProperty().setAfterSignUpWay(2);
            vo.getProperty().setSignUpType(1);
            BpmnNodeConfigHolder.setButtonSignConf(vo);
            BpmnNodeButtonSignConfJson bs = vo.getOrCreateNodeConfigJson().getButtonSignConf();
            assertNotNull(bs.getSignUpConf());
            assertEquals(2, bs.getSignUpConf().getAfterSignUpWay());
            assertEquals(1, bs.getSignUpConf().getSignUpType());
        }

        @Test
        @DisplayName("should not set signUpConf when isSignUp is 0")
        void noSignUpConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.setIsSignUp(0);
            BpmnNodeConfigHolder.setButtonSignConf(vo);
            BpmnNodeButtonSignConfJson bs = vo.getOrCreateNodeConfigJson().getButtonSignConf();
            assertNull(bs.getSignUpConf());
        }

        @Test
        @DisplayName("should set labels from labelList")
        void labels() {
            BpmnNodeVo vo = createBasicVo();
            vo.setLabelList(Arrays.asList(
                    new BpmnNodeLabelVO("val1", "name1"),
                    new BpmnNodeLabelVO("val2", "name2")));
            BpmnNodeConfigHolder.setButtonSignConf(vo);
            BpmnNodeButtonSignConfJson bs = vo.getOrCreateNodeConfigJson().getButtonSignConf();
            assertNotNull(bs.getLabels());
            assertEquals(2, bs.getLabels().size());
            assertEquals("val1", bs.getLabels().get(0).getLabelValue());
            assertEquals("name1", bs.getLabels().get(0).getLabelName());
        }
    }

    @Nested
    @DisplayName("setConditionsConf")
    class SetConditionsConfTest {
        @Test
        @DisplayName("should set conditionsConf on node config")
        void setConditionsConf() {
            BpmnNodeVo vo = createBasicVo();
            BpmnNodeConditionsConfJson.ConditionGroup group = BpmnNodeConditionsConfJson.ConditionGroup.builder()
                    .isDefault(1)
                    .groupRelation(0)
                    .sort(0)
                    .build();
            BpmnNodeConfigHolder.setConditionsConf(vo, Arrays.asList(group), "OUT-001");
            BpmnNodeConditionsConfJson cc = vo.getOrCreateNodeConfigJson().getConditionsConf();
            assertNotNull(cc);
            assertEquals(1, cc.getConditionGroups().size());
            assertEquals(1, cc.getConditionGroups().get(0).getIsDefault().intValue());
            assertEquals("OUT-001", cc.getOutSideConditionId());
        }
    }

    @Nested
    @DisplayName("setLowCodeConf")
    class SetLowCodeConfTest {
        @Test
        @DisplayName("should do nothing when lfFieldControlVOs is empty")
        void emptyFieldControls() {
            BpmnNodeVo vo = createBasicVo();
            BpmnNodeConfigHolder.setLowCodeConf(vo);
            assertNull(vo.getOrCreateNodeConfigJson().getLowCodeConf());
        }
    }

    @Nested
    @DisplayName("getOrCreateApproverConf (lazy creation)")
    class LazyCreationTest {
        @Test
        @DisplayName("should create approverConf on first access")
        void firstAccess() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setSignType(1);
            BpmnNodeConfigHolder.setHrbpConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertNotNull(conf);
            assertNotNull(conf.getHrbpConf());
        }

        @Test
        @DisplayName("should reuse same approverConf across multiple calls")
        void reuseApproverConf() {
            BpmnNodeVo vo = createBasicVo();
            vo.getProperty().setSignType(1);
            vo.getProperty().setHrbpConfType(1);
            BpmnNodeConfigHolder.setHrbpConf(vo);
            BpmnNodeConfigHolder.setCustomizeConf(vo);
            BpmnNodeApproverConfJson conf = vo.getOrCreateNodeConfigJson().getApproverConf();
            assertNotNull(conf.getHrbpConf());
            assertNotNull(conf.getCustomizeConf());
        }
    }
}
