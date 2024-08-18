package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: AntFlow
 * @since 0.5
 */
@TableName("t_bpmn_approve_remind")
@Data
public class BpmnApproveRemind {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * conf id
     */
    @TableField("conf_id")
    private Long confId;
    /**
     * node id
     */
    @TableField("node_id")
    private Long nodeId;
    /**
     * template id
     */
    @TableField("template_id")
    private Long templateId;
    /**
     * days
     */
    private String days;
    /**
     * is delete,0=false,1=true
     */
    @TableField("is_del")
    private Integer isDel;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;

}