package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.constant.enums.MissingAssigneeProcessStragtegyEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Classname BpmnNodeVo
 * @Description node vo
 * @Date 2021-10-31 10:02
 * @Created by AntOffice
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeVo  implements Serializable {

    private Long id;
    /**
     * conf id
     */
    private Long confId;
    /**
     * node id
     */
    private String nodeId;
    /**
     * node type 1 for start node 2 for gateway 3 for condition 4 for approver
     */
    private Integer nodeType;
    private Boolean isParallel;
    private Boolean isDynamicCondition;
    private boolean aggregationNode;
    /**
     * node property 1 for no property 2 for layer approval 3 for specified layer approval 4 for specified role 5 for specified person 6 for HRBP
     * 7 for self-select module 8 for related configuration table
     */
    private Integer nodeProperty;
    /**
     * node property name
     */
    private String nodePropertyName;
    /**
     * prev node id
     */
    private String nodeFrom;
    private String nodeFroms;
    private List<String>prevId=new ArrayList<>();
    /**
     * 该审批节点是否可以批量同意（0-否；1-是）
     * can be processing in batch
     */
    private Integer batchStatus;

    private Integer approvalStandard;
    /**
     * node name
     */
    private String nodeName;
    /**
     * node display name
     */
    private String nodeDisplayName;
    /**
     * annotation
     */
    private String annotation;
    /**
     * is duduplication 0 no 1 yes
     */
    private Integer isDeduplication;
    private boolean deduplicationExclude;
    /**
     * is node sign up 0 for no 1for yes
     */
    private Integer isSignUp;
    /**
     * ordered node type
     * @see OrderNodeTypeEnum
     */
    private Integer orderedNodeType;
    /**
     * remark
     */
    private String remark;

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
     * node to
     */
    private List<String> nodeTo;

    /**
     * node property
     */
    private BpmnNodePropertysVo property;

    /**
     * node params
     */
    private BpmnNodeParamsVo params;

    /**
     * buttons
     */
    private BpmnNodeButtonConfBaseVo buttons;

    /**
     * node notice template
     */
    private List<BpmnTemplateVo> templateVos;
    /**
     * node approval remind
     */
    private BpmnApproveRemindVo approveRemindVo;

    //===============>>third party processs service<<===================

    /**
     * condition url
     */
    private String conditionsUrl;


    private String formCode;

    private Integer isOutSideProcess;
    private Integer isLowCodeFlow;
    private List<LFFieldControlVO> lfFieldControlVOs;
    /**
     * forwarded emp list
     */
    private List<BaseIdTranStruVo> empToForwardList=new ArrayList<>();
    //antflow实现通知的原理是下个节点,如果是最后一个审批人节点没有下个节点了,需要特殊处理
    private boolean lastNodeForward;
    /**
     * 0 for no and 1 for yes
     */
    private List<BpmnNodeVo> fromNodes;
    private List<BpmnNodeLabelVO> labelList;
    private String elementId;
    /**
     * 当前未找到审批人处理方式,如果为null时不进行默认处理
     * @see MissingAssigneeProcessStragtegyEnum
     */
    private Integer missingAssigneeDealWay;

	/**
	 * 允许发起人自选抄送人
	 */
	private Integer ccSelfSelectFlag;
    public void setPrevId(List<String>prevId){
        this.prevId=prevId;
        if(!ObjectUtils.isEmpty(prevId)){
            this.nodeFroms=String.join(",",prevId);
        }
    }
    //set nodes from
    public void setNodeFroms(String nodeFroms){
        this.nodeFroms=nodeFroms;
        if(!ObjectUtils.isEmpty(nodeFroms)){
            this.prevId= Arrays.asList(nodeFroms.split(","));
        }
    }
    public void updateLabelListPossible(BpmnNodeLabelVO labelVO){
        if(!CollectionUtils.isEmpty(this.labelList)){
            this.labelList.add(labelVO);
        }else{
            this.labelList=new ArrayList<>();
            this.labelList.add(labelVO);
        }
    }
    @Override
    public String toString(){
        return "BpmnNodeVo{" +
                "id=" + id +
                ", confId=" + confId +
                ", nodeId='" + nodeId + '\'' +
                ", nodeType=" + nodeType +
                ", nodeName='" + nodeName + '\'' +
                ", nodeDisplayName='" + nodeDisplayName + '\'' +
                ", nodePropertyName='" + nodePropertyName + '\'' +
                ", isDynamicCondition=" + isDynamicCondition +
                ", nodeProperty=" + nodeProperty +
                ", aggregationNode=" + aggregationNode +
                ", nodeFrom='" + nodeFrom + '\'' +
                ", nodeFroms='" + nodeFroms + '\'' +
                ", batchStatus=" + batchStatus +
                ", isParallel=" + isParallel +
                ", approvalStandard=" + approvalStandard +
                ", annotation='" + annotation + '\'' +
                ", isDeduplication=" + isDeduplication +
                ", deduplicationExclude=" + deduplicationExclude +
                ", isSignUp=" + isSignUp +
                ", orderedNodeType=" + orderedNodeType +
                ", remark='" + remark + '\'' +
                ", isDel=" + isDel;

         }
}
