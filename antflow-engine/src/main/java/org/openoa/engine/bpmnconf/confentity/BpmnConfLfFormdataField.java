package org.openoa.engine.bpmnconf.confentity;
  
import com.baomidou.mybatisplus.annotation.FieldFill;  
import com.baomidou.mybatisplus.annotation.IdType;  
import com.baomidou.mybatisplus.annotation.TableField;  
import com.baomidou.mybatisplus.annotation.TableId;  
import com.baomidou.mybatisplus.annotation.TableName;  
import com.baomidou.mybatisplus.annotation.TableLogic;  
import lombok.Data;  
  
import java.io.Serializable;  
import java.sql.Timestamp;  
  
@Data  
@TableName("t_bpmn_conf_lf_formdata_field")  
public class BpmnConfLfFormdataField implements Serializable {
  
    private static final long serialVersionUID = 1L;  
  
    /**  
     * 主键ID  
     */  
    @TableId(value = "id", type = IdType.AUTO)  
    private Long id;  
  
    /**  
     * BPMN配置ID  
     */  
    @TableField("bpmn_conf_id")  
    private Long bpmnConfId;  
  
    /**  
     * 字段名  
     */  
    @TableField("field_name")  
    private String fieldName;  
  
    /**  
     * 字段类型  
     */  
    @TableField("field_type")  
    private Integer fieldType;  
  
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
    private Timestamp createTime;  
  
    /**  
     * 更新人  
     */  
    @TableField("update_user")  
    private String updateUser;  
  
    /**  
     * 更新时间  
     */  
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)  
    private Timestamp updateTime;  
  
    // 如果需要其他自定义方法，可以在此类中添加  
}