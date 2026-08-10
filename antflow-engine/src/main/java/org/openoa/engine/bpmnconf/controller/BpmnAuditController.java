package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmProcessAudit;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.biz.ProcessAuditBizServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessAuditBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程表单字段变更审计查询接口.
 * 前端审批/查看表单页面, 通过 processNumber 拉取该流程实例下所有节点的字段变更记录.
 */
@Slf4j
@RestController
@RequestMapping(value = "/bpmnAudit")
public class BpmnAuditController {
    @Autowired
    private ProcessAuditBizService processAuditBizService;

    /**
     * 按 processNumber 查询所有审计记录, 按 taskDefKey + createTime 升序.
     * 返回字段: id / processNumber / formCode / fieldName / oldValue / newValue /
     * taskName / taskDefKey / createUser / createTime.
     */
    @GetMapping("/list")
    public Result<List<BpmProcessAudit>> list(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(processAuditBizService.getProcessAudits(processNumber));
    }
}