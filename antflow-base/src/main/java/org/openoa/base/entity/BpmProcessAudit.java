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

    /**
     * 字段 label(展示给业务用户).
     * 低代码流程从 t_bpmn_conf_lf_formdata_field.fieldName 读取;
     * DIY 流程无 label 概念, 保持 null, 前端 fallback 用 fieldName 展示.
     */
    @TableField("field_label")
    private String fieldLabel;

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

    /**
     * 变更人姓名(审批时快照, 用于审计溯源).
     * createUser 是 empId, 这里存当时的登录人姓名, 避免后续员工改名/查询联表.
     */
    @TableField("create_user_name")
    private String createUserName;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
