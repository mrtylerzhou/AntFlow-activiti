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
 * tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_app_application")
public class BpmProcessAppApplication{

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * business code,and the main program is default to empty
     */
    @TableField("business_code")
    private String businessCode;
    /**
     * application name
     */
    @TableField("process_name")
    private String title;
    /**
     * app type (1:process 2:app 3:parent app)
     */
    @TableField("apply_type")
    private Integer applyType;
    /**
     * icon for the pc
     */
    @TableField("pc_icon")
    private String pcIcon;
    /**
     * icon for mobile platform
     */
    @TableField("effective_source")
    private String effectiveSource;
    /**
     * is child app ( 0:no 1:yes )
     */
    @TableField("is_son")
    private Integer isSon;
    /**
     * view url
     */
    @TableField("look_url")
    private String lookUrl;
    /**
     * submit url
     */
    @TableField("submit_url")
    private String submitUrl;
    /**
     * condition url
     */
    @TableField("condition_url")
    private String conditionUrl;
    /**
     * parent app id
     */
    @TableField("parent_id")
    private Integer parentId;
    /**
     * application url
     */
    @TableField("application_url")
    private String applicationUrl;
    /**
     * app route
     */
    private String route;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;
    /**
     * permission code
     */
    @TableField("permissions_code")
    private String permissionsCode;
    /**
     * 0 for not deleted 1 for deleted
     */
    @TableField("is_del")
    private Integer isDel;
    /**
     * create user's id
     */
    @TableField("create_user_id")
    private Integer createUserId;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * modify user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * modify time
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * is for all (page no configuration function, default write to the database)
     */
    @TableField("is_all")
    private Integer isAll;
    /**
     * process state (0:no 1:yes)
     */
    private Integer state;
    /**
     * sort
     */
    private Integer sort;
    /**
     * source
     */
    private String source;


}
