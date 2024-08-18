package org.openoa.engine.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutSideBpmAccessRespVo implements Serializable {

    /**
     * 流程编号
     */
    private String processNumber;

    /**
     * 业务Id
     */
    private String businessId;

    /**
     * 流程记录列表
     */
    private List<OutSideBpmAccessProcessRecordVo> processRecord;

}
