package org.openoa.engine.bpmnconf.controller;


import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ConfDetailRequestDto;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAccessBusinessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.openoa.engine.vo.OutSideBpmAccessBusinessVo;
import org.openoa.engine.vo.OutSideBpmAccessRespVo;
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
     * 业务方流程发起
     *
     * @param vo
     * @return
     */
    @PostMapping("/processSubmit")
    public Result<OutSideBpmAccessRespVo> accessBusinessStart(@RequestBody OutSideBpmAccessBusinessVo vo) {
        return Result.newSuccessResult(outSideBpmAccessBusinessService.accessBusinessStart(vo));
    }

    /**
     * 获取OutSide FormCode Page List 模板列表使用
     *
     * @param dto
     * @return
     */
    @PostMapping("/getOutSideFormCodePageList")
    public ResultAndPage<BpmnConfVo> listPage(@RequestBody ConfDetailRequestDto dto) {
        PageDto page = dto.getPageDto();
        BpmnConfVo vo = dto.getEntity();
        return outSideBpmAccessBusinessService.selectOutSideFormCodePageList(page, vo);
    }

    /**
     * 三方接入的流程预览     *
     *
     * @param vo
     * @return
     */
    @PostMapping("/processPreview")
    public Result accessBusinessPreview(@RequestBody OutSideBpmAccessBusinessVo vo) {
        return Result.newSuccessResult(outSideBpmAccessBusinessService.accessBusinessPreview(vo));
    }

    /**
     * outside process break
     *
     * @param vo
     * @return
     */
    @PostMapping("/processBreak")
    public Result accessBusinessBreak(@RequestBody OutSideBpmAccessBusinessVo vo) {
        outSideBpmAccessBusinessService.processBreak(vo);
        return Result.newSuccessResult(null);
    }

    /**
     * get process records
     *
     * @param processNumber
     * @return
     */
    @GetMapping("/outSideProcessRecord")
    public Result outSideProcessRecord(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(outSideBpmAccessBusinessService.outSideProcessRecord(processNumber));
    }

}
