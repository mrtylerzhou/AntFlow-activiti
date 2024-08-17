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
 * notice template
 * @author AntFlow
 * @since 0.5
 *
 */
@Data
@Builder
@TableName("t_bpmn_conf_notice_template")
@AllArgsConstructor
@NoArgsConstructor
public class BpmnConfNoticeTemplate {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * bpmn code
     */
    @TableField("bpmn_code")
    private String bpmnCode;
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
