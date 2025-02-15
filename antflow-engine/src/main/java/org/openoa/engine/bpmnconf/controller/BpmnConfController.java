package org.openoa.engine.bpmnconf.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.confentity.BpmnOutsideConf;
import org.openoa.engine.bpmnconf.service.biz.BpmVerifyInfoBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeToServiceImpl;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.dto.PageDto;
import org.openoa.base.interf.ActivitiService;

import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.bpmnconf.service.biz.ProcessApprovalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Classname BpmnConfController
 * @Description TODO
 * @Date 2022-02-19 16:21
 * @Created by AntOffice
 */
@Tag(name = "工作流配置管理", description = "")
@Slf4j
@RestController
@RequestMapping(value = "/bpmnConf")
public class BpmnConfController {
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmnNodeToServiceImpl bpmnNodeToService;
    @Autowired
    private ProcessApprovalServiceImpl processApprovalService;

    @Autowired
    private BpmnConfCommonServiceImpl bpmnConfCommonService;
    @Autowired
    private BpmVerifyInfoBizServiceImpl bpmVerifyInfoBizService;
    @Autowired(required = false)
    private Map<String, ActivitiService> activitiServices;

    @Autowired
    private BpmnNodeServiceImpl testService;

    @GetMapping("/todoList")
    public Result<TaskMgmtVO> todoList() {
        TaskMgmtVO taskMgmtVO = processApprovalService.processStatistics();
        return Result.newSuccessResult(taskMgmtVO);
    }

    @PostMapping("/edit")
    public Result edit(@Parameter @RequestBody BpmnConfVo bpmnConfVo) {
        bpmnConfService.edit(bpmnConfVo);
        return Result.newSuccessResult("ok");
    }

    @PostMapping("/listPage")
    public Result<ResultAndPage<BpmnConfVo>> listPage(@Parameter @RequestBody ConfDetailRequestDto dto) {
        PageDto page = dto.getPageDto();
        BpmnConfVo vo = dto.getEntity();
        return Result.newSuccessResult(bpmnConfService.selectPage(page, vo));
    }

    /**
     * admin's preview
     */
    @PostMapping("/preview")
    public Result preview(@Parameter @RequestBody String params) {
        bpmVerifyInfoBizService.getVerifyInfoList("DSFZH_WMA_21");
        return Result.newSuccessResult(bpmnConfCommonService.previewNode(params));
    }

    /**
     * start page preview
     */
    @PostMapping("/startPagePreviewNode")
    public Result<PreviewNode> startPagePreviewNode(@Parameter @RequestBody String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        Boolean isStartPreview = jsonObject.getBoolean("isStartPreview");

        if (isStartPreview == null || isStartPreview) {
            return Result.newSuccessResult(bpmnConfCommonService.startPagePreviewNode(params));
        } else {
            return Result.newSuccessResult(bpmnConfCommonService.taskPagePreviewNode(params));
        }

    }

    @GetMapping("/getBpmVerifyInfoVos")
    public Result<List<BpmVerifyInfoVo>> getBpmVerifyInfoVos(@Parameter @RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(bpmVerifyInfoBizService.getBpmVerifyInfoVos(processNumber, false));
    }

    @PostMapping("/process/viewBusinessProcess")
    public Result<BusinessDataVo> viewBusinessProcess(@Parameter @RequestBody String values, @Parameter String formCode) {
        return Result.newSuccessResult(processApprovalService.getBusinessInfo(values, formCode));
    }

    @PostMapping("/process/buttonsOperation")
    public Result buttonsOperation(@Parameter @RequestBody String values, @Parameter String formCode) {
        BusinessDataVo resultData = processApprovalService.buttonsOperation(values, formCode);
        return Result.newSuccessResult(resultData);
    }

    /**
     * effective a config
     *
     * @param id
     * @return
     */
    @GetMapping("/effectiveBpmn/{id}")
    public Result effectiveBpmn(@Parameter @PathVariable("id") Integer id) {
        bpmnConfService.effectiveBpmnConf(id);
        return Result.newSuccessResult(null);
    }

    @RequestMapping("/detail/{id}")
    public Result<BpmnConfVo> detail(@Parameter @PathVariable("id") Integer id) {
        return Result.newSuccessResult(bpmnConfService.detail(id));
    }

    @RequestMapping("/process/listPage/{type}")
    public ResultAndPage<TaskMgmtVO> viewPcProcessList(@Parameter @RequestBody DetailRequestDto requestDto, @Parameter @PathVariable("type") Integer type) throws JiMuBizException {
        PageDto pageDto = requestDto.getPageDto();
        TaskMgmtVO taskMgmtVO = requestDto.getTaskMgmtVO();
        taskMgmtVO.setType(type);
        return processApprovalService.findPcProcessList(pageDto, taskMgmtVO);
    }

    @RequestMapping("/listOutsideConfs")
    public Result<List<BpmnOutsideConf>> listOutsideConfs() {
        Map<String, ActivitiService> activitiServices = this.activitiServices;
        List<BpmnOutsideConf> bpmnOutsideConfs = new ArrayList<>();

        for (String key : activitiServices.keySet()) {
            ActivitiService activitiService = activitiServices.get(key);
            ActivitiServiceAnno annotation = activitiService.getClass().getAnnotation(ActivitiServiceAnno.class);
            String desc = annotation.desc();
            BpmnOutsideConf conf = new BpmnOutsideConf();
            conf.setFormCode(key);
            conf.setBusinessName(desc);
            bpmnOutsideConfs.add(conf);
        }

        return Result.newSuccessResult(bpmnOutsideConfs);
    }
}
