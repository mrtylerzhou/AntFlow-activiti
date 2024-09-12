package org.openoa.base.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.dto.PageDto;

import java.util.List;
import java.util.Map;

/**
 * business data vo
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVo extends PageDto {
    /**
     * process number
     */
    private String processNumber;
    /**
     * process key
     */
    private String processKey;
    /**
     * business id
     */
    private String businessId;
    /**
     * params
     */
    private String params;
    /**
     * process title
     */
    private String processTitle;
    /**
     * approval comment
     */
    private String approvalComment;
    /**
     * entity name
     */
    private String entityName;
    /**
     * process record
     */
    private ProcessRecordInfoVo processRecordInfo;
    /***
     * type 1 for save 2 for submit
     */
    private Integer type;
    /**
     * process state
     */
    private Boolean processState;
    /**
     * task id
     */
    private String taskId;
    /**
     * variable object map
     */
    Map<String, Object> objectMap;

    /**
     * nnode sign up approvers
     */
    public List<String> moreHandlers;

    /**
     * formCode
     */
    public String formCode;
    /**
     * operation Type
     */
    private Integer operationType;
    /**
     * forward user ids
     */
    public List<String> userIds;
    public List<BaseIdTranStruVo> userInfos;
    /**
     * approvers list
     */
    private List<String> approversList;

    private Boolean flag;

    /**
     * start page init datas
     */
    private Object initDatas;


    //preview params
    /**
     * start user
     */
    private String startUserId="";

    /**
     * bpmm code
     */
    private String bpmnCode;

    /**
     * bpmn name
     */
    private String bpmnName;

    /**
     * approval users
     */
    private String emplId;


    private String paramStr;

    private String empId;

    /**
     * process digest
     *
     * @return
     */
    private String processDigest;

    /**
     * data source id
     */
    private Long dataSourceId;


    private List<String> empIds;

    /**
     * is sign up node
     */
    private Boolean isSignUpNode;

    /**
     * sign up users
     */
    private List<String> signUpUsers;

    /**
     * is start page preview
     */
    private Boolean isStartPagePreview;

    /**
     * back to user
     */
    private String backToEmployeeId;


    //===============>>third party process<<===================

    /**
     * form data
     */
    private String formData;


    /**
     * conf vo
     */
    private BpmnConfVo bpmnConfVo;

    private Integer accountType;
    private BaseIdTranStruVo jobLevelVo;
    //todo 由于目前没有对接单点登陆,无法拿到登陆人信息,这里先传入进来
    private String assignee;
    /**
     * is third party process api access
     */
    private Boolean isOutSideAccessProc = false;

    private Boolean isOutSideChecked = false;
    /**
     * flow call back url
     */
    private String bpmFlowCallbackUrl;

    /**
     * view url
     */
    private String viewUrl;

    /**
     * submit url
     */
    private String submitUrl;

    private String submitUser;

    /**
     * conditions url
     */
    private String conditionsUrl;

    /**
     * 0 for embedded 1 for api access
     */
    private Integer outSideType;
    /**
     * template mark
     */
    private String templateMark;

    /**
     * template mark id
     */
    private Integer templateMarkId;
    /**
     * embedded node
     */
    private List<OutSideBpmAccessEmbedNodeVo> embedNodes;
    /**
     * level nodes
     */
    private List<OutSideLevelNodeVo>outSideLevelNodes;
}
