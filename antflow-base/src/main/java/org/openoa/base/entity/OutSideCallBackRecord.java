package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.openoa.base.constant.enums.CallbackTypeEnum;
import org.openoa.base.constant.enums.MsgProcessEventEnum;
import org.openoa.base.interf.TenantField;

import javax.sql.rowset.serial.SerialArray;
import java.io.Serializable;
import java.util.Date;

/**
 * @Classname OutSideCallBackRecord
 * @Description TODO
 */
@TableName("t_out_side_bpm_call_back_record")
@Getter
@Setter
public class OutSideCallBackRecord implements TenantField, Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * process number
     */
    @TableField("process_number")
    private String processNumber;
    /**
     * push status 0 for success,1 for fail
     */
    @TableField("status")
    private Integer status;
    /**
     * retry times
     */
    @TableField("retry_times")
    private Integer retryTimes;
    /**
     * operation type
     * @see MsgProcessEventEnum
     */
    @TableField("button_operation_type")
    private Integer buttonOperationType;
    /**
     * call back type name
     * @see CallbackTypeEnum
     */
    @TableField("call_back_type_name")
    private String callBackTypeName;
    /**
     * business id
     */
    @TableField("business_id")
    private Long businessId;
    @TableField("form_code")
    private String formCode;
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
     * update usre
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;
}
