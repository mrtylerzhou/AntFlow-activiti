package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 流程千里眼 - 请求VO
 *
 * @author AntFlow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowClairvoyanceVo implements Serializable {

    /**
     * 目标审批人ID列表(1~5个, OR关系)
     */
    private List<String> userIds;

    /**
     * 时间范围(天数): 1/3/5/7/15/30/180
     */
    private Integer timeRange;

    /**
     * 节点范围: CURRENT / FUTURE / CURRENT_FUTURE / ALL
     */
    private String nodeScope;

    /**
     * 内部扫描偏移量(前端维护, 首次传0)
     */
    private Integer offset;
}
