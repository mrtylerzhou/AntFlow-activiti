package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流程实例效能-节点详情 VO
 */
@Data
public class InstanceEfficiencyDetailVo implements Serializable {

    private String taskDefKey;

    private String nodeName;

    /**
     * 节点类型
     * @see org.openoa.base.constant.enums.NodeTypeEnum
     */
    private Integer nodeType;

    private String nodeTypeName;

    /**
     * 人员来源类型
     * @see org.openoa.base.constant.enums.NodePropertyEnum
     */
    private Integer nodeProperty;

    private String nodePropertyName;

    /**
     * 签署类型
     * @see org.openoa.base.constant.enums.SignTypeEnum
     */
    private Integer signType;

    private String signTypeName;

    /**
     * 是否发生过退回
     */
    private Boolean hasRollback;

    /**
     * 最后一轮人员明细
     */
    private List<InstanceEfficiencyAssigneeVo> assignees;
}
