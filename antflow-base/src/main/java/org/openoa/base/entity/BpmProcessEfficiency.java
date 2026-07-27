package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程效能统计表
 *
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_efficiency")
public class BpmProcessEfficiency implements TenantField, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计类型:任务级别
     */
    public static final int TYPE_TASK = 1;
    /**
     * 统计类型:节点级别
     */
    public static final int TYPE_NODE = 2;
    /**
     * 统计类型:流程级别
     */
    public static final int TYPE_PROCESS = 3;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程类型编码
     */
    @TableField("form_code")
    private String formCode;

    /**
     * 流程编号
     */
    @TableField("process_number")
    private String processNumber;

    /**
     * 流程实例ID
     */
    @TableField("proc_inst_id")
    private String procInstId;

    /**
     * 执行ID
     */
    @TableField("execution_id")
    private String executionId;

    /**
     * 任务定义Key
     */
    @TableField("task_def_key")
    private String taskDefKey;

    /**
     * 节点名称
     */
    @TableField("node_name")
    private String nodeName;

    /**
     * 审批人ID(节点级逗号分隔,流程级null)
     */
    @TableField("assignee")
    private String assignee;

    /**
     * 审批人姓名(节点级逗号分隔,流程级null)
     */
    @TableField("assignee_name")
    private String assigneeName;

    /**
     * 统计类型:1=任务,2=节点,3=流程
     */
    @TableField("static_type")
    private Integer staticType;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间(未完成存null)
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 耗时(毫秒)
     */
    @TableField("duration")
    private Long duration;

    /**
     * 流程状态(冗余)
     */
    @TableField("process_state")
    private Integer processState;

    /**
     * 流程创建时间(冗余,用于筛选)
     */
    @TableField("process_create_time")
    private Date processCreateTime;

    @TableField("tenant_id")
    private String tenantId;

    @TableField("is_del")
    private Integer isDel;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
