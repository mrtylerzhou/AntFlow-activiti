package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnStartConditionsVo {

    /**
     * process Number
     */
    private String processNum;
    /**
     * process Name
     */
    private String processName;
    /**
     * process digest
     */
    private String processDesc;
    /**
     * process's business id
     */
    private String businessId;

    //condition
    /**
     * start user id
     */
    private String startUserId;
    /**
     * start user's job level
     */
    private Long startUserJobLevelId;
    /**
     * start user's deptId
     */
    private Long startUserDeptId;
    /**
     * start user's serving company
     */
    private Long startUserServicesCompanyId;

    /**
     * approvalEmp Id
     */
    private Long approvalEmplId;
    /**
     * approval emp ids
     */
    private List<String> employeeIds;

    /**
     * a list of approver
     */
    private List<String> approversList;
    /**
     * entry Id
     */
    private String entryId;
    /**
     * total Money
     */
    private String totalMoney;
    /**
     * total Money Operator
     */
    private Integer totalMoneyOperator;


    /**
     * multi approval Ids
     */
    private List<String> emplIdList;

    //================for demo============

    /**
     * third party account type
     */
    private Integer accountType;
    private BaseIdTranStruVo jobLevelVo;

    private Double leaveHour;

    /**
     * forwarded emp list
     */
    private List<String> empToForwardList=new ArrayList<>();
    //===============>>third party process<<===================

    /**
     * is it a third party process 0 for no and 1 for yes
     */
    private Integer isOutSideProcess;

    /**
     * business party id
     */
    private Integer businessPartyId;

    /**
     * template mark id
     */
    private Integer templateMarkId;

    /**
     * 业务方类型（1-嵌入式；2-接入式）
     * 1-embedded;2-api access
     */
    private Integer outSideType;

    /**
     * 是否外部接入式流程
     * is it an third party api access process(at the moment,third party process only support api access process)
     */
    private Boolean isOutSideAccessProc = false;

    /**
     * embedded node(though its name is embedded node,but it it also for api access process)
     */
    private List<OutSideBpmAccessEmbedNodeVo> embedNodes;
    /**
     * third party level nodes
     */
    private List<OutSideLevelNodeVo>outSideLevelNodes;
}