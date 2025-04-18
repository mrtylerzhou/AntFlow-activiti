package org.openoa.engine.bpmnconf.controller;


import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmCallbackUrlConfServiceImpl;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmCallbackUrlConfController {


    @Autowired
    private OutSideBpmCallbackUrlConfServiceImpl outSideBpmCallbackUrlConfService;
    /**
     * query callback conf list by formCode
     *
     * @param formCode
     * @return
     */
    @GetMapping("/callbackUrlConf/list/{formCode}")
    public Result list(@PathVariable("formCode") String formCode) {
        return Result.newSuccessResult(outSideBpmCallbackUrlConfService.selectListByFormCode(formCode));
    }
    /**
     * query callback conf list by page
     *
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/callbackUrlConf/listPage")
    public Result listPage(PageDto page, OutSideBpmCallbackUrlConfVo vo) {
        return Result.newSuccessResult(outSideBpmCallbackUrlConfService.listPage(page, vo));
    }

    /**
     * query a specified callback conf 's detail
     *
     * @param id
     * @return
     */
    @GetMapping("/callbackUrlConf/detail/{id}")
    public Result detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmCallbackUrlConfService.detail(id));
    }

    /**
     * edit a callback conf
     *
     * @param vo
     * @return
     */
    @PostMapping("/callbackUrlConf/edit")
    public Result edit(@RequestBody OutSideBpmCallbackUrlConfVo vo) {
        outSideBpmCallbackUrlConfService.edit(vo);
        return Result.newSuccessResult(null);
    }

}
