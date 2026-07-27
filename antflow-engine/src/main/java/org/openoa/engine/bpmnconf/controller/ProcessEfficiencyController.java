package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmProcessEfficiency;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.ProcessEfficiencyVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.biz.ProcessEfficiencyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程效能统计 Controller
 *
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@RestController
@RequestMapping("/processEfficiency")
public class ProcessEfficiencyController {

    @Autowired
    private ProcessEfficiencyServiceImpl processEfficiencyService;

    /**
     * 触发效能统计计算
     *
     * @param vo formCodes为空则统计全部
     */
    @PostMapping("/calculate")
    public Result<String> calculate(@RequestBody(required = false) ProcessEfficiencyVo vo) {
        List<String> formCodes = vo != null ? vo.getFormCodes() : null;
        processEfficiencyService.calculateEfficiency(formCodes);
        return Result.newSuccessResult("统计完成");
    }

    /**
     * 分页查询流程级效能数据
     */
    @PostMapping("/page")
    public Result<ResultAndPage<BpmProcessEfficiency>> page(@RequestBody ProcessEfficiencyVo vo) {
        return Result.newSuccessResult(processEfficiencyService.pageProcessLevel(vo));
    }

    /**
     * 查询节点级效能数据(展开流程行时调用)
     */
    @GetMapping("/nodes")
    public Result<List<BpmProcessEfficiency>> nodes(@RequestParam("procInstId") String procInstId) {
        return Result.newSuccessResult(processEfficiencyService.listNodeLevel(procInstId));
    }

    /**
     * 查询任务级效能数据(展开节点行时调用)
     */
    @GetMapping("/tasks")
    public Result<List<BpmProcessEfficiency>> tasks(@RequestParam("procInstId") String procInstId,
                                                    @RequestParam("taskDefKey") String taskDefKey) {
        return Result.newSuccessResult(processEfficiencyService.listTaskLevel(procInstId, taskDefKey));
    }
}
