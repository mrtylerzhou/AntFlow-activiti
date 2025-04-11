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
 * hrpb config entity
 * you may notice that where there is a approvement strategy,there is a table behind to support this business
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_customize_conf")
public class BpmnNodeCustomizeConf {


    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * node id
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;
    /**
     * sign type 1 all sign 2 or sign
     */
    @TableField("sign_type")
    private Integer signType;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for deleted
     */
    @TableField("is_del")
    private Integer isDel;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}
