package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessNodeVo {
    /**
     * node id
     */
    private String nodeId;

    /**
     * node name
     */
    private String nodeName;
}
