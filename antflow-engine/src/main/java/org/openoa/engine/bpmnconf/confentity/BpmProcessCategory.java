package org.openoa.engine.bpmnconf.confentity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * process category
 *tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_category")
public class BpmProcessCategory {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process type name
     */
    @TableField("process_type_name")
    private String processTypeName;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    @TableField("state")
    private Integer state;
    /**
     * sort
     */
    private Integer sort;
    /**
     * is for app 0 for no and
     */
    @TableField("is_app")
    private Integer isApp;
    /**
     * entrance
     */
    @TableField("entrance")
    private String entrance;

}
