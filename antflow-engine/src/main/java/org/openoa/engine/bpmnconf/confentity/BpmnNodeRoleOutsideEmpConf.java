package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_role_outside_emp_conf")
public class BpmnNodeRoleOutsideEmpConf {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("node_id")
    private Long nodeId;
    @TableField("empl_id")
    private String emplId;
    @TableField("empl_name")
    private String emplName;
    /**
     * 0 for normal 1 for delete
     */
    @TableField("is_del")
    private Integer isDel;
    /**
     * as its name says
     */
    @TableField("create_user")
    private String createUser;
    /**
     * as its name says
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * as its name says
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * as its name says
     */
    @TableField("update_time")
    private Date updateTime;
}
