package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Batch agree request VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchAgreeVo implements Serializable {
    /**
     * task ids to approve
     */
    private List<String> taskIds;
    /**
     * approval comment (shared for all tasks)
     */
    private String batchApprovalComment;
}
