package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Approver configuration JSON for a BPMN node.
 * Consolidates: t_bpmn_node_personnel_conf, t_bpmn_node_personnel_empl_conf,
 * t_bpmn_node_role_conf, t_bpmn_node_role_outside_emp_conf,
 * t_bpmn_node_loop_conf, t_bpmn_node_assign_level_conf,
 * t_bpmn_node_hrbp_conf, t_bpmn_node_customize_conf,
 * t_bpmn_node_udr_conf, t_bpmn_node_form_related_user_conf,
 * t_bpmn_node_out_side_access_conf, t_bpmn_node_business_table_conf
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeApproverConfJson implements Serializable {

    /**
     * Personnel approver config (node_property = PERSONNEL or node_type = COPY)
     */
    private PersonnelConf personnelConf;

    /**
     * Role approver config (node_property = ROLE)
     */
    private List<RoleConf> roleConfList;

    /**
     * Loop approver config (node_property = LOOP)
     */
    private LoopConf loopConf;

    /**
     * Assign level approver config (node_property = LEVEL)
     */
    private AssignLevelConf assignLevelConf;

    /**
     * HRBP approver config (node_property = HRBP)
     */
    private HrbpConf hrbpConf;

    /**
     * Customize approver config (node_property = CUSTOMIZE)
     */
    private CustomizeConf customizeConf;

    /**
     * UDR (user-defined rule) approver config (node_property = UDR)
     */
    private List<UDRConf> udrConfList;

    /**
     * Form related user approver config (node_property = FORM_RELATED)
     */
    private List<FormRelatedUserConf> formRelatedUserConfList;

    /**
     * Outside access approver config (node_property = OUT_SIDE_ACCESS)
     */
    private OutSideAccessConf outSideAccessConf;

    /**
     * Business table approver config (node_property = BUSINESSTABLE)
     */
    private BusinessTableConf businessTableConf;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PersonnelConf implements Serializable {
        private Integer signType;
        private List<EmployeeInfo> employees;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeInfo implements Serializable {
        private String emplId;
        private String emplName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleConf implements Serializable {
        private String roleId;
        private String roleName;
        private Integer signType;
        private List<EmployeeInfo> outsideEmployees;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoopConf implements Serializable {
        private Integer loopEndType;
        private Integer loopNumberPlies;
        private String loopEndPerson;
        private String noparticipatingStaffIds;
        private Integer loopEndGrade;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignLevelConf implements Serializable {
        private Integer assignLevelType;
        private Integer assignLevelGrade;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HrbpConf implements Serializable {
        private Integer hrbpConfType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomizeConf implements Serializable {
        private Integer signType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UDRConf implements Serializable {
        private String valueJson;
        private Integer signType;
        private String udrProperty;
        private String udrPropertyName;
        private String ext1;
        private String ext2;
        private String ext3;
        private String ext4;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormRelatedUserConf implements Serializable {
        private String valueJson;
        private Integer signType;
        private Integer valueType;
        private String valueTypeName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OutSideAccessConf implements Serializable {
        private String nodeMark;
        private Integer signType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusinessTableConf implements Serializable {
        private Integer configurationTableType;
        private Integer tableFieldType;
        private Integer signType;
    }
}
