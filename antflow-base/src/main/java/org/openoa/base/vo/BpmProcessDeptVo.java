package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessDeptVo extends BaseVo {

    /**
     * auto incr id
     */
    private Long id;
    /**
     * process code
     */
    private String processCode;
    /**
     * process type
     */
    private Integer processType;
    /**
     * process type name
     */
    private String processTypeName;
    /**
     * process name
     */
    private String processName;
    /**
     * process belonging department
     */
    private Long deptId;
    /**
     * remarks
     */
    private String remarks;
    /**
     * create time
     */
    private Date createTime;
    /**
     * create user
     */
    private Long createUser;
    /**
     * update user
     */
    private Long updateUser;
    /**
     * update time
     */
    private Date updateTime;
    /**
     * process key
     */
    private String processKey;
    /**
     * department name
     */
    private String deptName;
    /**
     * monitoring department Id list
     */
    public List<BaseIdTranStruVo> controlDeptIdList;
    /**
     * 监控权限员工集合
     * monitoring user  list
     */
    public List<BaseIdTranStruVo> controlUserIdList;
    /**
     * monitoring department id list
     */
    public List<Long> controlDeptIds;
    /**
     * monitoring user id list
     */
    public List<String> controlUserIds;
    /**
     * department list for create permission
     */
    public List<BaseIdTranStruVo> createDeptList;
    /**
     * department id list for create permission
     */
    public List<Long> createDeptIds;


    /**
     * user list for create permission
     */
    public List<BaseIdTranStruVo> createUserList;
    /**
     * user id list for create permission
     */
    public List<String> createUserIds;
    /**
     * notify type list
     */
    public List<BaseIdTranStruVo> notifyTypeList;
    /**
     * notify type id list
     */
    public List<Integer> notifyTypeIds;
    /**
     * remind type list
     */
    public List<Integer> remindTypeIds;
    /**
     * remind type list
     */
    public List<BaseIdTranStruVo> remindTypeList;
    /**
     * user id list for view permission
     */
    public List<String> viewUserIds;
    /**
     * user list for view permission
     */
    public List<BaseIdTranStruVo> viewUserList;
    /**
     * depart id list for view permission
     */
    public List<Long> viewdeptIds;
    /**
     * department list for view permission
     */
    public List<BaseIdTranStruVo> viewdeptList;
    /**
     * node ids
     */
    public List<String> nodeIds;

    /***
     * time out notice time
     */
    public Integer noticeTime;
    /**
     * process node vo list
     */
    public List<ProcessNodeVo> processNodeList;
    /**
     * process name and number fuzzy search
     */
    public String search;
    /**
     * is for all users
     */
    private Integer isAll;

    /**
     * icon id
     */
    private Integer iconId;

    public List<Long> createOfficeIds;

    public List<BaseIdTranStruVo> createOfficeList;

    public List<Long> viewOfficeIds;

    public List<BaseIdTranStruVo> viewOfficeList;
    private List<BpmnTemplateVo> templateVos;

}
