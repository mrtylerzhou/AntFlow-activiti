package org.openoa.engine.bpmnconf.adp.processoperation;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.ThirdPartyCallBackServiceImpl;
import org.openoa.base.exception.AFBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ProcessStateEnum.REJECT_STATE;
import static org.openoa.base.constant.enums.ProcessStateEnum.END_STATE;

/**
 * end/abort/disagree a process
 */
@Slf4j
@Component
public class EndProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    protected BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ThirdPartyCallBackServiceImpl thirdPartyCallBackService;
    @Autowired
    private BackToModifyImpl backToModifyImpl;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());

        String verifyUserName = StringUtils.EMPTY;

        String verifyUserId = StringUtils.EMPTY;
        boolean isAbandon=ProcessOperationEnum.BUTTON_TYPE_ABANDON.getCode().equals(vo.getOperationType());
        if (vo.getIsOutSideAccessProc()) {
            Map<String, Object> objectMap = vo.getObjectMap();
            if (!CollectionUtils.isEmpty(objectMap)) {
                verifyUserName = Optional.ofNullable(objectMap.get("employeeName")).map(String::valueOf).orElse(StringUtils.EMPTY);
                verifyUserId = Optional.ofNullable(objectMap.get("employeeId")).map(Object::toString).orElse("");
            }
        } else {
                verifyUserName =SecurityUtils.getLogInEmpName();
                verifyUserId = SecurityUtils.getLogInEmpIdStr();
        }

        Integer processState = vo.getFlag() ? END_STATE.getCode() : REJECT_STATE.getCode();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        if(CollectionUtils.isEmpty(taskList)){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"当前流程实例不存在!");
        }

        // === 不同意退回分叉: 检测formKey中的disagree_back标签 ===
        if (ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE.getCode().equals(vo.getOperationType())) {
            Task currentTask = taskList.stream()
                    .filter(task -> SecurityUtils.getLogInEmpId().equals(task.getAssignee()))
                    .findFirst().orElse(null);
            if (currentTask != null && tryDisagreeBack(vo, currentTask, bpmBusinessProcess)) {
                return;
            }
        }

        Task taskData;
        if (isAbandon) {
            taskData = taskList.get(0);
        } else {
            taskData = taskList.stream().filter(task -> SecurityUtils.getLogInEmpId().equals(task.getAssignee()))
                    .findFirst().orElseThrow(() -> new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(), "当前流程已审批!"));
        }
        //save verify info
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .businessId(bpmBusinessProcess.getBusinessId())
                .verifyUserId(verifyUserId)
                .verifyUserName(verifyUserName)
                .verifyStatus(processState.equals(END_STATE.getCode()) ? ProcessSubmitStateEnum.END_AGRESS_TYPE.getCode() : processState)
                .verifyDate(new Date())
                .processCode(vo.getProcessNumber())
                .verifyDesc(vo.getApprovalComment())
                .taskName(taskData.getName())
                .taskId(taskData.getId())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .build());
        //terminate process (update state + delete process instance + cancellation)
        endProcessWithoutVerify(vo);
    }

    /**
     * terminate a process without recording verify info, so that other handlers
     * (e.g. OpposeProcessImpl) can terminate the process while recording their own verify info.
     *
     * @param vo business data vo
     */
    public void endProcessWithoutVerify(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        String processInstanceId = bpmBusinessProcess.getProcInstId();
        Integer processState = vo.getFlag() ? END_STATE.getCode() : REJECT_STATE.getCode();
        //update process state
        bpmBusinessProcessService.updateBusinessProcess(BpmBusinessProcess.builder()
                .businessNumber(bpmBusinessProcess.getBusinessNumber())
                .processState(processState)
                .build());
        //stop a process
        businessContans.deleteProcessInstance(processInstanceId);
        //call business adaptor method
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        if(!vo.getIsOutSideAccessProc()){
            formFactory.getFormAdaptor(vo).cancellationData(vo);
        }
        vo.setStartUserId(bpmBusinessProcess.getCreateUser());
    }

    /**
     * 尝试执行“不同意退回”逻辑。
     * 检测task.formKey中是否含有af_syslabel_disagree_back标签，
     * 若有则查询节点配置获取backType和backToNodeId，转发给BackToModifyImpl处理。
     *
     * @return true=已转发处理, false=未配置退回,继续走原有结束逻辑
     */
    private boolean tryDisagreeBack(BusinessDataVo vo, Task task, BpmBusinessProcess bpmBusinessProcess) {
        String formKey = task.getFormKey();
        if (StringUtils.isEmpty(formKey)) {
            return false;
        }
        try {
            NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(formKey, NodeExtraInfoDTO.class);
            if (extraInfoDTO == null || !NodeUtil.nodeLabelContainsAny(
                    extraInfoDTO.getNodeLabelVOS(), StringConstants.AF_SYSLABEL_DISAGREE_BACK)) {
                return false;
            }
        } catch (Exception e) {
            log.warn("解析formKey失败,走默认结束流程", e);
            return false;
        }
        // 查询节点配置获取backType和backToNodeId
        String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(
                vo.getProcessNumber(), task.getTaskDefinitionKey());
        if (StringUtils.isEmpty(nodeId)) {
            log.warn("未找到节点映射,走默认结束流程. processNumber={}, taskDefKey={}",
                    vo.getProcessNumber(), task.getTaskDefinitionKey());
            return false;
        }
        BpmnNode bpmnNode = bpmnNodeService.getById(Long.valueOf(nodeId));
        if (bpmnNode == null || StringUtils.isEmpty(bpmnNode.getNodeConfigJson())) {
            log.warn("未找到节点配置,走默认结束流程. nodeId={}", nodeId);
            return false;
        }
        BpmnNodeConfigJson configJson = JSON.parseObject(bpmnNode.getNodeConfigJson(), BpmnNodeConfigJson.class);
        Integer backType = configJson.getBackType();
        String backToNodeId = configJson.getBackToNodeId();
        if (backType == null || (backType != 4 && backType != 5)) {
            return false;
        }
        if (StringUtils.isEmpty(backToNodeId)) {
            log.error("不同意退回配置缺少目标节点! nodeId={}, backType={}", nodeId, backType);
            throw new AFBizException("不同意退回配置缺少目标节点,请联系流程管理员!");
        }
        // 转发给BackToModifyImpl
        vo.setBackToModifyType(backType);
        vo.setBackToNodeId(backToNodeId);
        vo.setTaskId(task.getId());
        backToModifyImpl.doProcessButton(vo);
        return true;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_STOP,
                ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_STOP, ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
    }
}
