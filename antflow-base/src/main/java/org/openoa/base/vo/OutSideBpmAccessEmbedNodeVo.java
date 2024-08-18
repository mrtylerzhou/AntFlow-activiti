package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * third party process service - embed nodes
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutSideBpmAccessEmbedNodeVo implements Serializable {

    /**
     * node mark
     */
    private String nodeMark;

    /**
     * node name
     */
    private String nodeName;

    /**
     * node assignee list
     */
    private List<Long> assigneeIdList;


}
