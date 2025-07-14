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
@TableName("t_bpmn_template")
@AllArgsConstructor
@NoArgsConstructor
public class BpmnTemplate {

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
     * event type
     */
    private Integer event;
    /**
     * inform type
     */
    private String informs;
    /**
     * specified emp
     */
    private String emps;
    /**
     * specified roles
     */
    private String roles;
    /**
     * specified func
     */
    private String funcs;
    /**
     * template id
     */
    @TableField("template_id")
    private Long templateId;
    @TableField("message_send_type")
    private String messageSendType;
    @TableField("form_code")
    private String formCode;
    /**
     * is del 0 for no and 1 for yes
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
     * update time
     */
    @TableField("update_user")
    private String updateUser;

}
