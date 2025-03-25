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
 * @Date 2025-03-05 20:06
 * @Param
 * @return
 * @Version 1.0
 */
@Data
@TableName("t_biz_refund")
public class BizRefund {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("refund_user_id")
    private Integer refundUserId;
    @TableField("refund_user_name")
    private String refundUserName;
    @TableField("refund_type")
    private Integer refundType;
    @TableField("refund_date")
    private Date refundDate;
    @TableField("refund_money")
    private Double refundMoney;
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
