package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.StartFlowListPageReq;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.vo.StartFlowCategoryVo;

/**
 * 发起流程页(任务中心):按流程分类聚合全部可用流程,三栏流式分页
 */
public interface StartFlowListBizService {

    /**
     * 分页查询(页 = 最多 3 栏,一栏 8 卡片位,分类块不跨栏不跨页)
     * 过滤优先级:流程名称 > formCode > 流程类型
     *
     * @param req page 为第几页;bpmnName/formCode/categoryId 为过滤条件
     */
    ResultAndPage<StartFlowCategoryVo> page(StartFlowListPageReq req);
}
