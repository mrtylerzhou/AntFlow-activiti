package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmProcessComment;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessCommentBizService;
import org.openoa.engine.vo.ProcessCommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程沟通接口.
 * 前端审批/查看表单页面, 通过 processNumber 拉取/发送该流程实例下的沟通消息.
 */
@Slf4j
@RestController
@RequestMapping(value = "/bpmnComment")
public class BpmnCommentController {
    @Autowired
    private ProcessCommentBizService processCommentBizService;

    /**
     * 按 processNumber 查询未删除的沟通消息, 按 createTime + id 升序.
     */
    @GetMapping("/list")
    public Result<List<BpmProcessComment>> list(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(processCommentBizService.listComments(processNumber));
    }

    /**
     * 发送根消息或回复.
     */
    @PostMapping("/save")
    public Result<BpmProcessComment> save(@RequestBody ProcessCommentVo vo) {
        return Result.newSuccessResult(processCommentBizService.addComment(vo));
    }

    /**
     * 撤回自己发送的消息.
     */
    @PostMapping("/withdraw")
    public Result<Void> withdraw(@RequestParam("id") Long id) {
        processCommentBizService.withdrawComment(id);
        return Result.newSuccessResult(null);
    }
}
