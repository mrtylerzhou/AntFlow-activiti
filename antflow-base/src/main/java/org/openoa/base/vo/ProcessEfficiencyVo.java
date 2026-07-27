package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 流程效能统计查询VO
 *
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessEfficiencyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分页参数
     */
    private PageDto pageDto;

    /**
     * 流程类型编码
     */
    private String formCode;

    /**
     * 流程编号
     */
    private String processNumber;

    /**
     * 审批人(姓名或ID模糊匹配)
     */
    private String assignee;

    /**
     * 流程状态
     */
    private Integer processState;

    /**
     * 开始时间范围-起
     */
    private Date startTimeBegin;

    /**
     * 开始时间范围-止
     */
    private Date startTimeEnd;

    /**
     * 流程实例ID(展开节点级时使用)
     */
    private String procInstId;

    /**
     * 任务定义Key(展开任务级时使用)
     */
    private String taskDefKey;

    /**
     * 统计计算接口参数:formCode列表
     */
    private List<String> formCodes;
}
