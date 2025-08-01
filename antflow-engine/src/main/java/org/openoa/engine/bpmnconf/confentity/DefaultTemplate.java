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

@Data
@Builder
@TableName("t_default_template")
@AllArgsConstructor
@NoArgsConstructor
public class DefaultTemplate {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * event
     */
    private Integer event;
    /**
     * template id
     */
    @TableField("template_id")
    private Long templateId;
    /**
     *is deleted 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
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
