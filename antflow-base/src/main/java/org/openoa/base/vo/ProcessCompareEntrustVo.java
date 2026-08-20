package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程对比: 加签/减签/转办记录项 (GET /bpmnConf/compareEntrusts)
 * 来源 bpm_flowrun_entrust(表自带 node_id)。
 * 设计: .scratch/process-instance-compare-design.md §4.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCompareEntrustVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 设计节点 id (t_bpmn_node.id, 与 getBpmVerifyInfoVos 的 nodeId 同口径) */
    private String nodeId;
    /** 0/1=转办 2=加签 3=减签 4=表单关联刷新 */
    private Integer actionType;
    /** actionType 可读名称 */
    private String actionTypeName;
    /** 原审批人 id */
    private String originalId;
    /** 原审批人姓名 */
    private String originalName;
    /** 实际/被操作审批人 id */
    private String actualId;
    /** 实际/被操作审批人姓名 */
    private String actualName;
}
