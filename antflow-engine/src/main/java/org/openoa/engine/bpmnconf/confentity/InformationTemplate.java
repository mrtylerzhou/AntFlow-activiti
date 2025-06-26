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
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@TableName("t_information_template")
@AllArgsConstructor
@NoArgsConstructor
public class InformationTemplate {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     *  name
     */
    private String name;
    /**
     * number
     */
    private String num;
    /**
     * system title
     */
    @TableField("system_title")
    private String systemTitle;
    /**
     * system con
     */
    @TableField("system_content")
    private String systemContent;
    /**
     * email title
     */
    @TableField("mail_title")
    private String mailTitle;
    /**
     * email content
     */
    @TableField("mail_content")
    private String mailContent;
    /**
     * sms content
     */
    @TableField("note_content")
    private String noteContent;
    /**
     * jump url 1 for process approval page 2 for process detail page 3 for todolist page
     */
    @TableField("jump_url")
    private Integer jumpUrl;
    /**
     * remark
     */
    private String remark;
    /**
     * status 0 for enabled and 1 for disabled
     */
    private Integer status;

    /**
     * @see org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum
     */
    private Integer event;
    @TableField("event_name")
    private String eventName;
    /**
     * is del 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;
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
     * update time
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;

}
