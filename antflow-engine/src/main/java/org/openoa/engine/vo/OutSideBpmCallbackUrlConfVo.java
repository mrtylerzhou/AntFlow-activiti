package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.DetailedUser;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * 工作流对外服务-业务方回调接口配置表
 * third party process service callback url conf
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class OutSideBpmCallbackUrlConfVo implements Serializable {

    /**
     * auto incr id
     */
    private Long id;
    /**
     * business party id
     */
    private Long businessPartyId;
    /**
     * app id
     */
    private Long applicationId;
    /**
     * conf call back url
     */
    private String bpmConfCallbackUrl;
    /**
     * flow callback url
     */
    private String bpmFlowCallbackUrl;

    /**
     * api id
     */
    private String apiClientId;

    /**
     * api-secret
     */
    private String apiClientSecret;

    /**
     * status 1 for enabled,2 for disabled
     */
    private Integer status;
    /**
     * create user
     */
    private Integer createUserId;
    /**
     * create user name
     */
    private String createUserName;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for deleted
     */
    private Integer isDel;
    /**
     * create user
     */
    private String createUser;
    /**
     * createtime
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

    //===============>>ext fields<<===================

    /**
     * status name
     */
    private String statusName;

    /**
     * business party's name
     */
    private String businessPartyName;

    /**
     * access type 1 for embedded,2 for api access
     */
    private Integer accessType;

    /**
     * access type name
     */
    private String accessTypeName;

    /**
     * api admin list
     */
    private List<DetailedUser> interfaceAdmins;

    /**
     * bpmn name
     */
    private String bpmnName;
    /**
     * form Code
     */
    private String formCode;

    //===============>>seartch conditions<<===================

    /**
     * login user's business party list
     */
    private List<Long> businessPartyIds;

}
