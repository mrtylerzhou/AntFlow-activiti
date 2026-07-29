package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.vo.FlowClairvoyanceResultVo;
import org.openoa.base.vo.FlowClairvoyanceVo;
import org.openoa.base.exception.AFBizException;
import org.openoa.engine.bpmnconf.service.biz.FlowClairvoyanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import org.openoa.base.entity.Result;

/**
 * 流程千里眼 Controller
 *
 * @author AntFlow
 */
@RestController
@RequestMapping("/flowClairvoyance")
public class FlowClairvoyanceController {

    @Autowired
    private FlowClairvoyanceServiceImpl flowClairvoyanceService;

    /**
     * 分批搜索运行中流程的审批人
     */
    @PostMapping("/search")
    public Result<FlowClairvoyanceResultVo> search(@RequestBody FlowClairvoyanceVo vo) {
        if (CollectionUtils.isEmpty(vo.getUserIds())) {
            throw new AFBizException("请至少选择一个审批人");
        }
        if (vo.getUserIds().size() > 5) {
            throw new AFBizException("最多选择5个审批人");
        }
        return Result.newSuccessResult(flowClairvoyanceService.search(vo));
    }
}
