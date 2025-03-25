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
@TableName("t_biz_ucar_refuel")
public class BizUcarfuel {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("license_plate_number")
    private String licensePlateNumber;
    @TableField("refuel_time")
    private Date refuelTime;
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
