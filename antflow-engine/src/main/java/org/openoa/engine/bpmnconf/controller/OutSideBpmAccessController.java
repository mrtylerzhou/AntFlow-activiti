package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAccessBusinessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.openoa.engine.vo.OutSideBpmAccessBusinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/outSide")
@Validated
public class OutSideBpmAccessController {

    @Autowired
    private OutSideBpmAccessBusinessServiceImpl outSideBpmAccessBusinessService;

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;

    /**
     * entry point for  outside process submit via http
     *
     * @param vo
     * @return
     */
    @PostMapping("/processSubmit")
    public Result  accessBusinessStart(@RequestBody OutSideBpmAccessBusinessVo vo) {
        return Result.newSuccessResult(outSideBpmAccessBusinessService.accessBusinessStart(vo));
    }

    /**
     * preview for embedded process
     *
     * @param vo
     * @return
     */
    @PostMapping("/processPreview")
    public Result  accessBusinessPreview(@RequestBody OutSideBpmAccessBusinessVo vo) {
        return Result .newSuccessResult(outSideBpmAccessBusinessService.accessBusinessPreview(vo));
    }

    /**
     * outside process break
     *
     * @param vo
     * @return
     */
    @PostMapping("/processBreak")
    public Result  accessBusinessBreak(@RequestBody OutSideBpmAccessBusinessVo vo) {
        outSideBpmAccessBusinessService.processBreak(vo);
        return Result.newSuccessResult(null);
    }

    /**
     * outside template list
     *
     * @param vo
     * @return
     */
    @PostMapping("/templateList")
    public Result  selectListByPartMark(@RequestBody OutSideBpmAccessBusinessVo vo) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.selectListByPartMark(vo.getBusinessPartyMark()));
    }

    /**
     * get process records
     *
     * @param processNumber
     * @return
     */
    @GetMapping("/outSideProcessRecord")
    public Result  outSideProcessRecord(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(outSideBpmAccessBusinessService.outSideProcessRecord(processNumber));
    }

}
