package org.openoa.engine.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * third party business party
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class OutSideBpmBusinessPartyVo implements Serializable {

    /**
     * auto incr id
     */
    private Long id;
    /**
     * business party mark
     */
    private String businessPartyMark;
    /**
     * business party name
     */
    private String name;

    /**
     * 业务方类型（1-嵌入式；2-接入式）
     * type one for embedding and 2 for api access
     */
    @JSONField(name = "accessType")
    private Integer type;

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

    //===============>>ext fields<<===================

    /**
     * type name
     */
    @JSONField(name = "accessTypeName")
    private String typeName;

    /**
     * process admin
     */
    private String processAdminsStr;

    /**
     * process admin list
     */
    private List<BaseIdTranStruVo> processAdmins;

    /**
     * process admin id list
     */
    private List<Long> processAdminIds;

    /**
     * application admin
     */
    private String applicationAdminsStr;

    /**
     * application admin list
     */
    private List<BaseIdTranStruVo> applicationAdmins;

    /**
     * application admin id list
     */
    private List<Long> applicationAdminIds;

    /**
     * api admin
     */
    public String interfaceAdminsStr;

    /**
     * api admin list
     */
    private List<BaseIdTranStruVo> interfaceAdmins;

    /**
     * api admin id list
     */
    private List<Long> interfaceAdminIds;

    /**
     * template admin
     */
    public String templateAdminsStr;

    /**
     * template admin list
     */
    private List<BaseIdTranStruVo> templateAdmins;

    /**
     * template admin id list
     */
    private List<Long> templateAdminIds;

    //===============>>condition<<===================

    //search condition
    private String search;

}
