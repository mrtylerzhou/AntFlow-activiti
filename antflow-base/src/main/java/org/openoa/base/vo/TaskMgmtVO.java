package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMgmtVO implements Serializable {
    /**
     * task's id
     */
    private String taskId;
    /**
     * task name
     */
    private String taskName;
    /**
     * process name
     */
    private String processName;
    /**
     * process instance's Id
     */
    private String processInstanceId;
    /**
     * process's id
     */
    public String processId;
    /**
     * process's key
     */
    public String processKey;
    /**
     * process's create time
     */
    private Date createTime;
    /**
     * task's owner
     */
    private String taskOwner;
    /**
     * task's description
     */
    private String description;
    /**
     * task's start time
     */
    private String startTime;

    /**
     * task's  run time
     */
    private Date runTime;
    /**
     * task's end time
     */
    private String endTime;
    /**
     * process's applier
     */
    private String applyUser;
    private String applyUserName;
    /**
     * apply date
     */
    private String applyDate;
    /**
     * applier's department
     */
    private String applyDept;
    /**
     * process manager's name
     */
    private String actualName;
    /**
     * it's department
     */
    private String deptName;
    /**
     * the original operator
     */
    private String originalName;

    /**
     * process Number
     */
    private String processNumber;
    /***
     * process's state
     */
    private String taskState;
    /***
     * task's state,1:finished,2 in approval state
     */
    private Integer taskStype;
    /**
     *
     */
    private Integer processState;
    /**
     * 业务id
     */
    private String businessId;
    /**
     */
    private String handleUserName;
    /**
     * hanle user's name,some brief digest for like match
     */
    private String search;
    /**
     * processType
     */
    private String processType;
    /**
     * processType
     */
    private String processTypeName;
    private Integer type;
    private String code;
    /***
     * process's node key
     */
    private Integer nodeType;
    //process id
    private String id;
    //process's name
    private String name;
    /**
     * user id
     */
    private String userId;
    private String userName;

    /**
     * role ids
     */
    private List<Integer> roleIds;
    /**
     * APP route url
     */
    private String routeUrl;
    /**
     * entry id
     */
    private String entryId;
    /**
     * process version
     */
    private Integer version;

    /**
     * apptime
     */
    private String appTime;
    /**
     * process concern state
     */
    private Integer concernState;
    /**
     * disagree type
     */
    private Integer disagreeType;
    /**
     * operation type
     */
    private Integer operationType;
    /**
     * approve comment
     */
    public String approvalComment;
    /**
     * forwarded user id
     */
    public List<Integer> userIds;
    /**
     * over time url
     */
    public String overtimeUrl;
    /**
     * app left slide
     */
    @Builder.Default
    public Boolean isLeftStroke = true;
    /**
     * app left slide title
     */
    @Builder.Default
    public String title = "同意";
    /**
     * can be submitted in batch
     */
    public Boolean isBatchSubmit;
    /**
     * app process key list
     */
    public List<String> processKeyList;
    /**
     * is forward
     */
    public Boolean isForward;
    /**
     * check whether it is an old process or not
     */
    private Boolean isOld;
    /**
     * batch submit task ids
     */
    public List<String> taskIds;
    /**
     * change handlers in batch
     */
    public List<ContansDataVo> changeHandlers;
    /**
     * create user
     */
    public String createUser;

    /**
     * todoitems count
     */
    public Integer todoCount;
    /**
     * today's done count
     */
    public Integer doneTodayCount;
    /**
     * today's newly created count
     */
    public Integer doneCreateCount;
    /**
     * draft count
     */
    public Integer draftCount;

    public String processCode;


    private List<String> processNumbers;

    /**
     * process digest
     */
    private String processDigest;
    /**
     * is read
     */
    private Integer isRead;
    /**
     * start user avatar
     */
    private String headImg;
    /**
     * start user department path
     */
    private String departmentPath;
    /**
     * apply user id
     */
    private Integer applyUserId;

    /**
     * is third party process
     */
    private Boolean isOutSideProcess;

    /**
     * access type 1 for embedded 2 for api access
     */
    private Integer accessType;

    //===============>>search conditions<<===================

    /**
     * version process keys
     */
    private List<String> versionProcessKeys;
}
