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
 * 用户自动审批设置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_user_auto_approve")
public class BpmUserAutoApprove implements TenantField, Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 归属人id
     */
    @TableField("owner_user_id")
    private String ownerUserId;
    /**
     * 归属人姓名
     */
    @TableField("owner_user_name")
    private String ownerUserName;
    /**
     * 流程formCode
     */
    @TableField("form_code")
    private String formCode;
    /**
     * 配置时活跃版本bpmnCode
     */
    @TableField("bpmn_code")
    private String bpmnCode;
    /**
     * 节点范围JSON [{elementId,nodeName}], 空=整个流程
     */
    @TableField("node_scope_json")
    private String nodeScopeJson;
    /**
     * 条件JSON {conditionList,groupRelation}, 仅LF流程
     */
    @TableField("condition_json")
    private String conditionJson;
    /**
     * 默认审批意见
     */
    @TableField("default_comment")
    private String defaultComment;
    /**
     * 启用 1是 0否
     */
    @TableField("enabled")
    private Integer enabled;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    @TableField("create_user")
    private String createUser;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_user")
    private String updateUser;
    @TableField("update_time")
    private Date updateTime;
}
