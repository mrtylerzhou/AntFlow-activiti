package org.openoa.base.entity;
  
import com.baomidou.mybatisplus.annotation.FieldFill;  
import com.baomidou.mybatisplus.annotation.IdType;  
import com.baomidou.mybatisplus.annotation.TableField;  
import com.baomidou.mybatisplus.annotation.TableId;  
import com.baomidou.mybatisplus.annotation.TableName;  
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

@Data  
@TableName("t_bpmn_conf_lf_formdata")  
public class BpmnConfLfFormdata implements TenantField, Serializable {
  
    private static final long serialVersionUID = 1L;  
  
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程配置ID（独立表单为NULL；内联表单指向所属流程的conf id）
     */
    @TableField("bpmn_conf_id")
    private Long bpmnConfId;

    /**
     * 独立表单家族标识（同族各版本共享；内联表单为NULL）
     */
    @TableField("form_code")
    private String formCode;

    /**
     * 独立表单显示名（内联表单为NULL）
     */
    @TableField("form_name")
    private String formName;

    /**
     * 是否当前生效版本 0否 1是（仅独立表单使用；内联表单恒为0）
     */
    @TableField("effective_status")
    private Integer effectiveStatus;

    /**
     * 表单数据（JSON格式）
     */
    @TableField("formdata")
    private String formdata;  
  
    /**  
     * 逻辑删除标记（0：未删除，1：已删除）  
     */  
    @TableLogic  
    @TableField("is_del")  
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
  
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