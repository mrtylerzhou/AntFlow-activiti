package org.openoa.engine.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CallbackRespVo implements Serializable {

    /**
     * 返回状态
     */
    private String status;

    /**
     * 对接方返回业务编号
     */
    private String businessId;

    /**
     * 业务方标识
     */
    private String businessPartyMark;

    /**
     * 对接方返回业务扩展信息
     */
    private String extend;

}
