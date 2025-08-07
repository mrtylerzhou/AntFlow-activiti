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
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_node_record")
public class BpmProcessNodeRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process instance id
     */
    @TableField("processInstance_id")
    private String processInstanceId;
    /**
     * task id
     */
    @TableField("task_id")
    private String taskId;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
}