package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.InstanceEfficiencyDetailVo;
import org.openoa.base.vo.InstanceEfficiencyNodeVo;
import org.openoa.base.vo.InstanceEfficiencySummaryVo;
import org.openoa.engine.bpmnconf.service.biz.ProcessInstanceEfficiencyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程实例效能(流程监控 → 更多 → 效能)
 * 实时计算,不入库。
 *
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@RestController
@RequestMapping("/processInstanceEfficiency")
public class ProcessInstanceEfficiencyController {

    @Autowired
    private ProcessInstanceEfficiencyServiceImpl processInstanceEfficiencyService;

    /**
     * 顶部汇总(当时耗时、流程状态、发起时间)
     */
    @GetMapping("/summary")
    public Result<InstanceEfficiencySummaryVo> summary(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(processInstanceEfficiencyService.getSummary(processNumber));
    }

    /**
     * 节点列表(含耗时、退回标识、进行中标识、TOP3 排名)
     */
    @GetMapping("/nodes")
    public Result<List<InstanceEfficiencyNodeVo>> nodes(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(processInstanceEfficiencyService.listNodes(processNumber));
    }

    /**
     * 节点详情(最后一轮人员明细 + 签署信息)
     */
    @GetMapping("/nodeDetail")
    public Result<InstanceEfficiencyDetailVo> nodeDetail(@RequestParam("processNumber") String processNumber,
                                                         @RequestParam("taskDefKey") String taskDefKey) {
        return Result.newSuccessResult(processInstanceEfficiencyService.getNodeDetail(processNumber, taskDefKey));
    }
}
