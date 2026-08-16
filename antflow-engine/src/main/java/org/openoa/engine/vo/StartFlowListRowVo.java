package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 发起流程聚合查询行(来源 t_bpmn_conf 有效流程或 DIY 适配器 Bean)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartFlowListRowVo implements Serializable {

    /**
     * 流程业务编码(formCode)
     */
    private String formCode;
    /**
     * 流程名称
     */
    private String bpmnName;
    /**
     * 流程分类 id(bpm_process_category.id,可空)
     */
    private Integer bpmnType;
    /**
     * 是否低代码 0否 1是
     */
    private Integer isLowCodeFlow;
    /**
     * 是否第三方流程 0否 1是
     */
    private Integer isOutSideProcess;
    /**
     * 创建时间(分类内排序用)
     */
    private Date createTime;
    /**
     * 关联应用 id(outside 跳转用,可空)
     */
    private Long applicationId;
    /**
     * 流程类型 DIY/LF/OUTSIDE(派生)
     */
    private String type;
}
