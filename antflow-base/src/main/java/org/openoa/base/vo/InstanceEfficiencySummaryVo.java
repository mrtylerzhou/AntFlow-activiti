package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程实例效能-顶部汇总 VO
 */
@Data
public class InstanceEfficiencySummaryVo implements Serializable {

    private String processNumber;

    /**
     * 流程状态
     * @see org.openoa.base.constant.enums.ProcessStateEnum
     */
    private Integer processState;

    private String processStateName;

    /**
     * 流程发起时间
     */
    private Date createTime;

    /**
     * 当时耗时(毫秒)
     * 进行中: now - createTime
     * 已完成: max(节点end_time) - createTime
     */
    private Long totalDuration;

    /**
     * 当时耗时(格式化文本)
     */
    private String totalDurationText;

    /**
     * 是否已完成
     */
    private Boolean finished;
}
