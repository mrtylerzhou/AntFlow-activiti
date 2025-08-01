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
 * node role conf
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_role_conf")
public class BpmnNodeRoleConf {

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
     * role id
     */
    @TableField("role_id")
    private String roleId;

    @TableField("role_name")
    private String roleName;
    /**
     * sign type 1 for all sign 2 for or sign
     */
    @TableField("sign_type")
    private Integer signType;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for no delete 1 for delete
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
