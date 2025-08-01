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
 *
 * @Classname BpmnConfNoticeTemplateDetail
 * @Description TODO
 * @Date 2021-11-06 8:12
 * @Created by AntOffice
 * @since 0.5
 */
@Data
@Builder
@TableName("t_bpmn_conf_notice_template_detail")
@AllArgsConstructor
@NoArgsConstructor
public class BpmnConfNoticeTemplateDetail {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * bpmn Code
     */
    @TableField("bpmn_code")
    private String bpmnCode;
    /**
     * notice type
     */
    @TableField("notice_template_type")
    private Integer noticeTemplateType;
    /**
     * notice template detail
     */
    @TableField("notice_template_detail")
    private String noticeTemplateDetail;
    /**
     * 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * update time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update suer
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}