package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.StrUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批人推进操作:先同意当前任务,再跳转到未来节点
 * 独立于管理员推进(FastForwardProcessImpl, code=33)
 *
 * 执行逻辑:
 * 1. complete当前任务 + 记录"同意"审批日志
 * 2. 查询新的当前taskDefKey(complete后引擎推进一个节点)
 * 3. 判断是否跨并行网关:
 *    - 不跨: moveTo(新当前taskDefKey, 目标taskDefKey)
 *    - 跨: 递归complete中间任务(记录"推进跳过")
 */
@Service
@Slf4j
public class ForwardToNodeImpl implements ProcessOperationAdaptor {

    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DefaultTaskFlowControlServiceFactory taskFlowControlServiceFactory;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private BpmFlowrunEntrustService bpmFlowrunEntrustService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private BpmVariableMapper bpmVariableMapper;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        if (!StringUtils.hasText(processNumber)) {
            throw new AFBizException("流程编号不能为空");
        }
        // 目标节点: 前端传入nodeId(主键id), 需转换为elementId(taskDefKey)
        String targetNodeId = vo.getForwardToNodeId();
        if (!StringUtils.hasText(targetNodeId)) {
            throw new AFBizException("推进目标节点不能为空");
        }

        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (bpmBusinessProcess == null) {
            throw new AFBizException("未找到流程信息,流程编号:" + processNumber);
        }
        String procInstId = bpmBusinessProcess.getProcInstId();

