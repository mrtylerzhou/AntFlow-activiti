package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 业务数据动态列表返回
 * columns + rows + total 结构,前端直接渲染
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataListVo implements Serializable {

    /**
     * 列定义(固定列在前,动态列按配置表id排序在后)
     */
    private List<BusinessDataColumnVo> columns;

    /**
     * 行数据(key-value)
     */
    private List<Map<String, Object>> rows;

    /**
     * 总条数
     */
    private Long total;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessDataColumnVo implements Serializable {
        /**
         * 列key(固定列用固定字段名,动态列用 field_{fieldId})
         */
        private String key;
        /**
         * 列标题
         */
        private String label;
        /**
         * 是否固定列(流程编号等)
         */
        private Boolean fixed;
    }
}
