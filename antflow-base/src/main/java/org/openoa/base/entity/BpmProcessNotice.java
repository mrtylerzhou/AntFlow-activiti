package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;

/**
 * process notice
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@TableName("bpm_process_notice")
public class BpmProcessNotice implements TenantField, Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * notice type 1:mail 2:sms 3:app
     */
    private Integer type;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}
