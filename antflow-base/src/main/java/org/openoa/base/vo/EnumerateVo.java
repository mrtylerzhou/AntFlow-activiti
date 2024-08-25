package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumerateVo implements Serializable {

    private Integer code;

    private String desc;

    /**
     * 是否节点内属性
     */
    private Boolean isInNode;

    /**
     * 节点类型（1发起节点 4审批节点 0节点外）
     */
    private Integer nodeType;

    /**
     * 默认的消息模板
     */
    private BaseIdTranStruVo vo;

    /**
     * 默认的通知对象
     */
    private List<Integer> informIdList;

    private String processCode;
}
