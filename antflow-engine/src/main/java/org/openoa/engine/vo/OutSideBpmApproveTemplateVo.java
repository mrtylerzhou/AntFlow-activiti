package org.openoa.engine.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * process for third party system's template conf vo
 * @since 0.73.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class OutSideBpmApproveTemplateVo implements Serializable {

    /**
     * auto incr id
     */
    private Long id;
    /**
     * business party's id
     */
    private Long businessPartyId;
    /**
     * template application id
     */
    private Integer applicationId;
    /**
     * approve_type_id
     */
    private Integer approveTypeId;
    /**
     * approve_type_name
     */
    private String approveTypeName;
    /**
     * api_client_id
     */
    private String apiClientId;
    /**
     * api_client_secret
     */
    private String apiClientSecret;
    /**
     * api_token
     */
    private String apiToken;
    /**
     * api_url
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for delete
     */
    private Integer isDel;
    /**
     * create user
     */
    private String createUser;
    /**
     * create time
     */
    private Date createTime;
    /**
     * update user
     */
    private String updateUser;
    /**
     * update time
     */
    private Date updateTime;

    /**
     * create user id
     */
    private String createUserId;

    //===============>>ext fields<<===================

    /**
     * business party's mark
     */
    private String businessPartyMark;

    /**
     * business party's name
     */
    private String businessPartyName;

    /**
     * create user
     */
    private String createUserName;

    /**
     * application id(formCode)
     */
    private String applicationFormCode;

    /**
     * template application name
     */
    private String applicationName;

    /**
     * template list
     */
    private List<OutSideBpmApproveTemplateVo> templates;

    /**
     * whether it is used or not
     */
    private Boolean isUsed;

    //===============>>query conditions<<===================

    /**
     * 登录人所属业务方列表
     */
    private List<Long> businessPartyIds;

}
