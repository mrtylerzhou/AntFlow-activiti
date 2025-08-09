package org.openoa.engine.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.OutSideBpmAccessEmbedNodeVo;
import org.openoa.base.vo.OutSideLevelNodeVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * third party process service-business access  table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class OutSideBpmAccessBusinessVo implements Serializable {

    /**
     * auto incr id
     */
    private Long id;
    /**
     * business party id
     */
    private Long businessPartyId;
    /**
     * bpmn conf id
     */
    private Long bpmnConfId;
    /**
     * form code
     */
    private String formCode;
    /**
     * process number
     */
    private String processNumber;
    /**
     * form data on the pc
     */
    private String formDataPc;
    /**
     * form data on app
     */
    private String formDataApp;
    /**
     * template marks
     */
    private List<String> templateMarks;
    /**
     * start user id
     */
    private String userId;
    private String userName;
    /**
     * approvalEmp Ids
     */
    private List<BaseIdTranStruVo> approvalEmpls;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for deleted
     */
    private Integer isDel;
    /**
     * create user
     */
    private String createUser;
    /**
     * create time
     */
    private Date createTime;
    /**
     * update user
     */
    private String updateUser;
    /**
     * update time
     */
    private Date updateTime;

    //===============>>ext fields<<===================

    /**
     * process break desc
     */
    private String processBreakDesc;

    /**
     * who break this process
     */
    private String processBreakUserId;

    /**
     * business party mark
     */
    private String businessPartyMark;

    /**
     * application id
     */
    private Integer applicationId;


    private Integer outSideType;

    /**
     * key is node's id,value is a list of approves,if there is only one start user chosen node,the map's key is ignored
     */
    private Map<String,List<BaseIdTranStruVo>> approversList;
    /**
     * embedded mode
     */
    private List<OutSideBpmAccessEmbedNodeVo> embedNodes;

    /**
     * out side level nodes
     */
    private List<OutSideLevelNodeVo>outSideLevelNodes;
    private Map<String,Object> lfConditions;
    private Boolean isLowCodeFlow;
    private Map<String,Object> lfFields;
}
