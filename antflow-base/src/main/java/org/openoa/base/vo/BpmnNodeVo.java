package org.openoa.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openoa.base.constant.enums.MissingAssigneeProcessStragtegyEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Boolean isCarbonCopyNode;
    private boolean aggregationNode;
    private Boolean isAutomaticNode;
    /**
     * 条件审批节点标记: 设计期 nodeType=12, nodeSpecialProcess 转为 4 后置 true
     * 运行期与 automaticNode 类似, 但保留真实审批人, 仅在条件满足时自动 complete
     */
    private Boolean isConditionApproveNode;
    /**
     * 条件推进节点标记: 条件审批(nodeType=12)子类型, 自动勾选推进按钮(42,别名同意)
     * 满足条件时自动推进到固定目标节点(虚拟人-3), 不满足时留给真实审批人(可手动推进), 强制 forwardType=2
     */
    private Boolean isConditionAdvanceNode;
    /**
     * 条件完成节点标记: 条件推进(nodeType=12)子类型, 目标设计时自动算最后一个审批人节点(不可编辑)
     * 运行时复用条件推进处理器(processConditionAdvanceNode), 满足条件自动推进到最后一个审批人, 不满足留给真实审批人
     */
    private Boolean isConditionFinishNode;
    /**
     * 条件拒绝节点标记: 条件审批(nodeType=12)子类型
     * 满足条件时自动拒绝(固定终止流程, 忽略不同意退回配置), 不满足时留给真实审批人
     */
    private Boolean isConditionDisagreeNode;
    /**
     * 条件自动加批节点标记: 条件审批(nodeType=12)子类型
     * 满足条件时自动加批(autoSignUpUsers), 不满足时留给真实审批人(加批按钮屏蔽)
     */
    private Boolean isConditionAutoSignUpNode;
    /**
     * 条件自动加批节点的加批人列表(必填), 存 nodeConfigJson.autoSignUpUsers
     */
    private java.util.List<BaseIdTranStruVo> autoSignUpUsers;
    /**
     * 条件自动加批节点的加批规则子配置(增强版: 支持指定人员/角色/直属领导/层级/HRBP/发起人自己/自定义).
     * 结构: {setType, nodeApproveList, directorLevel, property, nodeProperty, resolvedProperty}
     * 存 nodeConfigJson.autoSignUpConf; 运行期由 AutoSignUpAssigneeResolver 解析为具体用户.
     */
    private Object autoSignUpConf;
    /**
     * 条件自动转办节点标记: 条件审批(nodeType=12)子类型
     * 满足条件时逐任务自动转办(委托语义), 不满足时留给真实审批人(转办按钮屏蔽)
     */
    private Boolean isConditionAutoTransferNode;
    /**
     * 条件自动转办节点的转办配置: {transferType:1|2, transferToUser:{id,name}, transferPairs:[{from,to}]}
     * 存 nodeConfigJson.autoTransferConf.
     */
    private Object autoTransferConf;
    /**
     * 条件抄送节点标记: 设计期 nodeType=13, nodeSpecialProcess 转为 4 后置 true
     * 运行期与 copyNodeV2 类似, 总是 complete; 仅条件满足时写抄送记录
     */
    private Boolean isConditionCopyNode;
    /**
     * 协助节点标记: 设计期 nodeType=17, nodeSpecialProcess 转为 4 后置 true
     * 运行期与审批人节点一致, 但语义为"办理"而非"审批"
     */
    private Boolean isAssistNode;
    /**
     * 自动推进节点标记: 设计期 nodeType=18, nodeSpecialProcess 转为 4 后置 true
     * 运行期与自动节点(9)同构(虚拟人 -3), 满足条件时推进到指定目标节点, 不满足时自动 complete
     */
    private Boolean isAutoAdvanceNode;
    /**
     * 自动完成节点标记: 自动推进(nodeType=18)子类型, 目标自动为最后一个审批人节点
     * 仅前端反显区分+颜色区分用, 运行时复用 auto_advance_node 处理器
     */
    private Boolean isAutoCompleteNode;
    /**
     * 自动退回节点标记: 设计期 nodeType=19, nodeSpecialProcess 转为 4 后置 true
     * 运行期与自动节点(9)同构(虚拟人 -3), 满足条件时退回到指定目标节点(FOUR_DISAGREE), 不满足时自动 complete
     */
    private Boolean isAutoReturnNode;
    /**
     * 条件退回节点标记: 设计期 nodeType=20, nodeSpecialProcess 转为 4 后置 true
     * 运行期保留真实审批人, 满足条件时自动退回到不同意按钮配置的目标节点, 不满足时留给审批人
     */
    private Boolean isConditionReturnNode;
    /**
     * 条件退回发起人节点标记: 设计期 nodeType=21, nodeSpecialProcess 转为 4 后置 true
     * 运行期保留真实审批人, 满足条件时自动退回发起人节点, 不满足时留给审批人
     */
    private Boolean isConditionReturnStarterNode;
    /**
     * 完成审批节点标记: 审批人节点(nodeType=4) + 推进按钮(42) + finish_approve_node 标签
     * 目标自动填充为流程最后一个审批人节点, 用户不可改
     */
    private Boolean isFinishApproveNode;
    /**
     * 同意推进节点标记: 审批人节点(nodeType=4) + 同意按钮(固定节点行为) + approve_forward_node 标签
     * 同意按钮配置为"固定节点"行为时为 true, complete 后推进到 forwardNodeIds 指定的固定节点.
     * 与推进按钮(42)互斥: isApproveForwardNode=true 时 buttons.approvalPage 不能含 42.
     */
    private Boolean isApproveForwardNode;
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

    private Integer extraFlags;
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
    private BpmnNodeButtonConfBaseVo buttons=new BpmnNodeButtonConfBaseVo();

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
     * 外部表单模式: 节点级整表隐藏标记
     * Key = formdataId (t_bpmn_conf_lf_formdata.id), Value = true 表示该表单在此节点整体隐藏
     * 仅外部表单模式使用; 内联模式为 null
     */
    private Map<String, Boolean> formHidden;
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
    /**
     * Transient node config JSON - populated during edit flow
     */
    @JsonIgnore
    private BpmnNodeConfigJson nodeConfigJsonObj;
    private String elementId;
    /**
     * 当前未找到审批人处理方式,如果为null时不进行默认处理
     * @see MissingAssigneeProcessStragtegyEnum
     */
    private Integer noHeaderAction;
    /**
     * 标识当前节点为"上一节点指定"审批人类型
     * 前端传入,后端在 nodeSpecialProcess 中据此自动贴 af_syslabel_prev_node_appointed 标签
     */
    private Boolean isPrevNodeAppointed;
    /**
     * 标识当前节点为"选择条件"审批人类型
     * 前端传入,后端在 nodeSpecialProcess 中据此自动贴 af_syslabel_pick_condition 标签
     */
    private Boolean isPickCondition;
    /**
     * Auto node condition configuration (received from frontend during edit,
     * sent to frontend during display). Stored in node_config_json.autoNodeConf.
     */
    private Object autoNodeConf;
    /**
     * 不同意按钮退回行为类型(4=退回指定节点重新开始, 5=退回指定节点回到当前节点)
     */
    private Integer disagreeBackType;
    /**
     * 不同意退回目标节点ID(设计态nodeId UUID)
     */
    private String disagreeBackToNodeId;
    /**
     * 退回按钮行为类型(0=无限制, 1=上一节点, 2=发起人(不回), 3=发起人(回), 4=指定节点(不回), 5=指定节点(回))
     * 前端传入,后端在 nodeSpecialProcess 中据此自动贴退回行为标签
     */
    private Integer drawBackType;
    /**
     * 退回按钮允许退回的节点ID列表(设计态nodeId UUID)
     * 仅 drawBackType=4/5 时有值
     */
    private java.util.List<String> drawBackNodeIds;
    /**
     * 推进按钮行为类型(0=任意未来节点, 1=指定节点多选, 2=固定节点单选)
     * 前端传入,后端在 nodeSpecialProcess 中据此自动贴推进标签
     */
    private Integer forwardType;
    /**
     * 推进按钮允许推进到的节点ID列表(设计态nodeId UUID)
     * 仅 forwardType=1/2 时有值
     */
    private java.util.List<String> forwardNodeIds;
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
    public void setOrAddLabelList(BpmnNodeLabelVO labelVO){
        if(!CollectionUtils.isEmpty(this.labelList)){
            this.labelList.add(labelVO);
        }else{
            this.labelList=new ArrayList<>();
            this.labelList.add(labelVO);
        }
    }

    /**
     * Get or create the node config JSON object
     */
    @JsonIgnore
    public BpmnNodeConfigJson getOrCreateNodeConfigJson() {
        if (this.nodeConfigJsonObj == null) {
            this.nodeConfigJsonObj = new BpmnNodeConfigJson();
        }
        return this.nodeConfigJsonObj;
    }

    /**
     * Serialize nodeConfigJsonObj to JSON string for DB storage
     */
    public void setNodeConfigJson(String nodeConfigJson) {
        if (nodeConfigJson != null && !nodeConfigJson.isEmpty()) {
            this.nodeConfigJsonObj = JsonConfUtil.parseNodeConfig(nodeConfigJson);
        }
    }

    public String serializeNodeConfigJson() {
        if (this.nodeConfigJsonObj == null) {
            return null;
        }
        return JsonConfUtil.toNodeConfigJson(this.nodeConfigJsonObj);
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
