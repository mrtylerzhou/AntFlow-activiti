package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程实例效能-人员明细 VO
 */
@Data
public class InstanceEfficiencyAssigneeVo implements Serializable {

    /**
     * 审批人 ID
     */
    private String assignee;

    /**
     * 审批人姓名
     */
    private String assigneeName;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间(null=未完成)
     */
    private Date endTime;

    /**
     * 个人耗时(毫秒)
     * 已完成:取 af_hi_taskinst.duration
     * 未完成:now - start_time
     */
    private Long duration;

    /**
     * 个人耗时(格式化文本)
     */
    private String durationText;

    /**
     * 是否已完成
     */
    private Boolean finished;
}
