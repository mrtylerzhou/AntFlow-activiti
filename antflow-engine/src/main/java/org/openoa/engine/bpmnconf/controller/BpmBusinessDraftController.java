package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.entity.Result;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessDraftBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/processDraft")
@RestController
public class BpmBusinessDraftController {
    @Autowired
    private BpmProcessDraftBizService bpmProcessDraftBizService;

    @GetMapping("/loadDraft")
    public Result loadDraft(String formCode){
        BusinessDataVo businessDataVo = bpmProcessDraftBizService.loadDraft(formCode, SecurityUtils.getLogInEmpId());
        return Result.newSuccessResult(businessDataVo);
    }
}
