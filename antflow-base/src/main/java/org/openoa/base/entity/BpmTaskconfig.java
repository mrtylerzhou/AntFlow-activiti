package org.openoa.base.entity;

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

/**
 * task config
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_taskconfig")
public class BpmTaskconfig  implements TenantField, Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process def id
     */
    @TableField("proc_def_id_")
    private String procDefId;
    /**
     * task def key
     */
    @TableField("task_def_key_")
    private String taskDefKey;
    /**
     * user id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * user number
     */
    @TableField("number")
    private Integer number;
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}