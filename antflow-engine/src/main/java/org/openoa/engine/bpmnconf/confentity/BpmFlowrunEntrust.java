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
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_flowrun_entrust")
public class BpmFlowrunEntrust {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * that is process instance id, not the task id
     */
    private String runinfoid;
    /**
     * current running task id
     */
    private String runtaskid;
    /**
     * the original assignee
     */
    private String original;
    /**
     * actual assignee
     */
    private String actual;
    /**
     * type 1 for entrust task, 2 for circulate task
     */
    private Integer type;
    /**
     * is read
     */
    @TableField("is_read")
    private Integer isRead;
    /**
     * process key
     */
    @TableField("proc_def_id")
    private String procDefId;
    /**
     * is view
     */
    @TableField("is_view")
    private Integer isView;
}
