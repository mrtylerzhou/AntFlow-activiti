package org.openoa.engine.bpmnconf.confentity;


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
 * specified level sign
 * @since 0.5
 * @author AntFlow
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_assign_level_conf")
public class BpmnNodeAssignLevelConf implements TenantField, Serializable {

    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * node id
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;
    /**
     * level type
     */
    @TableField("assign_level_type")
    private Integer assignLevelType;
    /**
     * specified level
     */
    @TableField("assign_level_grade")
    private Integer assignLevelGrade;
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
