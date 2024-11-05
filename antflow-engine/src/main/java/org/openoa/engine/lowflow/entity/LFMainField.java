package org.openoa.engine.lowflow.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.PipedReader;
import java.util.Date;

@TableName("t_lf_main_field")
public class LFMainField {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("main_id")
    private Long mainId;
    @TableField("field_id")
    private String fieldId;
    @TableField("field_name")
    private String fieldName;
    @TableField("parent_field_id")
    private String parentFieldId;
    @TableField("parent_field_name")
    private String parentFieldName;
    @TableField("field_value")
    private String fieldValue;
    @TableField("field_value_number")
    private Double filedValueNumber;
    @TableField("field_value_dt")
    private Date fieldValueDt;
    private Integer sort=0;

    /**
     * 逻辑删除标记（0：未删除，1：已删除）
     */
    @TableLogic
    @TableField("is_del")
    private Integer isDel;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
