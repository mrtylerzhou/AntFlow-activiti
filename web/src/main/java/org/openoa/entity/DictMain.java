package org.openoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_demo_dict_main")
public class DictMain {
    @TableId(value = "id", type = IdType.AUTO)
    private Long Id;
    @TableField("dict_name")
    private String dictName;
    @TableField("dict_type")
    private String dictType;
    @TableField("is_del")
    private Integer isDel;
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
