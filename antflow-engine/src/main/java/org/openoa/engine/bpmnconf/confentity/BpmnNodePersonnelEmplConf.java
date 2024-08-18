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
 * @Date 2022-05-01 9:25
 * @Param
 * @return
 * @Version 0.5
 */
@TableName("t_bpmn_node_personnel_empl_conf")
@Getter
@Setter
public class BpmnNodePersonnelEmplConf  {

    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * specified person id
     */
    @TableField("bpmn_node_personne_id")
    private Integer bpmnNodePersonneId;
    /**
     * emp id
     */
    @TableField("empl_id")
    private String emplId;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for delete
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