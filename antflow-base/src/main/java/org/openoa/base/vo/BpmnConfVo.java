package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Classname BpmnConfVo
 * @Description conf vo
 * @since 0.5
 * @Created by AntOffice
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnConfVo {

    /**
     * auto incr Id
     */
    private Long id;
    /**
     * bpmnCode
     */
    private String bpmnCode;
    /**
     * bpmnName
     */
    private String bpmnName;
    /**
     * process's type
     */
    private Integer bpmnType;
    /**
     * formCode
     */
    private String formCode;
    /**
     * formCode
     */
    private String formCodeDisplayName;
    /**
     * appId
     */
    private Integer appId;
    /**
     */
    private Integer deduplicationType;
    /**
     */
    private Integer effectiveStatus;
    /**
     */
    private Integer isAll;

    /**
     */
    private Integer isOutSideProcess;
    private Integer isLowCodeFlow;
    /**
     * process's business party
     */
    private Integer businessPartyId;

    /**
     */
    private String remark;
    /**
     */
    private Integer isDel;
    /**
     */
    private String createUser;
    /**
     */
    private Date createTime;
    /**
     */
    private String updateUser;
    /**
     */
    private Date updateTime;

    //===============>>query to do list<<===================

    /**
     * searh condition
     */
    private String search;

    //===============>>extend info<<===================

    /**
     */
    private String deduplicationTypeName;

    /**
     */
    private String createUserName;

    /**
     */
    private String createUserUuid;

    /**
     */
    private String businessPartyName;


    private String businessPartyMark;

    /**
     * conf's nodes
     */
    private List<BpmnNodeVo> nodes;

    /**
     * buttons on view page
     */
    private BpmnViewPageButtonBaseVo viewPageButtons;

    /**
     * out of scope notice template
     */
    private List<BpmnTemplateVo> templateVos;

    //===============>>thirdy party process<<===================

    /**
     * form json
     */
    private String formData;

    /**
     * process's conf callback
     */
    private String bpmConfCallbackUrl;

    /**
     * process's flow callback
     */
    private String bpmFlowCallbackUrl;

    /**
     * where to view the process
     */
    private String viewUrl;

    /**
     * submit url
     */
    private String submitUrl;

    /**
     * process condition url
     */
    private String conditionsUrl;


    private List<Long> businessPartyIds;

    /**
     * business type 1 for embedded 2 for api access
     */
    private Integer type;
    private String lfFormData;
    private Long lfFormDataId;
}