package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-01 9:23
 * @Param
 * @return
 * @Version 0.5
 */
@TableName("t_bpmn_node_personnel_conf")
@Getter
@Setter
public class BpmnNodePersonnelConf {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 节点配置表Id
     */
    @TableField("bpmn_node_id")
    private Integer bpmnNodeId;
    /**
     * 签署类型（1-会签；2-或签）
     */
    @TableField("sign_type")
    private Integer signType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 0:正常,1:删除
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * 创建人（邮箱前缀）
     */
    @TableField("create_user")
    private String createUser;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新人（邮箱前缀）
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
