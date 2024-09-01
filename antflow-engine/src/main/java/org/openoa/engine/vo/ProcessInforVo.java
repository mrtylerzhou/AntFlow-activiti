package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.BpmBusinessProcess;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInforVo {
    /*
        bpm business process connection
     */
    private BpmBusinessProcess bpmBusinessProcess;

    private String processinessKey;

    private String businessNumber;
    /**
     * receiver Id
     */
    private Integer userId;
    /**
     * process type
     */
    private String processType;
    /**
     * process name
     */
    private String processName;
    /**
     * others id
     */
    private String otherUserId;
    /**
     * carbon copy
     */
    private String[] cc;
    /**
     * email url
     */
    private String emailUrl;

    /**
     * in site message url
     */
    private String url;

    /**
     * App push url
     */
    private String appPushUrl;


    private String taskId;
    /**
     * process operation type
     */
    public Integer type;
    /**
     * node id
     */
    public String nodeId;
    /**
     * form code
     */
    public String formCode;
}
