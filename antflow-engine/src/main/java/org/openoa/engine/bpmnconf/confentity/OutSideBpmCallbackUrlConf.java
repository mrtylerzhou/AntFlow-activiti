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
 * business party callback url conf
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_out_side_bpm_callback_url_conf")
public class OutSideBpmCallbackUrlConf {

    private static final long serialVersionUID = 1L;

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
     * bpmn confi id
     */
    @TableField("application_id")
    private Long applicationId;
    @TableField("form_code")
    private String formCode;
    /**
     * conf callback url
     */
    @TableField("bpm_conf_callback_url")
    private String bpmConfCallbackUrl;
    /**
     *  process flow call back url
     */
    @TableField("bpm_flow_callback_url")
    private String bpmFlowCallbackUrl;

    /**
     * appId
     */
    @TableField("api_client_id")
    private String apiClientId;

    /**
     *appSecret
     */
    @TableField("api_client_secret")
    private String apiClientSecret;

    /**
     * 0 for enable,1 for disable
     */
    @TableField("status")
    private Integer status;
    /**
     * remakr
     */
    private String remark;
    /**
     * 0 for normal,1 for delete
     */
    @TableField("is_del")
    private Integer isDel;
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
