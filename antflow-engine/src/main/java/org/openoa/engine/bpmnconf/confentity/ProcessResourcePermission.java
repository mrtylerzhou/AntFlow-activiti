package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("bpm_process_resource_permission")
public class ProcessResourcePermission implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 流程key
     */
    private String processKey;

    /**
     * 资源code
     */
    @TableField("resource_code")
    private String resourceCode;

    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     *create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * is del 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;

    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * update time
     */
    @TableField("update_user")
    private String updateUser;
}
