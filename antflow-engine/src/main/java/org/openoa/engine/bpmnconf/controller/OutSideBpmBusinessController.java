package org.openoa.engine.bpmnconf.controller;


import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ConfDetailRequestDto;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessAppApplicationServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmApproveTemplateServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.openoa.engine.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping(value = "/outSideBpm")
@Validated
public class OutSideBpmBusinessController {

    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;
    @Autowired
    private BpmProcessAppApplicationServiceImpl outProcessAppApplicationService;

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;

    @Autowired
    private OutSideBpmApproveTemplateServiceImpl outSideBpmApproveTemplateService;

    /**
     * 获取业务方项目信息分页列表
     */
    @PostMapping("/businessParty/listPage")
    public Result listPage(@RequestBody ConfDetailRequestDto dto) {
        PageDto page = dto.getPageDto();
        BpmnConfVo vo = dto.getEntity();
        OutSideBpmBusinessPartyVo searchVo =new OutSideBpmBusinessPartyVo();
        if (!Strings.isNullOrEmpty(vo.getRemark())) {
            searchVo.setName(vo.getRemark());
            searchVo.setRemark(vo.getRemark());
        }
        return Result.newSuccessResult(outSideBpmBusinessPartyService.listPage(page, searchVo));
    }

    /**
     * 获取业务方项目信息详情
     */
    @GetMapping("/businessParty/detail/{id}")
    public Result detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.detail(id));
    }

    /**
     * 编辑业务方项目信息
     */
    @PostMapping("/businessParty/edit")
    public Result edit(@RequestBody OutSideBpmBusinessPartyVo vo) {
        outSideBpmBusinessPartyService.edit(vo);
        return Result.newSuccessResult(null);
    }

    /**
     * 获取业务方项目列表
     */
    @PostMapping("/businessParty/applicationsPageList")
    public Result applicationsPageList(@RequestBody ConfDetailRequestDto dto) {
        PageDto page = dto.getPageDto();
        BpmnConfVo vo = dto.getEntity();
        BpmProcessAppApplicationVo searchVo =new BpmProcessAppApplicationVo();
        if (!Strings.isNullOrEmpty(vo.getRemark())) {
            searchVo.setBusinessName(vo.getRemark());
            searchVo.setProcessKey(vo.getRemark());
        }
        if (Strings.isNullOrEmpty(page.getOrderColumn())) {
            page.setOrderColumn("id");
        }
        return Result.newSuccessResult(outSideBpmBusinessPartyService.applicationsPageList(page, searchVo));
    }

    /**
     * 新增应用
     */
    @PostMapping("/businessParty/addBpmProcessAppApplication")
    public Result<Boolean> addBpmProcessAppApplication(@RequestBody BpmProcessAppApplicationVo vo) {
        boolean ret = outProcessAppApplicationService.addBpmProcessAppApplication(vo);
        return Result.newSuccessResult(ret);
    }

    /**
     * 获取应用详情
     */
    @GetMapping("/businessParty/applicationDetail/{id}")
    public Result applicationDetail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmBusinessPartyService.getApplicationDetailById(id));
    }


    /**
     * 取条件薄板列表分页
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
     * 根据appid获取条件薄板列表
     *
     * @param applicationId
     * @return
     */
    @GetMapping("/conditionTemplate/selectListByTemp/{applicationId}")
    public Result selectConditionListByAppId(@PathVariable("applicationId") Integer applicationId) {
        return Result.newSuccessResult(outSideBpmConditionsTemplateService.selectConditionListByAppId(applicationId));
    }

    /**
     * 新增与编辑条件模板
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
     * 删除条件模板
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
     *
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/approveTemplate/listPage")
    public Result approveTemplateListPage(PageDto page, OutSideBpmApproveTemplateVo vo) {
        return Result.newSuccessResult(outSideBpmApproveTemplateService.listPage(page, vo));
    }

    /**
     * 根据appi 获取审批人模板列表
     *
     * @param applicationId
     * @return
     */
    @GetMapping("/approveTemplate/selectListByTemp/{applicationId}")
    public Result selectApproveListByPartMarkIdAndAppId(@PathVariable("applicationId") Integer applicationId) {
        return Result.newSuccessResult(outSideBpmApproveTemplateService.selectListByTemp(applicationId));
    }

    /**
     * 编辑新增审批人模板
     *
     * @param vo
     * @return
     */
    @PostMapping("/approveTemplate/edit")
    public Result approveTemplateEdit(@RequestBody OutSideBpmApproveTemplateVo vo) {
        outSideBpmApproveTemplateService.edit(vo);
        return Result.success();
    }

    /**
     * 审批人模板详情
     *
     * @param id
     * @return
     */
    @GetMapping("/approveTemplate/detail/{id}")
    public Result approveTemplateDetail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(outSideBpmApproveTemplateService.detail(id));
    }
}
