package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmConditionsTemplateController {

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;

    /**
     * query template conf list by page
     *
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/templateConf/listPage")
    public Result listPage(PageDto page, OutSideBpmConditionsTemplateVo vo) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.listPage(page, vo));
    }

    /**
     * query a specified template conf's detail info
     *
     * @param id
     * @return
     */
    @GetMapping("/templateConf/detail/{id}")
    public Result detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.detail(id));
    }

    /**
     * edit template conf
     *
     * @param vo
     * @return
     */
    @PostMapping("/templateConf/edit")
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
    @GetMapping("/templateConf/delete/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        outSideBpmConditionsTemplateService.delete(id);
        return Result.newSuccessResult(null);
    }

}
