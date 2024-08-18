package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessNodeOvertimeVo implements Serializable {

    private Long id;
    /**
     * inform type 1:email 2:sms 3:app
     */
    private Integer noticeType;
    /**
     * node name
     */
    private String nodeName;
    /**
     * node id
     */
    private String nodeKey;
    /**
     * process belonging department id
     */
    private Long processDepId;
    /**
     * notice time
     */
    private Integer noticeTime;


}
