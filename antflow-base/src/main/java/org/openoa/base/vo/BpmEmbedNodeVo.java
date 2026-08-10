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
public class BpmEmbedNodeVo implements Serializable {


    /**
     * node name
     */
    private String nodeName;

    /**
     * node assignee list
     * 外层=层,内层=层内多人。每层至少 1 人;空层用 [zeroVo] 表示。
     * 层内多人时走会签/或签(由节点 signType 决定)。
     */
    private List<List<BaseIdTranStruVo>> assigneeList;


}
