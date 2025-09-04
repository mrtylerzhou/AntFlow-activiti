package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程审计表
 */
@Data
@TableName("t_bpm_process_audit")
public class BpmProcessAudit {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 流程编号 */
    @TableField("process_number")
    private String processNumber;

    /** 表单编码 */
    @TableField("form_code")
    private String formCode;

    /** 字段名 */
    @TableField("field_name")
    private String fieldName;

    /** 旧值 */
    @TableField("old_value")
    private String oldValue;

    /** 新值 */
    @TableField("new_value")
    private String newValue;

    /** 租户ID */
    @TableField("tenant_id")
    private String tenantId;

    /** 任务名称 */
    @TableField("task_name")
    private String taskName;

    /** 任务定义key */
    @TableField("task_def_key")
    private String taskDefKey;

    /** 创建人 */
    @TableField("create_user")
    private String createUser;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
