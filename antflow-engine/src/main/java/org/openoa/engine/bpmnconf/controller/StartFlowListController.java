package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.StartFlowListPageReq;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.interf.biz.StartFlowListBizService;
import org.openoa.engine.vo.StartFlowCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发起流程(任务中心):按流程分类聚合全部可用流程,三栏流式分页
 */
@Slf4j
@RestController
@RequestMapping(value = "/startFlowList")
public class StartFlowListController {

    @Autowired
    private StartFlowListBizService startFlowListBizService;

    /**
     * 分页(页 = 最多 3 栏);过滤优先级:流程名称 > formCode > 流程类型
     */
    @PostMapping("/page")
    public ResultAndPage<StartFlowCategoryVo> page(@RequestBody StartFlowListPageReq req) {
        return startFlowListBizService.page(req);
    }
}
