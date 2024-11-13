package org.openoa.engine.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CallbackReqVo implements Serializable {

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 业务方标识
     */
    private String businessPartyMark;

    /**
     * 表单编号
     */
    private String formCode;

    /**
     * 流程编号
     */
    private String processNum;

    /**
     * 对接方业务编号
     */
    private String businessId;

    /**
     * 流程记录列表
     */
    private List<OutSideBpmAccessProcessRecordVo> processRecord;

}