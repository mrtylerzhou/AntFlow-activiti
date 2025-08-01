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
 * node conf for outside access
 *
 * @author TylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bpmn_node_out_side_access_conf")
public class BpmnNodeOutSideAccessConf{


    /**
     * auto increment id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * bpmn_node_id
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;
    /**
     * node mark
     */
    @TableField("node_mark")
    private String nodeMark;
    /**
     * sign type 1 for all sign 2 for or sign
     */
    @TableField("sign_type")
    private Integer signType;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal 1 for delete
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * as its name says
     */
    @TableField("create_user")
    private String createUser;
    /**
     * as its name says
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * as its name says
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * as its name says
     */
    @TableField("update_time")
    private Date updateTime;

}
