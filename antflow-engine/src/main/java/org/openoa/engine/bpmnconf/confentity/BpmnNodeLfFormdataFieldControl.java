package org.openoa.engine.bpmnconf.confentity;
  
import com.baomidou.mybatisplus.annotation.FieldFill;  
import com.baomidou.mybatisplus.annotation.IdType;  
import com.baomidou.mybatisplus.annotation.TableField;  
import com.baomidou.mybatisplus.annotation.TableId;  
import com.baomidou.mybatisplus.annotation.TableName;  
import com.baomidou.mybatisplus.annotation.TableLogic;  
import lombok.Data;  
  
import java.io.Serializable;
import java.util.Date;

@Data  
@TableName("t_bpmn_node_lf_formdata_field_control")  
public class BpmnNodeLfFormdataFieldControl implements Serializable {
  
    private static final long serialVersionUID = 1L;  
  
    /**  
     * 主键ID  
     */  
    @TableId(value = "id", type = IdType.AUTO)  
    private Long id;  
  
    /**  
     * 节点ID  
     */  
    private Long nodeId;  
  
    /**  
     * 表单数据ID  
     */  
    private Long formdataId;  
  
    /**  
     * 字段名  
     */  
    @TableField("field_name")  
    private String fieldName;  

  
    /**  
     * 是否可写（0不限制，1限制）  
     */  
    @TableField("is_visible")
    private Integer isVisible;
  
    /**  
     * 是否可读（即是否限制，0不限制，1限制）  
     */  
    @TableField("is_readonly")
    private Integer isReadonly;
  
    /**  
     * 逻辑删除标记（0：未删除，1：已删除）  
     */  
    @TableLogic  
    @TableField("is_del")  
    private Integer isDel;  
  
    /**  
     * 创建人  
     */  
    @TableField("create_user")  
    private String createUser;  
  
    /**  
     * 创建时间  
     */  
    @TableField(value = "create_time", fill = FieldFill.INSERT)  
    private Date createTime;
  
    /**  
     * 更新人  
     */  
    @TableField("update_user")  
    private String updateUser;  
  
    /**  
     * 更新时间  
     */  
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)  
    private Date updateTime;
  
    // 如果需要其他自定义方法，可以在此类中添加  
}