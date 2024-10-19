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
 * @Date 2024-10-17 20:06
 * @Param
 * @return
 * @Version 1.0
 */
@Data
@TableName("t_biz_purchase")
public class BizPurchase {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("purchase_user_id")
    private Integer purchaseUserId;
    @TableField("purchase_user_name")
    private String purchaseUserName;
    @TableField("purchase_type")
    private Integer purchaseType;
    @TableField("purchase_time")
    private Date purchaseDate;
    @TableField("plan_procurement_total_money")
    private Double planProcurementTotalMoney;
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
