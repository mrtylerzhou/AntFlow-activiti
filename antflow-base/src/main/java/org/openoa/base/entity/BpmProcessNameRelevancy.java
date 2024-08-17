package org.openoa.base.entity;

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
 * process name relevancy
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_name_relevancy")
public class BpmProcessNameRelevancy {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process name id
     */
    @TableField("process_name_id")
    private Long processNameId;
    /**
     * process number
     */
    @TableField("process_key")
    private String processKey;
    @TableField("is_del")
    private Integer isDel;
    @TableField("create_time")
    private Date createTime;

}
