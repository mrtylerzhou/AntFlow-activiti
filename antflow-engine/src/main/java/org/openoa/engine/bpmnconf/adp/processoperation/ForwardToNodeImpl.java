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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
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
    @Autowired
    private PlatformTransactionManager transactionManager;

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
     * 自动推进节点专用: 延迟到当前事务提交后, 在新事务里 complete + moveTo 推进到目标节点.
     *
     * 为什么不能在 TaskListener create 事件阶段直接执行?
     * TaskListener create 事件在 Activiti CommandContext 内触发, 整个链路
     * (张三 complete → 自动推进节点任务创建 → TaskListener → advanceToTargetNode)
     * 都在同一个 CommandContext 里, 数据库操作都是缓冲未提交的.
     * 此时调 delegateTask.complete() + taskService 查询会看到不一致的中间状态
     * (例如查到已 complete 但删除未落库的上一节点任务, 而非新创建的下一节点任务).
     *
     * 为什么自动节点(nodeType=9)没这个问题?
     * 自动节点 complete 后不需要查询新任务做 moveTo, 纯 complete 让引擎自然流转,
     * CommandContext 关闭时引擎会正确处理. 而自动推进需要在 complete 后查询 + moveTo,
     * 这个组合在嵌套上下文里不可靠.
     *
     * 解决方案: 注册 Spring TransactionSynchronization, 在 afterCommit 阶段
     * (原事务提交, Activiti CommandContext 关闭, 所有操作落库) 用 TransactionTemplate
     * 开启新事务, 在新事务里 taskService.complete(taskId) + moveTo.
     *
     * 代价: 推进失败无法回滚原事务(张三的 complete 已提交), 流程停在自动推进节点, 人工可介入.
     * 这比"状态混乱(李四和王五任务并存)"好得多.
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
        // 提取 delegateTask 信息(事务提交后 delegateTask 不可用, 必须提前取出)
        String taskId = delegateTask.getId();
        String taskName = delegateTask.getName();
        String processDefinitionId = delegateTask.getProcessDefinitionId();

        log.info("自动推进 advanceToTargetNode 注册 afterCommit 回调, procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                procInstId, currentTaskDefKey, targetElementId, targetNodeName);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 正常路径: 注册 afterCommit 回调, 延迟到当前事务提交后执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        new TransactionTemplate(transactionManager).execute(status -> {
                            doAdvanceAfterCommit(procInstId, currentTaskDefKey, targetElementId,
                                    targetNodeName, verifyUserId, verifyUserName,
                                    taskId, taskName, processDefinitionId);
                            return null;
                        });
                    } catch (Exception e) {
                        // afterCommit 阶段原事务已提交, 异常无法回滚原事务.
                        // 新事务已回滚(TransactionTemplate 内部处理), 流程停在自动推进节点.
                        // 记录错误日志供人工排查和介入.
                        log.error("自动推进 afterCommit 执行失败, 流程停在自动推进节点, 请人工处理. procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                                procInstId, currentTaskDefKey, targetElementId, targetNodeName, e);
                    }
                }
            });
        } else {
            // 降级路径: 无事务上下文, 直接在新事务执行
            try {
                new TransactionTemplate(transactionManager).execute(status -> {
                    doAdvanceAfterCommit(procInstId, currentTaskDefKey, targetElementId,
                            targetNodeName, verifyUserId, verifyUserName,
                            taskId, taskName, processDefinitionId);
                    return null;
                });
            } catch (Exception e) {
                log.error("自动推进执行失败, procInstId={}, currentTaskDefKey={}, targetElementId={}",
                        procInstId, currentTaskDefKey, targetElementId, e);
                throw new AFBizException("自动推进失败", e);
            }
        }
    }

    /**
     * 在新事务里执行 complete + moveTo.
     * 此时原事务已提交, 张三 complete 和自动推进节点任务创建都已落库, 查询状态一致.
     */
    private void doAdvanceAfterCommit(String procInstId, String currentTaskDefKey, String targetElementId,
                                      String targetNodeName, String verifyUserId, String verifyUserName,
                                      String taskId, String taskName, String processDefinitionId) {
        log.info("自动推进 doAdvanceAfterCommit 开始, procInstId={}, currentTaskDefKey={}, targetElementId={}",
                procInstId, currentTaskDefKey, targetElementId);

        // Step 1: 查询当前活动任务(此时应为自动推进节点任务, 已落库)
        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(procInstId).active().list();
        if (CollectionUtils.isEmpty(currentTasks)) {
            log.warn("自动推进: 未查到活动任务, 流程可能已结束. procInstId={}", procInstId);
            return;
        }

        // 找到自动推进节点任务(by taskDefKey)
        Task autoAdvanceTask = currentTasks.stream()
                .filter(t -> currentTaskDefKey.equals(t.getTaskDefinitionKey()))
                .findFirst()
                .orElse(null);
        if (autoAdvanceTask == null) {
            log.warn("自动推进: 未查到自动推进节点任务, 可能已被处理. procInstId={}, currentTaskDefKey={}",
                    procInstId, currentTaskDefKey);
            return;
        }

        // Step 2: complete 自动推进节点任务
        // 用 taskService.complete(taskId) 而非 delegateTask.complete(): 在新事务里不嵌套, 引擎正常流转
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, verifyUserName);
        taskService.complete(autoAdvanceTask.getId(), varMap);

        // 记录审批日志
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .verifyDate(new Date())
                .taskName(taskName)
                .taskId(taskId)
                .runInfoId(procInstId)
                .verifyUserId(verifyUserId)
                .verifyUserName(verifyUserName)
                .taskDefKey(currentTaskDefKey)
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(String.format("自动推进:条件满足,推进至节点[%s]", targetNodeName))
                .processCode(processDefinitionId)
                .build());

        // Step 3: 查询 complete 后的新任务
        List<Task> newTasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(newTasks)) {
            log.info("自动推进: complete 后流程已结束, 无需跳转. procInstId={}", procInstId);
            return;
        }

        // 检查新任务是否已经是目标节点
        List<String> newTaskDefKeys = newTasks.stream()
                .map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
        if (newTaskDefKeys.size() == 1 && newTaskDefKeys.get(0).equals(targetElementId)) {
            log.info("自动推进: 目标即为下一节点, 无需跳转. procInstId={}", procInstId);
            return;
        }

        // Step 4: moveTo 到目标节点
        if (newTaskDefKeys.size() == 1) {
            String newCurrentTaskDefKey = newTaskDefKeys.get(0);
            moveToTarget(procInstId, newCurrentTaskDefKey, targetElementId);
        } else {
            // 并行流: 递归 complete
            recursiveCompleteToTarget(newTasks, procInstId, targetElementId,
                    processDefinitionId, null, processDefinitionId);
        }
    }
}
