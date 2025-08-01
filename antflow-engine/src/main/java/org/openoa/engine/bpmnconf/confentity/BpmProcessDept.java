package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_dept")
public class BpmProcessDept {
    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process number
     */
    @TableField("process_code")
    private String processCode;
    /**
     * process type
     */
    @TableField("process_type")
    private Integer processType;
    /**
     * process name
     */
    @TableField("process_name")
    private String processName;
    /**
     * process belonging department id
     */
    @TableField("dep_id")
    private Long deptId;
    /**
     * process remark
     */
    private String remarks;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * create user
     */
    @TableField("create_user")
    private Long createUser;
    /**
     * update time
     */
    @TableField("update_user")
    private Long updateUser;
    /**
     * modify time
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;
    /**
     * is deleted 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * is for all
     */
    @TableField("is_all")
    private Integer isAll;
}