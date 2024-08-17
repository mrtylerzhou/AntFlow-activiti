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
 * @Classname BpmVariableButton
 * @Description TODO
 * @Date 2021-11-27 15:25
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bpm_variable_button")
public class BpmVariableButton {

    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * variable id
     */
    @TableField("variable_id")
    private Long variableId;
    /**
     * element id
     */
    @TableField("element_id")
    private String elementId;
    /**
     * button page type 1 start page 2 approve page
     */
    @TableField("button_page_type")
    private Integer buttonPageType;
    /**
     * button type 1 submit 2 re-submit 3 agree 4 disagree 5 back-to-modify 6 back-to-previous-node-modify 7 cancel 8 print 9 forward
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