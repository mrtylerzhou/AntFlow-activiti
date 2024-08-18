package org.openoa.engine.vo;

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
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class BpmProcessAppApplicationVo implements Serializable {

    private Integer id;
    /**
     * business code,mainly for third party business,the central system's business code generally set to empty
     */
    private String businessCode;
    private String businessName;
    /**
     * access type (1-embedded; 2-api access)
     */
    private Integer accessType;
    /**
     * application title
     */
    private String title;
    /**
     * application type (1:process 2:application 3:parent application)
     */
    private Integer applyType;
    private String applyTypeName;
    /**
     * pcIcon
     */
    private String pcIcon;
    /**
     * appIcon
     */
    private String effectiveSource;
    /**
     * is child application(0:no 1:yes)
     */
    private Integer isSon;
    /**
     * view url
     */
    private String lookUrl;
    /**
     * submit url
     */
    private String submitUrl;
    /**
     * condition url
     */
    private String conditionUrl;
    /**
     * parent application id
     */
    private Integer parentId;
    /**
     * application url
     */
    private String applicationUrl;
    /**
     * app route link
     */
    private String route;
    /**
     * process key
     */
    private String processKey;
    /**
     * permission code
     */
    private String permissionsCode;
    /**
     * 0 for not deleted and 1 for deleted
     */
    private Integer isDel;
    /**
     * create user
     */
    private Integer createUserId;
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
     * is for all
     */
    private Integer isAll;

    private Integer state;

    private Integer sort;

    private String source;


    /***
     * process key list
     */
    private List<String> processKeyList;
    /**
     * process type
     */
    private List<Long> processTypes;
    private List<String> processTypeNames;
    private List<BpmProcessCategoryVo> processTypeList;
    /**
     * process name
     */
    private String processName;
    /**
     * process type
     */
    private String processTypeName;
    /**
     * process type name
     */
    private String typeName;
    /**
     * type id
     */
    private String typeIds;
    /**
     * is app application
     */
    private String isApp;

    /**
     * entrance
     */
    private String entrance;
    /**
     * process number
     */
    private String processCode;
    private String name;
    /**
     * is visible
     */
    public Integer visbleState;

    //===================
    private String createTimeStr;

    private String search;

    private Integer limitSize;

    /**
     * business code list
     */
    List<String> businessCodeList;
    /**
     * create user name
     */
    private String createUserName;

    /**
     * process category
     */
    private Integer processCategoryId;
    /**
     * application id
     */
    private Integer applicationId;
    /**
     * 1 third party application 2 central system internal application
     */
    private Integer isBusiness;

    private List<Integer> ids;

    /**
     * can be deleted true can be deleted false can not be deleted
     */
    private Boolean isCanDel;
    /**
     * appversion 0 or empty means old version 1 means new version
     */
    private Integer appVersion;

}
