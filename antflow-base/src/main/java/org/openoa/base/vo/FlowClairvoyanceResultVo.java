package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 流程千里眼 - 响应VO
 *
 * @author AntFlow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowClairvoyanceResultVo implements Serializable {

    /**
     * 命中的流程列表
     */
    private List<ProcessMatchResult> results;

    /**
     * 是否还有更多数据可扫描
     */
    private Boolean hasMore;

    /**
     * 下一次扫描的偏移量
     */
    private Integer nextOffset;

    /**
     * 本次扫描的流程数量
     */
    private Integer scannedCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessMatchResult implements Serializable {
        /**
         * 流程编号
         */
        private String processNumber;
        /**
         * 流程类型(formCode)
         */
        private String processKey;
        /**
         * 流程类型名称
         */
        private String processTypeName;
        /**
         * 发起人
         */
        private String userName;
        /**
         * 创建时间
         */
        private Date createTime;
        /**
         * 流程状态
         */
        private Integer processState;
        /**
         * 命中节点数
         */
        private Integer matchedNodeCount;
        /**
         * 命中人数
         */
        private Integer matchedPersonCount;
        /**
         * 命中节点详情
         */
        private List<MatchedNode> matchedNodes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedNode implements Serializable {
        /**
         * 节点elementId (taskDefKey)
         */
        private String elementId;
        /**
         * 节点名称
         */
        private String elementName;
        /**
         * 命中的审批人列表
         */
        private List<MatchedPerson> matchedPersons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedPerson implements Serializable {
        /**
         * 审批人ID
         */
        private String assignee;
        /**
         * 审批人姓名
         */
        private String assigneeName;
    }
}
