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
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_business_draft")
public class BpmBusinessDraft implements TenantField, Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * business id
     */
    @TableField("bpmn_code")
    private String bpmnCode;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * process number
     */
    @TableField("process_code")
    private String processCode;
    /**
     * create user
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * create user id
     */
    @TableField("create_user")
    private String createUser;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;
    @TableField("draft_json")
    private String draftJson;

    @TableField("is_del")
    public Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}