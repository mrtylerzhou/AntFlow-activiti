package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmApproveTemplateServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.openoa.engine.vo.OutSideBpmApproveTemplateVo;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmTemplateController {

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;

    @Autowired
    private OutSideBpmApproveTemplateServiceImpl outSideBpmApproveTemplateService;
    /**
     * query template conf list by page
     *
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/conditionTemplate/listPage")
    public Result listPage(PageDto page, OutSideBpmConditionsTemplateVo vo) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.listPage(page, vo));
    }
    /**
     * query template conf list by  applicationId
     *
     * @param applicationId
     * @return
     */
    @GetMapping("/conditionTemplate/selectListByTemp/{applicationId}")
    public Result selectConditionListByAppId(@PathVariable("applicationId") Integer applicationId) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.selectConditionListByAppId(applicationId));
    }

    /**
     * query a specified template conf's detail info
     *
     * @param id
     * @return
     */
    @GetMapping("/conditionTemplate/detail/{id}")
    public Result detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.detail(id));
    }

    /**
     * edit template conf
     *
     * @param vo
     * @return
     */
    @PostMapping("/conditionTemplate/edit")
    public Result edit(@RequestBody OutSideBpmConditionsTemplateVo vo) {
        outSideBpmConditionsTemplateService.edit(vo);
        return Result.success();
    }

    /**
     * delete template conf
     *
     * @param id
     * @return
     */
    @GetMapping("/conditionTemplate/delete/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        outSideBpmConditionsTemplateService.delete(id);
        return Result.newSuccessResult(null);
    }

    /**
     * 获取审批人模板列表
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/approveTemplate/listPage")
    public Result approveTemplateListPage(PageDto page, OutSideBpmApproveTemplateVo vo) {
        return Result.newSuccessResult(outSideBpmApproveTemplateService.listPage(page, vo));
    }

    @GetMapping("/approveTemplate/selectListByTemp/{applicationId}")
    public Result selectApproveListByPartMarkIdAndAppId(@PathVariable("applicationId") Integer applicationId) {
        return Result.newSuccessResult(outSideBpmApproveTemplateService.selectListByTemp(applicationId));
    }
    /**
     * edit approve template conf
     * @param vo
     * @return
     */
    @PostMapping("/approveTemplate/edit")
    public Result approveTemplateEdit(@RequestBody OutSideBpmApproveTemplateVo vo) {
        outSideBpmApproveTemplateService.edit(vo);
        return Result.success();
    }
    @GetMapping("/approveTemplate/detail/{id}")
    public Result approveTemplateDetail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmApproveTemplateService.detail(id));
    }

}
