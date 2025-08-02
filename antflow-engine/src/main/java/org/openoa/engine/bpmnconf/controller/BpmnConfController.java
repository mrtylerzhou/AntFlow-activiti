package org.openoa.engine.bpmnconf.controller;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.biz.BpmVerifyInfoBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeToServiceImpl;
import org.openoa.base.dto.PageDto;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.exception.AFBizException;
import org.openoa.engine.bpmnconf.service.biz.ProcessApprovalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Classname BpmnConfController
 * @Description TODO
 * @Date 2022-02-19 16:21
 * @Created by AntOffice
 */

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

    /**
     * 首页代办统计
     *
     * @return
     */
    @GetMapping("/todoList")
    public Result<TaskMgmtVO> todoList() {
        TaskMgmtVO taskMgmtVO = processApprovalService.processStatistics();
        return Result.newSuccessResult(taskMgmtVO);
    }

    /**
     * 流程设计发布/复制
     *
     * @param bpmnConfVo
     * @return
     */
    @PostMapping("/edit")
    public Result edit(@RequestBody BpmnConfVo bpmnConfVo) {
        bpmnConfService.edit(bpmnConfVo);
        return Result.newSuccessResult("ok");
    }

    /**
     * 流程设计信息列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/listPage")
    public Result<ResultAndPage<BpmnConfVo>> listPage(@RequestBody ConfDetailRequestDto dto) {
        PageDto page = dto.getPageDto();
        BpmnConfVo vo = dto.getEntity();
        return Result.newSuccessResult(bpmnConfService.selectPage(page, vo));
    }

    /**
     * 流程设计信息预览详情
     */
    @PostMapping("/preview")
    public Result preview( @RequestBody String params) {
        return Result.newSuccessResult(bpmnConfCommonService.previewNode(params));
    }

    /**
     * 发起业务流程（DIY/LF）
     */
    @PostMapping("/startPagePreviewNode")
    public Result<PreviewNode> startPagePreviewNode(@RequestBody String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        Boolean isStartPreview = jsonObject.getBoolean("isStartPreview");

        if (isStartPreview == null || isStartPreview) {
            return Result.newSuccessResult(bpmnConfCommonService.startPagePreviewNode(params));
        } else {
            return Result.newSuccessResult(bpmnConfCommonService.taskPagePreviewNode(params));
        }

    }

    /**
     * 获取审批进度数据信息
     *
     * @param processNumber
     * @return
     */
    @GetMapping("/getBpmVerifyInfoVos")
    public Result<List<BpmVerifyInfoVo>> getBpmVerifyInfoVos(@RequestParam("processNumber") String processNumber) {
        return Result.newSuccessResult(bpmVerifyInfoBizService.getBpmVerifyInfoVos(processNumber, false));
    }

    /**
     * 获取审批页面按钮权限
     *
     * @param values
     * @param formCode
     * @return
     */
    @PostMapping("/process/viewBusinessProcess")
    public Result<BusinessDataVo> viewBusinessProcess(@RequestBody String values, String formCode) {
        return Result.newSuccessResult(processApprovalService.getBusinessInfo(values, formCode));
    }

    /**
     * 审批页：审批,发起，重新提交 operationType 1 发起 2 重新提交 3 审批
     *
     * @param values
     * @param formCode
     * @return
     */
    @PostMapping("/process/buttonsOperation")
    public Result buttonsOperation(@RequestBody String values, String formCode) {
        BusinessDataVo resultData = processApprovalService.buttonsOperation(values, formCode);
        return Result.newSuccessResult(resultData);
    }

    /**
     * 流程设计列表：启用
     *
     * @param id
     * @return
     */
    @GetMapping("/effectiveBpmn/{id}")
    public Result effectiveBpmn(@PathVariable("id") Integer id) {
        bpmnConfService.effectiveBpmnConf(id);
        return Result.newSuccessResult(null);
    }

    /**
     * 流程设计详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/detail/{id}")
    public Result<BpmnConfVo> detail(@PathVariable("id") Integer id) {
        return Result.newSuccessResult(bpmnConfService.detail(id));
    }

    /**
     * 流程列表  3我的发起，4我的已办，5我的代办，6所有进行中实例，9抄送到我
     *
     * @param requestDto
     * @param type
     * @return
     * @throws AFBizException
     */
    @RequestMapping("/process/listPage/{type}")
    public ResultAndPage<TaskMgmtVO> viewPcProcessList(@RequestBody DetailRequestDto requestDto, @PathVariable("type") Integer type) throws AFBizException {
        PageDto pageDto = requestDto.getPageDto();
        TaskMgmtVO taskMgmtVO = requestDto.getTaskMgmtVO();
        taskMgmtVO.setType(type);
        return processApprovalService.findPcProcessList(pageDto, taskMgmtVO);
    }
}
