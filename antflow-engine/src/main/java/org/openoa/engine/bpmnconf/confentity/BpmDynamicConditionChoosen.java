package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_bpm_dynamic_condition_choosen")
public class BpmDynamicConditionChoosen {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("process_number")
    private String processNumber;
    @TableField("node_id")
    private String nodeId;
}
