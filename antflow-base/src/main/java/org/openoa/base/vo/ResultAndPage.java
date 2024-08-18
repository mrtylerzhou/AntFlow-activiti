package org.openoa.base.vo;

import lombok.Data;
import org.openoa.base.dto.PageDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author AntFlow
 * @since 0.0.1
 * @param <T>
 */
@Data
public class ResultAndPage<T> implements Serializable {
    /**
     * back data
     */
    private List<T> data;
    /**
     * pagination
     */
    private PageDto pagination;
    /**
     * statics
     */
    private Map<String, Object> statistics;
    /**
     * sorting fields
     */
    private Map<String, String> sortColumnMap;
    /**
     * page flag
     */
    private Integer flag;

    private String requestId;
    private Integer code;
    public ResultAndPage(List<T> data, PageDto pagination) {
        this.data = data;
        this.pagination = pagination;
        this.code=200;
    }

    public ResultAndPage(List<T> data, PageDto pagination, Map<String, Object> statistics) {
        this.data = data;
        this.pagination = pagination;
        this.statistics = statistics;
        this.code=200;
    }

    public ResultAndPage(List<T> data, PageDto pagination, Map<String, Object> statistics, Map<String, String> sortColumnMap) {
        this.data = data;
        this.pagination = pagination;
        this.statistics = statistics;
        this.sortColumnMap = sortColumnMap;
        this.code=200;
    }

    public ResultAndPage(List<T> data, PageDto pagination, Map<String, Object> statistics, Map<String, String> sortColumnMap, Integer flag) {
        this.data = data;
        this.pagination = pagination;
        this.statistics = statistics;
        this.sortColumnMap = sortColumnMap;
        this.flag = flag;
        this.code=200;
    }
}
