package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 节点诊断结论 (POST /bpmnConf/diagnoseNode)
 *
 * <p>归因判定短路顺序(见 .scratch/process-diagnosis-design.md §5):</p>
 * <ol>
 *   <li>实际存在性: nodeId 出现在 af_hi_taskinst / af_ru_task 任一条 → present=true
 *       (重提多次出现也算存在); 存在时走加批归因(ADD_APPROVAL)</li>
 *   <li>尚未到达: 流程未结束且目标拓扑序在当前停留节点之后 → NOT_REACHED</li>
 *   <li>条件分支横评: 目标挂在条件网关分支上, 全分支求值 → CONDITION_MISS</li>
 *   <li>减签跳过: bpm_flowrun_entrust(actionType=3) 非空 → SIGN_SKIP</li>
 *   <li>兜底: UNKNOWN, 裸列原始数据</li>
 * </ol>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeDiagnosisVo implements Serializable {

    /** 节点实际存在于审批路径(hi ∪ ru 任一条) */
    private Boolean present;

    /** 用户选择(有/没有)与实际存在性矛盾 */
    private Boolean expectationMismatch;

    /** EXISTS / NOT_REACHED / CONDITION_MISS / SIGN_SKIP / ADD_APPROVAL / UNKNOWN */
    private String conclusionType;

    /** 一句话结论 */
    private String message;

    private String nodeName;

    /** 当前停留节点名(NOT_REACHED 展示用) */
    private String currentNodeName;

    /** 当前停留节点 bpmn_node id */
    private String currentNodeId;

    /** 该节点相关的加减签/委托记录 */
    private List<EntrustRecordVo> entrustRecords;

    /** 加批记录(bpm_verify_info verifyStatus=9 + variableConfigJson.signUps) */
    private List<SignupRecordVo> signupRecords;

    /** 前驱节点是否配置了加批按钮(buttonType=19) */
    private Boolean prevNodeHasAddApproval;

    private String prevNodeName;

    /** 目标节点所在条件网关的全分支横评(条件/实际值/命中) */
    private List<BranchEvaluation> branches;

    /** 兜底: 该流程与目标节点相关的 task 原始记录 */
    private List<RawTaskVo> rawTasks;

    // ============ 人员维度诊断 (4.3) ============

    /** 应审人(引擎评估, 含运行期加减签标记: name 后缀 +加签/-减签/*转办) */
    private List<ApproverVo> expectedApprovers;

    /** 配置规则描述(nodeProperty 汉字, 如 "指定人员/指定角色/发起人/直属领导") */
    private String ruleDesc;

    /** 实际审批人(该节点 task assignee) */
    private List<ApproverVo> actualApprovers;

    /** 人员维度结论; 仅请求带 personId 时返回 */
    private PersonDiagnosis personDiagnosis;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproverVo implements Serializable {
        private String userId;
        private String name;
        /** 运行期标记: +加签 / -减签 / *转办 / null 原配置 */
        private String mark;
        private String source;
        private String time;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonDiagnosis implements Serializable {
        private String personId;
        private String personName;
        /** 该人是否出现在节点实际审批人中 */
        private Boolean presentPerson;
        /** 与用户预期是否矛盾 */
        private Boolean expectationMismatch;
        private String message;
        /** true=结论含推断成分(动态评估差异), 非确证事实 */
        private Boolean inference;
        private String inferenceNote;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntrustRecordVo implements Serializable {
        private Integer actionType;
        /** 转办/加签/减签/表单关联刷新 */
        private String actionTypeName;
        private String originalId;
        private String originalName;
        private String actualId;
        private String actualName;
        private String nodeId;
        private Date createTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRecordVo implements Serializable {
        private String userName;
        private Date verifyDate;
        private String verifyDesc;
        /** 来源: verify_info / sign_up_config */
        private String source;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchEvaluation implements Serializable {
        private String branchName;
        private Integer priority;
        private Boolean isDefault;
        /** 该分支条件按当前表单值求值结果; 默认分支无值为 null */
        private Boolean hit;
        /** 目标节点是否在此分支上 */
        private Boolean containsTarget;
        private List<ConditionItemResult> conditions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionItemResult implements Serializable {
        private String label;
        private String fieldName;
        private String fieldTypeName;
        /** 操作符文本, 如 ">", "介于" */
        private String opText;
        /** 条件期望值(展示用, zdy1 [op2 zdy2]) */
        private String expectText;
        /** 当前表单实际值 */
        private String actualValue;
        private Boolean pass;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RawTaskVo implements Serializable {
        private String taskId;
        private String taskName;
        private String assigneeName;
        private Date startTime;
        private Date endTime;
        private String deleteReason;
        private String nodeId;
        /** ru / hi */
        private String source;
    }
}
