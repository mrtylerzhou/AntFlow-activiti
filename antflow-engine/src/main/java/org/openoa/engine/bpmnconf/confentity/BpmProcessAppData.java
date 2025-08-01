package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;


/**
 * app online process
 * @author TylerZhou
 * @since 0.5
 */
@Data
@Builder
@TableName("bpm_process_app_data")
public class BpmProcessAppData {


   @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;
    /**
     * prcess Name
     */
    @TableField("process_name")
    private String processName;
    /**
     * is online 0 for no 1 for yes
     */
    private Integer state;
    /**
     * APP route
     */
    private String route;
    /**
     * sort
     */
    private Integer sort;
    /**
     * pic source route
     */
    private String source;
    /**
     * is for all
     */
    @TableField("is_all")
    private Integer isAll;
    /**
     * version id
     */
    @TableField("version_id")
    private Long versionId;
    /**
     * application id
     */
    @TableField("application_id")
    private Long applicationId;
    /**
     * 1 for version app,2 for app data
     */
    @TableField("type")
    private Integer type;
 /**
  * 0 for not deleted 1 for deleted
  */
 @TableField("is_del")
 private Integer isDel;
 @TableField("tenant_id")
 private String tenantId;
}
