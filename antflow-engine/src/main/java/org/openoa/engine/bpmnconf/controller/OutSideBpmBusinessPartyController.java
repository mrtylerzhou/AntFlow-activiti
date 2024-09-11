package org.openoa.engine.bpmnconf.controller;


import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.vo.OutSideBpmApplicationVo;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmBusinessPartyController {

    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;

    /**
     * get business party list by page
     *
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/businessParty/listPage")
    public Result listPage(PageDto page, OutSideBpmBusinessPartyVo vo) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.listPage(page, vo));
    }


    /**
     * get business party detail
     *
     * @param id
     * @return
     */
    @GetMapping("/businessParty/detail/{id}")
    public Result detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.detail(id));
    }

    /**
     * edit business party's info
     *
     * @param vo
     * @return
     */
    @PostMapping("/businessParty/edit")
    public Result edit(@RequestBody OutSideBpmBusinessPartyVo vo) {
        outSideBpmBusinessPartyService.edit(vo);
        return Result.newSuccessResult(null);
    }

    /**
     * add application  business party
     *
     * @param
     * @return
     */
    @PostMapping("/businessParty/registerApplication")
    public Result<Long> registerApplication(@RequestBody OutSideBpmApplicationVo vo) {

        Long applicationId = outSideBpmBusinessPartyService.editApplication(vo);

        return Result.newSuccessResult(applicationId);
    }


}
