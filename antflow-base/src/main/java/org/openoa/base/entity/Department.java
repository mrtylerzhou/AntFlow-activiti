package org.openoa.base.entity;


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
 * department info
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_department")
public class Department {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * name
     */
    private String name;
    /**
     * short name
     */
    @TableField("short_name")
    private String shortName;
    /**
     * parent Id
     */
    @TableField("parent_id")
    private Integer parentId;
    private String path;
    /**
     * department level
     */
    private Integer level;
    /**
     * sort
     */
    private Integer sort;
    /**
     * is deleted 0 for no and 1 for yes
     */
    private Integer isDel;
    /**
     * is hide 0 show 1 hide
     */
    private Integer isHide;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     *create time
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * update time
     */
    @TableField("update_user")
    private String updateUser;


}
