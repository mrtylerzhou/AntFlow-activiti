package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.ProcessPerspectiveResultVo;
import org.openoa.base.vo.ProcessPerspectiveVo;
import org.openoa.engine.bpmnconf.service.biz.ProcessPerspectiveServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程透视 Controller
 */
@Slf4j
@RestController
@RequestMapping("/processPerspective")
public class ProcessPerspectiveController {

    @Autowired
    private ProcessPerspectiveServiceImpl processPerspectiveService;

    /**
     * 分批搜索流程配置
     */
    @PostMapping("/search")
    public Result<ProcessPerspectiveResultVo> search(@RequestBody ProcessPerspectiveVo vo) {
        return Result.newSuccessResult(processPerspectiveService.search(vo));
    }
}
