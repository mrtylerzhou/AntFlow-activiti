package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutSideBpmAccessProcessRecordVo implements Serializable {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 审批时间
     */
    private String approvalTime;

    /**
     * 审批状态
     */
    private Integer approvalStatus;

    /**
     * 审批状态名称
     */
    private String approvalStatusName;

    /**
     * 审批人
     */
    private String approvalUserName;

}
