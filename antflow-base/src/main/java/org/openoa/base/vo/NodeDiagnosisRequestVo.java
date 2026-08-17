package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 节点诊断请求 (POST /bpmnConf/diagnoseNode)
 */
@Data
public class NodeDiagnosisRequestVo implements Serializable {

    private String processNumber;

    /**
     * t_bpmn_node 主键 id (同 nodeConfig 节点 id, 与 af_hi_taskinst.node_id / af_ru_task.node_id 对齐)
     */
    private Long nodeId;

    /**
     * 用户选择: true=有此节点 / false=没有此节点 / null=未选择
     */
    private Boolean expectedPresent;

    /**
     * 人员维度诊断: 选中的审批人 id (不传则只做节点维度)
     */
    private String personId;

    /**
     * 人员维度预期: true=预期此审批人存在 / false=预期不存在
     */
    private Boolean expectedPersonPresent;
}
