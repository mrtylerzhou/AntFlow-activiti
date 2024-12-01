package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.Employee;

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
    //todo
    Employee employee;
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
    public List<ProcessActionButtonVo> options;
    /**
     * app below buttons
     */
    public List<ProcessActionButtonVo> appBelowOptions;
    /**
     * type
     */
    public Integer type;
    /***
     * create time
     */
    private Date createTime;
    /**
     * task state
     */
    public String taskState;
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
    public List<ProcessActionButtonVo> initiatePcButtons;
    /**
     * app buttons on start page
     */
    public Map<String, List<ProcessActionButtonVo>> initiateAppButtons;
    /**
     * node id
     */
    public String nodeId;

    /**
     * has custom node
     */
    public Boolean isCustomNode;

    /**
     * process code
     */
    public String processCode;

    public String processKey;

    /**
     * start page init data
     */
    private Object initDatas;
    /**
     * lowcode flow field permissions
     */
    private List<LFFieldControlVO> lfFieldControlVOs;
}
