package org.openoa.engine.bpmnconf.controller;


import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmBusinessParty;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessAppApplicationServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.OutSideBpmApplicationVo;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Tag(name = "第三方业务方管理")
@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmBusinessPartyController {

    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;
    @Autowired
    private BpmProcessAppApplicationServiceImpl outProcessAppApplicationService;

    /**
     * get business party list by page
     */
    @GetMapping("/businessParty/listPage")
    public Result listPage(PageDto page, OutSideBpmBusinessPartyVo vo) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.listPage(page, vo));
    }


    /**
     * get business party detail
     */
    @GetMapping("/businessParty/detail/{id}")
    public Result detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.detail(id));
    }

    /**
     * edit business party's info
     */
    @PostMapping("/businessParty/edit")
    public Result edit(@RequestBody OutSideBpmBusinessPartyVo vo) {
        outSideBpmBusinessPartyService.edit(vo);
        return Result.newSuccessResult(null);
    }

    /**
     * get business Party applications Page List
     */
    @GetMapping("/businessParty/applicationsPageList")
    public Result applicationsPageList(PageDto page, BpmProcessAppApplicationVo vo) {
        if (Strings.isNullOrEmpty(page.getOrderColumn())) {
            page.setOrderColumn("id");
        }
        return Result.newSuccessResult(outSideBpmBusinessPartyService.applicationsPageList(page, vo));
    }

    @GetMapping("/businessParty/getThirdPartyApplications/{businessPartyMark}")
    public Result<List<BpmProcessAppApplicationVo>> getThirdPartyApplications(@PathVariable String businessPartyMark){
        List<BpmProcessAppApplicationVo> thirdPartyApplications = outSideBpmBusinessPartyService.findThirdPartyApplications(businessPartyMark);
        return Result.newSuccessResult(thirdPartyApplications);
    }

    /**
     * add application  business party
     */
    @PostMapping("/businessParty/registerApplication")
    public Result<Long> registerApplication(@RequestBody OutSideBpmApplicationVo vo) {

        Long applicationId = outSideBpmBusinessPartyService.editApplication(vo);

        return Result.newSuccessResult(applicationId);
    }

    /**
     * add application  business party
     */
    @PostMapping("/businessParty/addBpmProcessAppApplication")
    public Result<Boolean> addBpmProcessAppApplication(@RequestBody BpmProcessAppApplicationVo vo) {
        boolean ret = outProcessAppApplicationService.addBpmProcessAppApplication(vo);
        return Result.newSuccessResult(ret);
    }

    /**
     * get business Party applications detail
     */
    @GetMapping("/businessParty/applicationDetail/{id}")
    public Result applicationDetail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.getApplicationDetailById(id));
    }

    /**
     * get business Party
     */
    @GetMapping("/businessParty/getPartyMarkByIdBpmConf/{businessPartyMark}")
    public Result<List<BpmnConfVo>> getPartyMarkByIdBpmConf(@PathVariable String businessPartyMark) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.getBpmConf(businessPartyMark));
    }



    /**
     * get business PartyMark  for key-value
     */
    @GetMapping("/businessParty/getPartyMarkKV")
    public Result getPartyMarkKV() {
        return Result.newSuccessResult(basePartyMark());
    }

    private List<BaseKeyValueStruVo> basePartyMark() {
        List<BaseKeyValueStruVo> results = new ArrayList<>();
        for (OutSideBpmBusinessParty osBpmParty : outSideBpmBusinessPartyService.list()) {
            if (!StringUtils.isEmpty(osBpmParty.getBusinessPartyMark())) {
                results.add(
                        BaseKeyValueStruVo
                                .builder()
                                .key(osBpmParty.getBusinessPartyMark())
                                .value(osBpmParty.getName())
                                .build()
                );
            }
        }
        return results;
    }
}
