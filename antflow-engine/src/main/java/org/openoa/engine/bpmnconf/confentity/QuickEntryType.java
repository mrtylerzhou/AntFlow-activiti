package org.openoa.engine.bpmnconf.confentity;

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
 * quick entry type
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_quick_entry_type")
public class QuickEntryType{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * query entry id
     */
    @TableField("quick_entry_id")
    private Long quickEntryId;
    /**
     * type 1 for pc and 2 for app
     */
    private Integer type;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    @TableField("create_time")
    private Date createTime;
    /**
     * type name
     */
    @TableField("type_name")
    private String typeName;

}
