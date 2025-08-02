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
 * loop sign conf
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_loop_conf")
public class BpmnNodeLoopConf implements TenantField, Serializable {


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
     * loop end type,it is used for extensibility purpose,if you system do not have this concept,just feel free to ignore it
     */

    @TableField("loop_end_type")
    private Integer loopEndType;
    /**
     * how many level
     */

    @TableField("loop_number_plies")
    private Integer loopNumberPlies;
    /**
     * end person
     */

    @TableField("loop_end_person")
    private String loopEndPerson;
    /**
     * end staff ids
     */

    @TableField("noparticipating_staff_ids")
    private String noparticipatingStaffIds;
    /**
     * end grade
     */

    @TableField("loop_end_grade")
    private Integer loopEndGrade;
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
