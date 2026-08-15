package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.ProcessPermissionsPageReq;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.ProcessPermissionsListVo;
import org.openoa.base.vo.ProcessPermissionsSaveResult;
import org.openoa.base.vo.ProcessPermissionsSaveVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.biz.ProcessPermissionsBizServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程权限管理
 * 管理 bpm_process_permissions 表(查看/创建/监控权限)
 */
@Slf4j
@RestController
@RequestMapping(value = "/processPermissions")
public class BpmProcessPermissionsController {

    @Autowired
    private ProcessPermissionsBizServiceImpl processPermissionsBizService;

    /**
     * 分页列表
     */
    @PostMapping("/listPage")
    public ResultAndPage<ProcessPermissionsListVo> listPage(@RequestBody ProcessPermissionsPageReq req) {
        return processPermissionsBizService.listPage(req);
    }

    /**
     * 批量保存(三层笛卡尔积: 流程×人员/部门×权限类型, 已存在则跳过)
     */
    @PostMapping("/save")
    public Result save(@RequestBody ProcessPermissionsSaveVo vo) {
        try {
            ProcessPermissionsSaveResult result = processPermissionsBizService.save(vo);
            return Result.newSuccessResult(result);
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 删除(物理)
     */
    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        try {
            processPermissionsBizService.delete(id);
            return Result.newSuccessResult("ok");
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }
}
