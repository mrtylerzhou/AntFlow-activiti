package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户自动审批设置 前后端交换VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAutoApproveVo implements Serializable {

    private Long id;
    /**
     * 配置指向版本的bpmnConf id(编辑时拉节点下拉用)
     */
    private Long confId;
    /**
     * 归属人id
     */
    private String ownerUserId;
    /**
     * 归属人姓名
     */
    private String ownerUserName;
    /**
     * 流程formCode
     */
    private String formCode;
    /**
     * 配置指向的版本bpmnCode
     */
    private String bpmnCode;
    /**
     * 流程名称(展示用)
     */
    private String bpmnName;
    /**
     * 流程类型 1 DIY 2 LF低代码 3 第三方
     */
    private Integer flowType;
    /**
     * 节点范围, 空=整个流程
     */
    private List<NodeScopeItem> nodeScope;
    /**
     * 审批条件(后端存储格式), 仅LF
     */
    private List<List<BpmnNodeConditionsConfVueVo>> conditionList;
    /**
     * 条件组关系 false=且 true=或
     */
    private Boolean groupRelation;
    /**
     * 默认审批意见
     */
    private String defaultComment;
    /**
     * 启用 1是 0否
     */
    private Integer enabled;
    /**
     * 活跃状态(实时计算列): 配置bpmnCode == 当前活跃版本
     */
    private Boolean active;
    private Date createTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeScopeItem implements Serializable {
        /**
         * 节点elementId
         */
        private String elementId;
        /**
         * 节点名称快照
         */
        private String nodeName;
    }
}
