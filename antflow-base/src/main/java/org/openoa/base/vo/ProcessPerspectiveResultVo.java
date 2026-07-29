package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 流程透视搜索结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPerspectiveResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 按formCode聚合的结果列表
     */
    private List<FormCodeResult> results;

    /**
     * 是否还有更多批次
     */
    private Boolean hasMore;

    /**
     * 本批处理的流程数
     */
    private Integer processedCount;

    /**
     * 用户选中的流程总数
     */
    private Integer totalCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormCodeResult implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 流程类型编码
         */
        private String formCode;

        /**
         * 流程显示名称
         */
        private String displayName;

        /**
         * 流程分类: LF / DIY / OUTSIDE
         */
        private String flowType;

        /**
         * 最新匹配版本(按create_time DESC第一条)
         */
        private VersionMatch latestMatch;

        /**
         * 该formCode下所有匹配的版本
         */
        private List<VersionMatch> allMatches;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VersionMatch implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 配置ID
         */
        private Long confId;

        /**
         * 版本编号
         */
        private String bpmnCode;

        /**
         * 版本名称
         */
        private String bpmnName;

        /**
         * 生效状态 0-未生效 1-生效中
         */
        private Integer effectiveStatus;

        /**
         * 创建时间
         */
        private Date createTime;
    }
}
