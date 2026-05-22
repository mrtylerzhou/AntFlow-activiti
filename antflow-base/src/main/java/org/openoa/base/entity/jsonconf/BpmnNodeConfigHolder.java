package org.openoa.base.entity.jsonconf;

import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.vo.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper for building BpmnNodeConfigJson from BpmnNodeVo data during edit flow.
 * Each adaptor calls the appropriate static method to populate its section.
 */
public class BpmnNodeConfigHolder {

    private BpmnNodeConfigHolder() {
    }

    /**
     * Build personnel approver config from node VO
     */
    public static void setPersonnelConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        List<BpmnNodeApproverConfJson.EmployeeInfo> employees = new ArrayList<>();
        if (!CollectionUtils.isEmpty(prop.getEmplIds())) {
            List<String> emplIds = prop.getEmplIds();
            List<BaseIdTranStruVo> emplList = prop.getEmplList();
            if (!CollectionUtils.isEmpty(emplList)) {
                for (BaseIdTranStruVo e : emplList) {
                    employees.add(BpmnNodeApproverConfJson.EmployeeInfo.builder()
                            .emplId(e.getId()).emplName(e.getName()).build());
                }
            } else {
                for (String id : emplIds) {
                    employees.add(BpmnNodeApproverConfJson.EmployeeInfo.builder()
                            .emplId(id).build());
                }
            }
        }
        approverConf.setPersonnelConf(BpmnNodeApproverConfJson.PersonnelConf.builder()
                .signType(prop.getSignType())
                .employees(employees)
                .build());
    }

    /**
     * Build role approver config from node VO
     */
    public static void setRoleConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null || CollectionUtils.isEmpty(prop.getRoleList())) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        List<BpmnNodeApproverConfJson.RoleConf> roleConfList = new ArrayList<>();
        for (BaseIdTranStruVo role : prop.getRoleList()) {
            BpmnNodeApproverConfJson.RoleConf rc = BpmnNodeApproverConfJson.RoleConf.builder()
                    .roleId(role.getId())
                    .roleName(role.getName())
                    .signType(prop.getSignType())
                    .build();
            if (vo.getIsOutSideProcess() != null && vo.getIsOutSideProcess() == 1
                    && !CollectionUtils.isEmpty(prop.getEmplList())) {
                rc.setOutsideEmployees(prop.getEmplList().stream()
                        .map(e -> BpmnNodeApproverConfJson.EmployeeInfo.builder()
                                .emplId(e.getId()).emplName(e.getName()).build())
                        .collect(Collectors.toList()));
            }
            roleConfList.add(rc);
        }
        approverConf.setRoleConfList(roleConfList);
    }

    /**
     * Build loop approver config from node VO
     */
    public static void setLoopConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        approverConf.setLoopConf(BpmnNodeApproverConfJson.LoopConf.builder()
                .loopEndType(prop.getLoopEndType())
                .loopNumberPlies(prop.getLoopNumberPlies())
                .loopEndPerson(joinList(prop.getLoopEndPersonList()))
                .noparticipatingStaffIds(joinList(prop.getNoparticipatingStaffIds()))
                .loopEndGrade(prop.getLoopEndGrade())
                .build());
    }

    /**
     * Build assign level approver config from node VO
     */
    public static void setAssignLevelConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        approverConf.setAssignLevelConf(BpmnNodeApproverConfJson.AssignLevelConf.builder()
                .assignLevelType(prop.getAssignLevelType())
                .assignLevelGrade(prop.getAssignLevelGrade())
                .build());
    }

    /**
     * Build HRBP approver config from node VO
     */
    public static void setHrbpConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        approverConf.setHrbpConf(BpmnNodeApproverConfJson.HrbpConf.builder()
                .hrbpConfType(prop.getHrbpConfType())
                .build());
    }

    /**
     * Build customize approver config from node VO
     */
    public static void setCustomizeConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        approverConf.setCustomizeConf(BpmnNodeApproverConfJson.CustomizeConf.builder()
                .signType(prop.getSignType())
                .build());
    }

    /**
     * Build UDR approver config from node VO
     */
    public static void setUdrConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        BaseIdTranStruVo udrProp = prop.getUdrAssigneeProperty();
        BpmnNodeApproverConfJson.UDRConf udr = BpmnNodeApproverConfJson.UDRConf.builder()
                .valueJson(prop.getUdrValueJson())
                .signType(prop.getSignType())
                .udrProperty(udrProp != null ? udrProp.getId() : null)
                .udrPropertyName(udrProp != null ? udrProp.getName() : null)
                .ext1(prop.getExt1())
                .ext2(prop.getExt2())
                .ext3(prop.getExt3())
                .ext4(prop.getExt4())
                .build();
        List<BpmnNodeApproverConfJson.UDRConf> list = approverConf.getUdrConfList();
        if (list == null) {
            list = new ArrayList<>();
            approverConf.setUdrConfList(list);
        }
        list.add(udr);
    }

    /**
     * Build form related user approver config from node VO
     */
    public static void setFormRelatedUserConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        BpmnNodeApproverConfJson.FormRelatedUserConf fr = BpmnNodeApproverConfJson.FormRelatedUserConf.builder()
                .valueJson(com.alibaba.fastjson2.JSON.toJSONString(prop.getFormInfos()))
                .signType(prop.getSignType())
                .valueType(prop.getFormAssigneeProperty())
                .valueTypeName(org.openoa.base.constant.enums.NodeFormAssigneePropertyEnum
                        .getDescByCode(prop.getFormAssigneeProperty()))
                .build();
        List<BpmnNodeApproverConfJson.FormRelatedUserConf> list = approverConf.getFormRelatedUserConfList();
        if (list == null) {
            list = new ArrayList<>();
            approverConf.setFormRelatedUserConfList(list);
        }
        list.add(fr);
    }

    /**
     * Build outside access approver config from node VO
     */
    public static void setOutSideAccessConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        approverConf.setOutSideAccessConf(BpmnNodeApproverConfJson.OutSideAccessConf.builder()
                .nodeMark(prop.getNodeMark())
                .signType(prop.getSignType())
                .build());
    }

    /**
     * Build business table approver config from node VO
     */
    public static void setBusinessTableConf(BpmnNodeVo vo) {
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop == null) return;
        BpmnNodeApproverConfJson approverConf = getOrCreateApproverConf(vo);
        approverConf.setBusinessTableConf(BpmnNodeApproverConfJson.BusinessTableConf.builder()
                .configurationTableType(prop.getConfigurationTableType())
                .tableFieldType(prop.getTableFieldType())
                .signType(prop.getSignType())
                .build());
    }

    /**
     * Build conditions config
     */
    public static void setConditionsConf(BpmnNodeVo vo, List<BpmnNodeConditionsConfJson.ConditionGroup> groups, String outSideId) {
        BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
        config.setConditionsConf(BpmnNodeConditionsConfJson.builder()
                .conditionGroups(groups)
                .outSideConditionId(outSideId)
                .build());
    }

    /**
     * Build button/sign config from node VO
     */
    public static void setButtonSignConf(BpmnNodeVo vo) {
        BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
        BpmnNodeButtonSignConfJson bs = new BpmnNodeButtonSignConfJson();

        // Buttons - BpmnNodeButtonConfBaseVo has startPage, approvalPage, viewPage (List<Integer>)
        BpmnNodeButtonConfBaseVo btns = vo.getButtons();
        if (btns != null) {
            List<BpmnNodeButtonSignConfJson.ButtonConf> buttonList = new ArrayList<>();
            addButtonsFromIntList(buttonList, btns.getStartPage(), 1, 0);
            addButtonsFromIntList(buttonList, btns.getApprovalPage(), 2, 0);
            addButtonsFromIntList(buttonList, btns.getViewPage(), 3, 0);
            bs.setButtonConfList(buttonList);
        }

        // Sign-up
        if (vo.getIsSignUp() != null && vo.getIsSignUp() == 1) {
            BpmnNodePropertysVo prop = vo.getProperty();
            if (prop != null) {
                bs.setSignUpConf(BpmnNodeButtonSignConfJson.SignUpConf.builder()
                        .afterSignUpWay(prop.getAfterSignUpWay())
                        .signUpType(prop.getSignUpType())
                        .build());
            }
        }

        // Labels
        if (!CollectionUtils.isEmpty(vo.getLabelList())) {
            bs.setLabels(vo.getLabelList().stream()
                    .map(l -> BpmnNodeButtonSignConfJson.NodeLabel.builder()
                            .labelName(l.getLabelName())
                            .labelValue(l.getLabelValue())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Additional sign
        BpmnNodePropertysVo prop = vo.getProperty();
        if (prop != null && !CollectionUtils.isEmpty(prop.getAdditionalSignInfoList())) {
            List<BpmnNodeButtonSignConfJson.AdditionalSignConf> addSignList = new ArrayList<>();
            for (ExtraSignInfoVo info : prop.getAdditionalSignInfoList()) {
                addSignList.add(BpmnNodeButtonSignConfJson.AdditionalSignConf.builder()
                        .signInfos(com.alibaba.fastjson2.JSON.toJSONString(info.getSignInfos()))
                        .signProperty(info.getNodeProperty())
                        .signPropertyType(info.getPropertyType())
                        .signType(prop.getSignType())
                        .build());
            }
            bs.setAdditionalSignConfList(addSignList);
        }

        config.setButtonSignConf(bs);
    }

    /**
     * Build template/reminder config from node VO
     */
    public static void setTemplateConf(BpmnNodeVo vo) {
        BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
        BpmnNodeTemplateConfJson tc = new BpmnNodeTemplateConfJson();

        // Templates
        if (!CollectionUtils.isEmpty(vo.getTemplateVos())) {
            tc.setTemplates(vo.getTemplateVos().stream()
                    .map(t -> BpmnNodeTemplateConfJson.TemplateConf.builder()
                            .event(t.getEvent())
                            .informs(t.getInforms())
                            .emps(t.getEmps())
                            .roles(t.getRoles())
                            .funcs(t.getFuncs())
                            .templateId(t.getTemplateId())
                            .messageSendType(convertMessageSendTypeList(t.getMessageSendTypeList()))
                            .formCode(t.getFormCode())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Approve remind
        BpmnApproveRemindVo remind = vo.getApproveRemindVo();
        if (remind != null) {
            tc.setApproveRemind(BpmnNodeTemplateConfJson.ApproveRemindConf.builder()
                    .templateId(remind.getTemplateId())
                    .days(remind.getDays())
                    .build());
        }

        config.setTemplateConf(tc);
    }

    /**
     * Build low-code field control config from node VO
     */
    public static void setLowCodeConf(BpmnNodeVo vo) {
        if (CollectionUtils.isEmpty(vo.getLfFieldControlVOs())) return;
        BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
        config.setLowCodeConf(BpmnNodeLowCodeConfJson.builder()
                .fieldControls(vo.getLfFieldControlVOs().stream()
                        .map(fc -> BpmnNodeLowCodeConfJson.FieldControl.builder()
                                .formdataId(fc.getFormdataId())
                                .fieldId(fc.getFieldId())
                                .fieldName(fc.getFieldName())
                                .perm(fc.getPerm())
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

    // ============ Private helpers ============

    private static BpmnNodeApproverConfJson getOrCreateApproverConf(BpmnNodeVo vo) {
        BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
        if (config.getApproverConf() == null) {
            config.setApproverConf(new BpmnNodeApproverConfJson());
        }
        return config.getApproverConf();
    }

    private static void addButtonsFromIntList(List<BpmnNodeButtonSignConfJson.ButtonConf> list,
                                              List<Integer> buttonTypes, int pageType, int startPageOnly) {
        if (CollectionUtils.isEmpty(buttonTypes)) return;
        for (Integer btnType : buttonTypes) {
            list.add(BpmnNodeButtonSignConfJson.ButtonConf.builder()
                    .buttonPageType(pageType)
                    .buttonType(btnType)
                    .buttonName(ButtonTypeEnum.getDescByCode(btnType))
                    .startPageOnly(startPageOnly)
                    .build());
        }
    }

    private static String joinList(List<?> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    private static String convertMessageSendTypeList(List<BaseNumIdStruVo> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.stream().map(BaseNumIdStruVo::toString).collect(Collectors.joining(","));
    }
}
