package org.openoa.base.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ES query result container.
 *
 * @Author tylerzhou
 */
@Data
public class EsQueryResult {
    private long total;
    private List<String> jsonResults = new ArrayList<>();
}
