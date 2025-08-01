package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("t_user_message_status")
public class UserMessageStatus {


    private Integer id;
    @TableField("user_id")
    private String userId;
    /**
     * message status
     */
    @TableField("message_status")
    private Boolean messageStatus;
    /**
     * email status
     */
    @TableField("mail_status")
    private Boolean mailStatus;

    /**
     * app vibration status
     */
    private Boolean shock;

    /**
     * voice
     */
    private Boolean sound;

    /**
     * is phone number public
     */
    @TableField("open_phone")
    private Boolean openPhone;

    /**
     * is dnd enabled
     */
    @TableField("not_trouble")
    private Boolean notTrouble;

    /**
     * dnd start time
     */
    @TableField("not_trouble_time_begin")
    private Date notTroubleTimeBegin;

    /**
     * dnd end time
     */
    @TableField("not_trouble_time_end")
    private Date notTroubleTimeEnd;


    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("create_user")
    private String createUser;
    @TableField("update_user")
    private String updateUser;
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}
