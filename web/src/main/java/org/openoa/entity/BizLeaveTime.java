package org.openoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2024-08-26 19:48
 * @Param
 * @return
 * @Version 1.0
 */
@Data
@TableName("t_biz_leavetime")
public class BizLeaveTime {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("leave_user_id")
    private Integer leaveUserId;
    @TableField("leave_user_name")
    private String leaveUserName;
    @TableField("leave_type")
    private Integer leaveType;
    @TableField("begin_time")
    private Date beginDate;
    @TableField("end_time")
    private Date endDate;
    @TableField("leavehour")
    private Double leaveHour;
    @TableField("create_time")
    private Date createTime;
    @TableField("create_user")
    private String createUser;
    @TableField("update_time")
    private Date updateTime;
    @TableField("update_user")
    private String updateUser;
    @TableField("remark")
    private String remark;
}
