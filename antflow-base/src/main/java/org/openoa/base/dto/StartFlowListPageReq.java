package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发起流程页查询请求(页 = 最多 3 栏)
 * 过滤优先级:流程名称 > formCode > 流程类型(命中前者忽略后者)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartFlowListPageReq implements Serializable {

    /**
     * 第几页
     */
    private Integer page;
    /**
     * 流程名称(like,优先于 formCode/流程类型)
     */
    private String bpmnName;
    /**
     * 流程 formCode(like,优先于流程类型)
     */
    private String formCode;
    /**
     * 流程分类 id;-1 表示未分类(bpmn_type IS NULL)
     */
    private Long categoryId;
}
