package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

/**
 * third party process service,condition template conf
 * @author tyler.Zhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_out_side_bpm_conditions_template")
public class OutSideBpmConditionsTemplate implements TenantField, Serializable {


    /**
     * auto increment id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * business party Id
     */
    @TableField("business_party_id")
    private Long businessPartyId;
    /**
     * template mark
     */
    @TableField("template_mark")
    private String templateMark;
    /**
     * template name
     */
    @TableField("template_name")
    private String templateName;
    /**
     * application id
     */
    @TableField("application_id")
    private Integer applicationId;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal, 1 for delete
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

    /**
     * as its name says
     */
    @TableField("create_user_id")
    private String createUserId;

}
