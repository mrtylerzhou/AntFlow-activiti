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
 * third party business access table
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_out_side_bpm_access_business")
public class OutSideBpmAccessBusiness{

    /**
     * auto increment id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * business party id
     */
    @TableField("business_party_id")
    private Long businessPartyId;
    /**
     * conf id
     */
    @TableField("bpmn_conf_id")
    private Long bpmnConfId;
    /**
     * formCode
     */
    @TableField("form_code")
    private String formCode;
    /**
     * process Number
     */
    @TableField("process_number")
    private String processNumber;
    /**
     * form data on the pc
     */
    @TableField("form_data_pc")
    private String formDataPc;
    /**
     * form data on app
     */
    @TableField("form_data_app")
    private String formDataApp;
    /**
     * template mark
     */
    @TableField("template_mark")
    private String templateMark;
    /**
     * start username
     */
    @TableField("start_username")
    private String startUsername;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * as its name says
     */
    @TableField("create_user")
    private String createUser;
    /**
     * as its name says
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * as its name says
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * as its name says
     */
    @TableField("update_time")
    private Date updateTime;

}
