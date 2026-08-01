package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程实例效能-节点列表项 VO
 */
@Data
public class InstanceEfficiencyNodeVo implements Serializable {

    /**
     * 任务定义 Key(BPMN element id)
     */
    private String taskDefKey;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     * @see org.openoa.base.constant.enums.NodeTypeEnum
     */
    private Integer nodeType;

    private String nodeTypeName;

    /**
     * 节点总耗时(毫秒,退回多轮累加)
     */
    private Long duration;

    /**
     * 节点总耗时(格式化文本)
     */
    private String durationText;

    /**
     * 是否发生过退回(多轮)
     */
    private Boolean hasRollback;

    /**
     * 是否进行中(当前运行节点)
     */
    private Boolean inProgress;

    /**
     * TOP 排名(1/2/3,null 表示未上榜)
     * 进行中节点不参与排行
     */
    private Integer topRank;

    /**
     * 执行序号(按 min(start_time) 升序)
     */
    private Integer orderNo;
}
