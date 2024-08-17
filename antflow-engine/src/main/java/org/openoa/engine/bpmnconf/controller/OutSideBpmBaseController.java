package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBaseServiceImpl;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmBaseController {

    @Autowired
    private OutSideBpmBaseServiceImpl outSideBpmBaseService;

    /**
     * get current user business party list
     *
     * @return
     */
    @GetMapping("/base/getEmplBusinessPartys")
    public Result getEmplBusinessPartys(@RequestParam(name = "name", required = false) String name,
                                        @RequestParam(name = "permCode", required = false) String permCode) {

        List<OutSideBpmBusinessPartyVo> outSideBpmBusinessPartyVos = outSideBpmBaseService.getEmplBusinessPartys(name, new String[]{permCode})
                .stream().filter(o -> o.getId() > 0L)
                .collect(Collectors.toList());

        return Result.newSuccessResult(outSideBpmBusinessPartyVos);
    }

    /**

     * get a list of process by business party id
     *
     * @param businessPartyId
     * @return
     */
    @GetMapping("/base/getEmplBusinessPartys/{businessPartyId}")
    public Result getProcessesByBusinessParty(@PathVariable("businessPartyId") Integer businessPartyId,
                                            @RequestParam(name = "name", required = false) String name) {
        return Result.newSuccessResult(outSideBpmBaseService.getProcessesByBusinessParty(businessPartyId, name));
    }


}
