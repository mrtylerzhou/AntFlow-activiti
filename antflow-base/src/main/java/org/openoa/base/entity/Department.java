package org.openoa.base.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    private String nameScn;
    /**
     * parent Id
     */
    @TableField("parent_id")
    private Integer parentId;
    /**
     * department path, e.g. /1/2/3 (真实层级关系所在, parent_id 可能不可靠)
     */
    private String path;
    /**
     * 是否叶子节点(非表字段, 接口返回供前端懒加载判断)
     */
    @TableField(exist = false)
    private Boolean isLeaf;

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
    private String createUser;
    private Date createTime;
    private Date updateTime;
    /**
     * modify user
     */
    private String updateUser;

}
