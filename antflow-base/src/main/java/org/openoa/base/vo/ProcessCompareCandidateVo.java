package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程对比: 候选实例项 (GET /bpmnConf/compareCandidates)
 * 限定与当前实例同 formCode(bpm_business_process.PROCESSINESS_KEY)。
 * 设计: .scratch/process-instance-compare-design.md §4.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCompareCandidateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 流程编号 (bpm_business_process.BUSINESS_NUMBER) */
    private String processNumber;
    /** 流程版本即 bpmnCode (bpm_business_process.VERSION) */
    private String version;
    /** 发起人 id (create_user) */
    private String createUser;
    /** 发起人姓名 */
    private String userName;
    /** 发起时间 */
    private Date createTime;
    /** 流程状态: 1审批中 2审批通过 3作废 6审批拒绝 */
    private Integer processState;
    /** 对应模板配置 id (t_bpmn_conf.id, 前端据此调 /bpmnConf/detail/{confId}) */
    private Long confId;
    /** 模板名称 (t_bpmn_conf.bpmn_name) */
    private String bpmnName;
}
