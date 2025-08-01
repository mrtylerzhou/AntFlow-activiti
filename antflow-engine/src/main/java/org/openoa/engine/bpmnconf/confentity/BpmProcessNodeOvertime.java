package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@TableName("bpm_process_node_overtime")
public class BpmProcessNodeOvertime {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * notice type 1:email 2:sms 3:app
     */
    @TableField("notice_type")
    private Integer noticeType;
    /**
     * node name
     */
    @TableField("node_name")
    private String nodeName;
    /**
     * node id
     */
    @TableField("node_key")
    private String nodeKey;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;
    /**
     * notice time
     */
    @TableField("notice_time")
    private Integer noticeTime;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}