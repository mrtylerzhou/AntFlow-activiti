package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.BpmProcessCategoryPageReq;
import org.openoa.base.entity.BpmProcessCategory;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessCategoryService;
import org.openoa.engine.vo.BpmProcessCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程分类管理(PC 端)
 * 管理 bpm_process_category 表数据;下拉选项供流程设计器「流程类型」使用
 */
@Slf4j
@RestController
@RequestMapping(value = "/processCategory")
public class BpmProcessCategoryController {

    @Autowired
    private BpmProcessCategoryService bpmProcessCategoryService;

    /**
     * 分页列表
     */
    @PostMapping("/listPage")
    public ResultAndPage<BpmProcessCategoryVo> listPage(@RequestBody BpmProcessCategoryPageReq req) {
        BpmProcessCategoryVo vo = BpmProcessCategoryVo.builder()
                .processTypeName(req.getProcessTypeName())
                .build();
        return bpmProcessCategoryService.selectPage(req.getPageDto(), vo);
    }

    /**
     * 新增/编辑分类
     */
    @PostMapping("/save")
    public Result save(@RequestBody BpmProcessCategoryVo vo) {
        try {
            if (vo.getIsApp() == null) {
                vo.setIsApp(0);
            }
            bpmProcessCategoryService.editProcessCategory(vo);
            return Result.newSuccessResult("ok");
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 分类操作:2 上移 / 3 下移 / 4 删除
     */
    @GetMapping("/operation/{type}/{id}")
    public Result operation(@PathVariable("type") Integer type, @PathVariable("id") Long id) {
        try {
            bpmProcessCategoryService.categoryOperation(type, id);
            return Result.newSuccessResult("ok");
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 下拉选项(流程设计器-基础设置-流程类型)
     * is_del=0,不过滤内置 id、不过滤 is_app
     */
    @GetMapping("/options")
    public Result options() {
        List<BpmProcessCategory> list = bpmProcessCategoryService
                .processCategoryList(new BpmProcessCategoryVo());
        List<BpmProcessCategoryVo> options = list.stream()
                .map(c -> BpmProcessCategoryVo.builder()
                        .id(c.getId())
                        .processTypeName(c.getProcessTypeName())
                        .build())
                .collect(Collectors.toList());
        return Result.newSuccessResult(options);
    }
}
