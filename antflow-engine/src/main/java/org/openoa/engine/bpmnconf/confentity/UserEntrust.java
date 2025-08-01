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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_entrust")
public class UserEntrust {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * sender
     */
    private String sender;

    @TableField("receiver_id")
    private String receiverId;
    /**
     * receiver name
     */
    @TableField("receiver_name")
    private String receiverName;
    @TableField("power_id")
    private String powerId;
    @TableField("begin_time")
    private Date beginTime;
    @TableField("end_time")
    private Date endTime;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;
    @TableField("create_user")
    private String createUser;
    @TableField("update_user")
    private String updateUser;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}