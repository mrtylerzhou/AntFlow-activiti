package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * process record info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessRecordInfoVo {
    /**
     * verify info
     */
    private List<BpmVerifyInfoVo> verifyInfoList;
    /***
     *
     */
    BaseIdTranStruVo employee;
    /***
     * process title
     */
    private String processTitle;
    /**
     * process number
     */
    private String processNumber;
    /**
     * start user id
     */
    private String startUserId;
    /**
     * node type
     */
    private Integer nodeType;
    /***
     * disagree type
     */
    private Integer disagreeType;
    /**
     * task id
     */
    public String taskId;
    /**
     * buttons pc buttons or app buttons
     */
    private List<ProcessActionButtonVo> options;
    /**
     * app below buttons
     */
    private List<ProcessActionButtonVo> appBelowOptions;
    /**
     * type
     */
    private Integer type;
    /***
     * create time
     */
    private Date createTime;
    /**
     * task state
     */
    private String taskState;
    /**
     * pc buttons
     */
    private Map<String, List<ProcessActionButtonVo>> pcButtons;
    /**
     * app buttons
     */
    private Map<String, Map<String, List<ProcessActionButtonVo>>> appButtons;

    /**
     * pc buttons on start page
     */
    private List<ProcessActionButtonVo> initiatePcButtons;
    /**
     * app buttons on start page
     */
    private Map<String, List<ProcessActionButtonVo>> initiateAppButtons;
    /**
     * node id
     */
    private String nodeId;

    private List<String> viewNodeIds;
    /**
     * has custom node
     */
    private Boolean isCustomNode;

    /**
     * process code
     */
    private String processCode;

    private String processKey;

    /**
     * start page init data
     */
    private Object initDatas;
    /**
     * lowcode flow field permissions
     */
    private List<LFFieldControlVO> lfFieldControlVOs;
}
