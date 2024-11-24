package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.ApplicationServiceImpl;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程和部门关联表 前端控制器
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController {
    @Autowired
    private ApplicationServiceImpl applicationService;

    /**
     * 应用添加/修改
     */
    @PostMapping("/edit")
    public Result edit(@RequestBody BpmProcessAppApplicationVo vo) {
        applicationService.edit(vo);
        return Result.success();
    }
    /**
     * 应用删除
     */
    @PostMapping("/del")
    public Result del(BpmProcessAppApplicationVo vo) {
        applicationService.del(vo.getId());
        return Result.success();
    }
    /**
     * 应用分页列表
     */
    @GetMapping("/applicationsList")
    public Result applicationsList(PageDto page, BpmProcessAppApplicationVo vo) {
        return Result.success(applicationService.pageList(page, vo));
    }
    /**
     * 获取父类列表 根据businessCode 如果为null就是内部应用
     */
    @GetMapping("getParentApplicationList")
    public Result getParentApplicationList(BpmProcessAppApplicationVo vo) {
        return Result.success(applicationService.getParentApplicationList(vo));
    }
    /**
     * 获取类别列表
     */
    @GetMapping("/getProcessTypeList")
    public Result getProcessTypeList(BpmProcessAppApplicationVo vo) {
        return Result.success(applicationService.getProcessTypeList(vo));
    }

    /**
     * 获取应用key 根据businessCode
     */
    @GetMapping("/getApplicationKeyList")
    public Result getApplicationKeyList(BpmProcessAppApplicationVo vo) {
        return Result.success(applicationService.getApplicationKeyList(vo));
    }
}
