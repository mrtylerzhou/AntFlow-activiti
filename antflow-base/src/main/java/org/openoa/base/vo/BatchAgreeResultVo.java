package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Batch agree result VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchAgreeResultVo implements Serializable {
    /**
     * success count
     */
    private Integer successCount;
    /**
     * failure details
     */
    private List<FailureItem> failures;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FailureItem implements Serializable {
        private String taskId;
        private String processNumber;
        private String processName;
        private String reason;
    }
}
