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
 * process node submit
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_node_submit")
public class BpmProcessNodeSubmit {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process instance id
     */
    @TableField("processInstance_Id")
    private String processInstanceId;
    /**

     * back type 1:back to previous node and commit to next node 2:back to initiator and commit to next node
     * 3:back to initiator and commit to back node 4:back to history node and commit to next node
     * 5:back to history node and commit to back node
     */
    @TableField("back_type")
    private Integer backType;
    /**
     * node key
     */
    @TableField("node_key")
    private String nodeKey;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;

    /**
     * state
     *
     * @return
     */
    @TableField("state")
    private Integer state;

}