        // 获取当前任务
        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(currentTasks)) {
            throw new AFBizException("未获取到当前流程任务!");
        }
        Task currentTask = currentTasks.get(0);

        // 将目标nodeId转换为elementId(taskDefKey)
        List<String> targetElementIds = bpmVariableMapper.getElementIdsdByNodeId(processNumber, targetNodeId);
        if (CollectionUtils.isEmpty(targetElementIds)) {
            throw new AFBizException("未能根据nodeId获取目标节点taskDefKey:" + targetNodeId);
        }
        String targetTaskDefKey = targetElementIds.get(0);

        // Step 1: complete当前任务(同意) + 记录审批日志
        String loginEmpId = SecurityUtils.getLogInEmpId();
        String loginEmpName = SecurityUtils.getLogInEmpName();
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, loginEmpName);
        taskService.complete(currentTask.getId(), varMap);

        // 记录"同意"审批日志(含推进信息)
        String comment = vo.getApprovalComment();
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .businessId(bpmBusinessProcess.getBusinessId())
                .verifyUserName(loginEmpName)
                .verifyUserId(loginEmpId)
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .processCode(processNumber)
                .runInfoId(procInstId)
                .verifyDesc("同意(推进至目标节点)" + (StringUtils.hasText(comment) ? ",意见:" + comment : ""))
                .taskName(currentTask.getName())
                .taskId(currentTask.getId())
                .taskDefKey(currentTask.getTaskDefinitionKey())
                .build());

        // Step 2: 查询complete后的新当前任务
        List<Task> newTasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(newTasks)) {
            // 流程已结束(complete后没有新任务),无需推进
            log.info("推进操作:complete后流程已结束,无需跳转. processNumber={}", processNumber);
            return;
        }

        // 检查新当前任务是否已经就是目标节点(即目标恰好是下一个节点)
        List<String> newTaskDefKeys = newTasks.stream()
                .map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
        if (newTaskDefKeys.size() == 1 && newTaskDefKeys.get(0).equals(targetTaskDefKey)) {
            // 目标就是下一个节点,无需额外跳转
            log.info("推进操作:目标即为下一节点,无需跳转. processNumber={}", processNumber);
            return;
        }

        // Step 3: 判断是否跨并行网关
        if (newTaskDefKeys.size() == 1) {
            // 顺序流: 使用moveTo直接跳转
            String newCurrentTaskDefKey = newTaskDefKeys.get(0);
            moveToTarget(procInstId, newCurrentTaskDefKey, targetTaskDefKey);
        } else {
            // 并行流(多个taskDefKey): 使用递归complete方式推进
            recursiveCompleteToTarget(newTasks, procInstId, targetTaskDefKey,
                    processNumber, comment, bpmBusinessProcess.getProcessinessKey());
        }
    }

    /**
     * 不跨并行网关: 使用moveTo跳转
     */
    private void moveToTarget(String procInstId, String currentTaskDefKey, String targetTaskDefKey) {
        TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(procInstId);
        try {
            List<String> unMovedTasks = taskFlowControlService.moveTo(currentTaskDefKey, targetTaskDefKey);
            // 清理未移动的并行任务execution
            if (!CollectionUtils.isEmpty(unMovedTasks)) {
                List<String> toClean = unMovedTasks.stream()
                        .distinct()
                        .filter(k -> !k.equals(currentTaskDefKey))
                        .collect(Collectors.toList());
                if (!toClean.isEmpty()) {
                    taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(procInstId, toClean);
                }
            }
        } catch (Exception e) {
            log.error("推进moveTo失败, procInstId={}, from={}, to={}", procInstId, currentTaskDefKey, targetTaskDefKey, e);
            throw new AFBizException("推进跳转失败,请重试!");
        }
    }

    /**
     * 跨并行网关: 递归complete中间任务直到目标节点
     */
    private void recursiveCompleteToTarget(List<Task> taskList, String processInstanceId,
                                           String forwardToNodeElementId, String processNumber,
                                           String verifyComment, String processKey) {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        String loginEmpId = SecurityUtils.getLogInEmpId();
        String loginEmpName = SecurityUtils.getLogInEmpName();
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, "推进跳过");

        for (Task task : taskList) {
            // 如果当前任务已经是目标节点或目标之后,停止
            if (task.getTaskDefinitionKey().equals(forwardToNodeElementId)) {
                return;
            }
            if (org.openoa.base.constant.enums.ProcessNodeEnum.compare(
                    task.getTaskDefinitionKey(), forwardToNodeElementId) > 0) {
                return;
            }
            taskService.complete(task.getId(), varMap);
            // 记录"推进跳过"审批日志
            bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                    .verifyDate(new Date())
                    .taskName(task.getName())
                    .taskId(task.getId())
                    .runInfoId(processInstanceId)
                    .verifyUserId(loginEmpId)
                    .verifyUserName(loginEmpName)
                    .taskDefKey(task.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc("推进跳过,原因:" + StrUtils.nullOrBlankToWhiteSpace(verifyComment))
                    .processCode(processNumber)
                    .build());
            // 委托记录
            String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(processNumber, task.getTaskDefinitionKey());
            bpmFlowrunEntrustService.addFlowrunEntrust(loginEmpId, loginEmpName,
                    task.getAssignee(), task.getAssigneeName(), task.getTaskDefinitionKey(), 0,
                    processInstanceId, processKey, nodeId, 1);
        }
        // 递归: complete后查询新任务继续推进
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        recursiveCompleteToTarget(tasks, processInstanceId, forwardToNodeElementId,
                processNumber, verifyComment, processKey);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_FORWARD_TO_NODE);
    }

    /**
     * 自动推进节点专用: complete 当前 delegateTask 后, 推进到指定目标 elementId.
     * 与人工推进 {@link #doProcessButton} 的差异:
     * - 入参为 delegateTask(由 NextNodeLabelsProcessor.postProcess 直接提供), 无需再查当前任务列表
     * - 目标节点已转换为 elementId(taskDefKey), 由调用方负责 UUID→主键→elementId 两步转换
     * - verifyUserId/verifyUserName 由调用方传入(自动推进用虚拟人 -3, 人工推进用登录用户)
     * - 推进实现方式: complete 后直接 moveTo(方式3), 不依赖新任务过滤
     *
     * 推进失败抛异常, 由外层事务回滚(问题8方案A).
     *
     * @param delegateTask      当前任务(自动推进节点任务)
     * @param procInstId        流程实例ID
     * @param currentTaskDefKey 当前任务 taskDefinitionKey(= delegateTask.getTaskDefinitionKey())
     * @param targetElementId   目标节点 elementId(taskDefKey)
     * @param targetNodeName    目标节点名称(用于审批日志)
     * @param verifyUserId      审批日志的 verifyUserId(自动推进用 -3)
     * @param verifyUserName    审批日志的 verifyUserName(自动推进用 "自动推进节点自动跳过")
     */
    public void advanceToTargetNode(DelegateTask delegateTask, String procInstId,
                                    String currentTaskDefKey, String targetElementId,
                                    String targetNodeName,
                                    String verifyUserId, String verifyUserName) {
        log.info("自动推进 advanceToTargetNode 开始, procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                procInstId, currentTaskDefKey, targetElementId, targetNodeName);

        // Step 1: complete 当前任务(同意语义) + 记录审批日志
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, verifyUserName);
        ((TaskEntity) delegateTask).complete(varMap, false);

        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .verifyDate(new Date())
                .taskName(delegateTask.getName())
                .taskId(delegateTask.getId())
                .runInfoId(procInstId)
                .verifyUserId(verifyUserId)
                .verifyUserName(verifyUserName)
                .taskDefKey(currentTaskDefKey)
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(String.format("自动推进:条件满足,推进至节点[%s]", targetNodeName))
                .processCode(delegateTask.getProcessDefinitionId())
                .build());

        // Step 2: 查 complete 后的新任务
        List<Task> newTasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(newTasks)) {
            // 流程已结束(complete 后没有新任务), 无需推进
            log.info("自动推进: complete 后流程已结束, 无需跳转. procInstId={}", procInstId);
            return;
        }

        // 检查新任务是否已经是目标节点(目标恰好是下一节点)
        List<String> newTaskDefKeys = newTasks.stream()
                .map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
        if (newTaskDefKeys.size() == 1 && newTaskDefKeys.get(0).equals(targetElementId)) {
            log.info("自动推进: 目标即为下一节点, 无需跳转. procInstId={}", procInstId);
            return;
        }

        // Step 3: 判断是否跨并行网关
        if (newTaskDefKeys.size() == 1) {
            // 顺序流: moveTo 跳转
            String newCurrentTaskDefKey = newTaskDefKeys.get(0);
            moveToTarget(procInstId, newCurrentTaskDefKey, targetElementId);
        } else {
            // 并行流(多个 taskDefKey): 递归 complete 推进
            // 注意: 自动推进场景下 verifyComment 为空, processKey 从 delegateTask 取
            recursiveCompleteToTarget(newTasks, procInstId, targetElementId,
                    delegateTask.getProcessDefinitionId(), null,
                    delegateTask.getProcessDefinitionId());
        }
    }
}
