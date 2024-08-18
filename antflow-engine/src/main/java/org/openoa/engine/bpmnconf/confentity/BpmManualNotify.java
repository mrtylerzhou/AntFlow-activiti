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
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_manual_notify")
public class BpmManualNotify {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * business id
     */
    @TableField("business_id")
    private Long businessId;
    /**
     * process code
     */
    private String code;
    /**
     * last remind time
     */
    @TableField("last_time")
    private Date lastTime;

    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;

}