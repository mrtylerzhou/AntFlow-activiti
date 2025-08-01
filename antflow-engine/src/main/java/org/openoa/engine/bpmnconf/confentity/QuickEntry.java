package org.openoa.engine.bpmnconf.confentity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

/**
 * quick entry
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_quick_entry")
public class QuickEntry implements TenantField, Serializable {

    /**
     * variable url mark
     */
    public static final Integer VARIABLE_URL_FLAG_TRUE = 1;
    public static final Integer VARIABLE_URL_FLAG_FALSE = 0;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * app name
     */
    private String title;
    /**
     * effective image url
     */
    @TableField("effective_source")
    private String effectiveSource;
    /**
     * is deleted
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * request url
     */
    private String route;
    /**
     * sort
     */
    private Integer sort;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 0 enable 1 disable
     */
    @TableField("status")
    private Integer status;

    /**
     * variable url flag 0 false 1 true
     */
    @TableField("variable_url_flag")
    private Integer variableUrlFlag;


}
