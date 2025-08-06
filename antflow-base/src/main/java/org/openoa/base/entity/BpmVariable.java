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
 * process variable
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpm_variable")
public class BpmVariable implements TenantField, Serializable {


    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process number
     */
    @TableField("process_num")
    private String processNum;
    /**
     * process name
     */
    @TableField("process_name")
    private String processName;
    /**
     * process desc
     */
    @TableField("process_desc")
    private String processDesc;
    /**
     * process start conditions
     */
    @TableField("process_start_conditions")
    private String processStartConditions;
    /**
     * bpmn code
     */
    @TableField("bpmn_code")
    private String bpmnCode;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for delete
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}
