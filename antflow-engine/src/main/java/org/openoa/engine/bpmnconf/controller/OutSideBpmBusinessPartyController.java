package org.openoa.engine.bpmnconf.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.vo.NodeRolePersonVo;
import org.openoa.engine.vo.OutSideBpmApplicationVo;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "第三方业务方管理")
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


    @GetMapping("/businessParty/getPartyMarkByIdBpmConf/{businessPartyMark}")
    public Result<List<BpmnConfVo>> getPartyMarkByIdBpmConf(@PathVariable String businessPartyMark) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.getBpmConf(businessPartyMark));
    }

    @Operation(summary ="角色人员同步")
    @PostMapping("/businessParty/syncRolePersonnel/{businessPartyMark}")
    public  Result<?> syncRolePersonnel(@Parameter(name = "thirdId")@PathVariable String businessPartyMark, @RequestBody NodeRolePersonVo userList){
        outSideBpmBusinessPartyService.syncRolePersonnel(businessPartyMark,userList);
        return Result.newSuccessResult(null);
    }


}
