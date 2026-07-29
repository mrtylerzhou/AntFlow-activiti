package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 流程透视搜索请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPerspectiveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选中的流程formCode列表
     */
    private List<String> formCodes;

    /**
     * 版本模式: RECENT-最近N个版本, EFFECTIVE-仅生效版本
     */
    private String versionMode;

    /**
     * 最近N个版本(versionMode=RECENT时有效)
     */
    private Integer recentN;

    /**
     * 流程偏移量(分批加载, 每批batchSize个流程)
     */
    private Integer offset;

    /**
     * 每批处理的流程数量
     */
    private Integer batchSize;

    /**
     * 筛选条件
     */
    private Filters filters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filters implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 流程名称模糊匹配(bpmnName)
         */
        private String bpmnNameLike;

        /**
         * 是否使用外部表单(LF流程 extraFlags & 64)
         */
        private Boolean useExternalForm;

        /**
         * 表单字段关键字(匹配field的label或name)
         */
        private String formFieldKeyword;

        /**
         * 非发起人节点是否包含可编辑字段权限(E/W)
         */
        private Boolean hasEditableFieldPerm;

        /**
         * 审批人规则类型列表(nodeProperty值, 组内OR)
         */
        private List<Integer> approverRules;

        /**
         * 包含额外增加审批(additionalSignInfoList propertyType=1)
         */
        private Boolean hasAdditionalSign;

        /**
         * 包含额外排除审批(additionalSignInfoList propertyType=2)
         */
        private Boolean hasExcludeSign;

        /**
         * 审批人为空时规则(noHeaderAction值列表, 组内OR)
         */
        private List<Integer> noHeaderActions;

        /**
         * 按钮权限类型列表(buttonType值, 组内OR)
         */
        private List<Integer> buttonTypes;

        /**
         * 是否包含通知(conf级或node级)
         */
        private Boolean hasNotice;

        /**
         * 节点类型列表(nodeType值, 组内OR)
         */
        private List<Integer> nodeTypes;

        /**
         * 审批人去重(deduplicationType > 1)
         */
        private Boolean deduplication;

        /**
         * 允许撤回(viewPageButtons含buttonType=29)
         */
        private Boolean allowRevoke;

        /**
         * 允许作废(viewPageButtons含buttonType=7)
         */
        private Boolean allowCancel;

        /**
         * 允许转发(viewPageButtons含buttonType=15)
         */
        private Boolean allowForward;
    }
}
