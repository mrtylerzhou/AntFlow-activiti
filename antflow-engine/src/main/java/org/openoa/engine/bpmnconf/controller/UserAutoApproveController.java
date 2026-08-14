package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.UserAutoApprovePageReq;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.UserAutoApproveVo;
import org.openoa.engine.bpmnconf.service.biz.UserAutoApproveBizServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户自动审批设置
 */
@Slf4j
@RestController
@RequestMapping(value = "/userAutoApprove")
public class UserAutoApproveController {

    @Autowired
    private UserAutoApproveBizServiceImpl userAutoApproveBizService;

    /**
     * 分页列表(带活跃状态实时计算列)
     */
    @PostMapping("/listPage")
    public ResultAndPage<UserAutoApproveVo> listPage(@RequestBody UserAutoApprovePageReq req) {
        return userAutoApproveBizService.listPage(req.getPageDto(), req.getOwnerUserName(), req.getFormCode());
    }

    /**
     * 活跃流程下拉(三类: DIY/LF/第三方)
     */
    @GetMapping("/activeConfList")
    public Result<List<UserAutoApproveVo>> activeConfList() {
        return Result.newSuccessResult(userAutoApproveBizService.activeConfList());
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    public Result save(@RequestBody UserAutoApproveVo vo) {
        userAutoApproveBizService.save(vo);
        return Result.newSuccessResult("ok");
    }

    /**
     * 编辑
     */
    @PostMapping("/update")
    public Result update(@RequestBody UserAutoApproveVo vo) {
        userAutoApproveBizService.update(vo);
        return Result.newSuccessResult("ok");
    }

    /**
     * 启停
     */
    @PostMapping("/toggle/{id}/{enabled}")
    public Result toggle(@PathVariable("id") Long id, @PathVariable("enabled") Integer enabled) {
        userAutoApproveBizService.toggle(id, enabled);
        return Result.newSuccessResult("ok");
    }

    /**
     * 删除(逻辑)
     */
    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        userAutoApproveBizService.delete(id);
        return Result.newSuccessResult("ok");
    }

    /**
     * 复制到最新活跃版本(含节点/表单校验)
     */
    @PostMapping("/copy/{id}")
    public Result copy(@PathVariable("id") Long id) {
        try {
            userAutoApproveBizService.copy(id);
        }catch (AFBizException e){
            return Result.newFailureResult(e.getCode(),e.getMessage());
        }
        return Result.newSuccessResult("ok");
    }
}
