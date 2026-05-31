package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

/**
 * ES query parameter VO (ES-agnostic, no ES library dependencies).
 * Used to pass query conditions to EsClientAdaptor.queryData().
 *
 * @Author tylerzhou
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsQueryParam implements Serializable {
    private int pageNum = 1;
    private int pageSize = 10;
    private Map<String, Object> equalsConditions = new HashMap<>();
    private Map<String, String> matchPhraseConditions = new HashMap<>();
    private Map<String, Object> noEqualsConditions = new HashMap<>();
    private Map<String, String> sortFields = new LinkedHashMap<>();
    private Map<String, Object> orLikeConditions = new HashMap<>();
    private Map<String, Object> orConditions = new HashMap<>();
    private Map<String, List> termsConditions = new HashMap<>();
    /**
     * Nested sort configuration.
     * Key = sort field name (e.g. "taskList.processTime")
     * Value = nested path (e.g. "taskList") and filter field/value
     */
    private Map<String, NestedSortConfig> nestedSortConfigs = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NestedSortConfig implements Serializable {
        private String nestedPath;
        private String filterField;
        private Object filterValue;
    }
}
