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
@TableName("t_bpm_variable_view_page_button")
public class BpmVariableViewPageButton {


    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * variable id
     */
    @TableField("variable_id")
    private Long variableId;
    /**
     * view type 1 start user 2 other approvals
     */
    @TableField("view_type")
    private Integer viewType;
    /**
     * button type (1-submit 2-resubmit 3-agree 4 disagree 5 back to modify 6 back to prev node 7 invalid 8 print 9 forward
     */
    @TableField("button_type")
    private Integer buttonType;
    /**
     * button name
     */
    @TableField("button_name")
    private String buttonName;
    /**
     * remark
     */
    private String remark;

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