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
 *  third party process service,business party admin person
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_out_side_bpm_admin_personnel")
public class OutSideBpmAdminPersonnel {


    /**
     * auto increment id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * business party id
     */
    @TableField("business_party_id")
    private Long businessPartyId;
    /**
     * administrator type 1-process administrator 2-application administrator 3-interface administrator
     */
    private Integer type;
    /**
     * administrator id
     */
    @TableField("employee_id")
    private Long employeeId;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal, 1 for deleted
     */
    @TableField("is_del")
    private Integer isDel;
    /**
     * crete user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * upate user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}
