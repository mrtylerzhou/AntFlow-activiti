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
 * process permission
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_permission")
public class BpmProcessPermission {


  @TableId(value = "id", type = IdType.AUTO)
  private Long id;
    /**
     * user id
     */
    @TableField("user_id")
    private String userId;
    /**
     * dept id
     */
    @TableField("dep_id")
    private String depId;
    /**
     * permission type 1 for view 2 for create 3 for monitor
     */
    @TableField("permissions_type")
    private Integer permissionsType;
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
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;

}
