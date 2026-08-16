package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 发起流程页返回:分类块(栏切分后的最小展示单元)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartFlowCategoryVo implements Serializable {

    /**
     * 分类 id,null 表示未分类
     */
    private Long categoryId;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 所属栏 0/1/2
     */
    private Integer column;
    /**
     * 该分类下的流程
     */
    private List<StartFlowVo> flows;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartFlowVo implements Serializable {
        /**
         * 流程业务编码
         */
        private String formCode;
        /**
         * 流程名称
         */
        private String bpmnName;
        /**
         * 流程类型 DIY/LF/OUTSIDE
         */
        private String type;
        /**
         * 关联应用 id(outside 跳转用)
         */
        private Long applicationId;
        /**
         * 创建时间
         */
        private Date createTime;
    }
}
