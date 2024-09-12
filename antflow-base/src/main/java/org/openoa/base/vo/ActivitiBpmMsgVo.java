package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitiBpmMsgVo {

    /**
     *message receiver Id
     */
    private String userId;

    /**
     * process id
     */
    private String processId;

    /**
     * bpmn code
     */
    private String bpmnCode;

    /**
     * form code
     */
    public String formCode;

    /**
     * process type
     */
    private String processType;

    /**
     * process name
     */
    private String processName;

    /**
     * process other user's id
     */
    private String otherUserId;

    /**
     * carbon copy array
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
     * app push url
     */
    private String appPushUrl;

    /**
     * task id for in site message
     */
    private String taskId;


}
