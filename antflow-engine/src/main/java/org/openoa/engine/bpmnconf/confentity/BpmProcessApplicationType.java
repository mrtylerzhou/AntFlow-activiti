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
 *
 * @author TylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_application_type")
public class BpmProcessApplicationType implements TenantField, Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * application id
     */
    @TableField("application_id")
    private Long applicationId;
    /**
     * category id
     */
    @TableField("category_id")
    private Long categoryId;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * sort
     */
    private Integer sort;
    /**
     * is frequently used 0 for no 1 for yes
     */
    private Integer state;
    /***
     * history id
     */
    @TableField("history_id")
    private Long historyId;
    /**
     * 0 for hidden 1 for visible
     */
    @TableField("visble_state")
    private Integer visbleState;
    @TableField("create_time")
    private Date createTime;
    /**
     * common use state
     */
    @TableField("common_use_state")
    private Integer commonUseState;

}
