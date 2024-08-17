package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_button_conf")
public class BpmnNodeButtonConf {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * node id
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;
    /**
     * button page type 1-initiate page 2-approval page
     */
    @TableField("button_page_type")
    private Integer buttonPageType;
    /**
     * button type 1-submit 2-reSubmit 3-agree 4-disagree 5-backToModify 6-backToPreNodeModify 7-cancel 8-print 9-forward
     */
    @TableField("button_type")
    private Integer buttonType;
    /**
     * button name
     */
    @TableField("button_name")
    private String buttonName;
    /**
     * remark
     */
    private String remark;

    @TableField("is_del")
    private Integer isDel;
    /**
     * create by
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update by
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}