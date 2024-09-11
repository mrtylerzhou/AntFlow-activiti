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
 * verify info
 * @author TylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_verify_info")
public class BpmVerifyInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process instance id
     */
    @TableField("run_info_id")
    private String runInfoId;
    /**
     * verify user
     */
    @TableField("verify_user_id")
    private String verifyUserId;
    /**
     * verify user name
     */
    @TableField("verify_user_name")
    private String verifyUserName;
    /**
     * verify status 1 submit 2 agree 3 not agree
     */
    @TableField("verify_status")
    private Integer verifyStatus;
    /**
     * verify desc
     */
    @TableField("verify_desc")
    private String verifyDesc;
    /**
     * verify date
     */
    @TableField("verify_date")
    private Date verifyDate;
    /**
     * task name
     */
    @TableField("task_name")
    private String taskName;
    /**
     * taskId
     */
    @TableField("task_id")
    private String taskId;
    /**
     * business type
     */
    @TableField("business_type")
    private Integer businessType;
    /**
     * business id
     */
    @TableField("business_id")
    private String businessId;
    /**
     * original assignee id
     */
    @TableField("original_id")
    private String originalId;
    /**
     * process code
     */
    @TableField("process_code")
    private String processCode;

}