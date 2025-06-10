package org.openoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-06 19:48
 * @Param
 * @return
 * @Version 1.0
 */
@Data
@TableName("t_biz_account_apply")
public class ThirdPartyAccountApply {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("account_type")
    private Integer accountType;
    @TableField("account_owner_name")
    private String accountOwnerName;
    @TableField("remark")
    private String remark;
}
