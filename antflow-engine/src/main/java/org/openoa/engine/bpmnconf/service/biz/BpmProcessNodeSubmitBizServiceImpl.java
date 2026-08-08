package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.entity.BpmProcessNodeSubmit;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.util.NodeUtil;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNodeSubmitBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnBizCustomService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.openoa.engine.bpmnconf.adp.processoperation.ForwardToNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BpmProcessNodeSubmitBizServiceImpl implements BpmProcessNodeSubmitBizService {

    private static final Logger log = LoggerFactory.getLogger(BpmProcessNodeSubmitBizServiceImpl.class);

    @Autowired
    private ProcessNodeJump processJump;
    @Autowired
    protected TaskService taskService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private DefaultTaskFlowControlServiceFactory taskFlowControlServiceFactory;
    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmnNodeService bpmnNodeService;
    @Autowired
    private BpmVariableMapper bpmVariableMapper;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private ForwardToNodeImpl forwardToNodeImpl;
    @Autowired
    private PlatformTransactionManager transactionManager;


    /**
     * 流程审批
     *
     * @param task
     */
    @Override
    public void processComplete(Task task) {

        BpmProcessNodeSubmit processNodeSubmit = this.getService().findBpmProcessNodeSubmit(task.getProcessInstanceId());
        String restoreNodeKey = "";
        Map<String, Object> varMap = new HashMap<>();
        //varMap.put(StringConstants.TASK_ASSIGNEE_NAME,SecurityUtils.getLogInEmpName());
        if (!ObjectUtils.isEmpty(processNodeSubmit)) {
            this.getService().addProcessNode(BpmProcessNodeSubmit.builder()
                    .state(0)
                    .nodeKey(task.getTaskDefinitionKey())
                    .processInstanceId(task.getProcessInstanceId())
                    .backType(0)
                    .createUser(task.getAssignee())
                    .build());
            boolean nextElementParallelGateway=false;
            PvmActivity nextElement = additionalInfoService.getNextElement(task.getTaskDefinitionKey(), task.getProcessInstanceId());
            if (nextElement != null) {
                String type = (String) nextElement.getProperty("type");
                if ("parallelGateway".equals(type)) {
                    nextElementParallelGateway=true;
                    List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
                    List<String> currentTaskDefKeys = tasks.stream().map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
                    if(currentTaskDefKeys.size()<=1){
                        if (nextElement.getOutgoingTransitions().size() > 1) {
                            restoreNodeKey = "";
                        } else {
                            restoreNodeKey = nextElement.getOutgoingTransitions().get(0).getDestination().getId();
                        }
                    }
                }
            }
            if (processNodeSubmit.getState().equals(0)) {
                if (!StringUtils.isEmpty(restoreNodeKey)) {
                    processJump.commitProcess(task.getId(), varMap, restoreNodeKey);
                } else {
                    taskService.complete(task.getId(), varMap);
                }
            } else {
                if(nextElementParallelGateway &&(processNodeSubmit.getBackType()==1||processNodeSubmit.getBackType()==2)){
                    taskService.complete(task.getId(), varMap);
                    return;
                }
                // node disagree type（1：back to previous node submit next node 2：back to initiator submit next node
                // 3. back to initiator submit next node 4. back to history node submit next node 5. back to history node submit back node
                String targetNodeKey = processNodeSubmit.getNodeKey();
                switch (processNodeSubmit.getBackType()) {
                    case 1:
                    case 4:
                        if (isMultiPlayerTargetNode(task, targetNodeKey) && isSingleActiveNode(task)) {
                            moveToMultiPlayerTarget(task, targetNodeKey);
                        } else {
                            processJump.commitProcess(task.getId(), varMap, targetNodeKey);
                        }
                        break;
                    case 2:
                    case 3:
                    case 5:
                        processJump.commitProcess(task.getId(), varMap, processNodeSubmit.getNodeKey());
                        break;
                    default:
                        taskService.complete(task.getId(), varMap);
                        break;
                }
            }
        } else {
            // === 同意推进节点专用分支: complete + Verifyinfo 在原事务, moveTo 在 afterCommit 新事务 ===
            // 仅在同意按钮入口(经过 BpmnSendMessageAspect, ThreadLocal 有 businessDataVo)时启用;
            // 批量审批入口(ThreadLocal 为空)走默认 complete, 不推进
            // 普通同意审批 processNodeSubmit 为空, 走此 else 分支
            if (tryApproveForward(task, varMap)) {
                return;
            }
            taskService.complete(task.getId(), varMap);

            //执行自定义业务逻辑
            Collection<BpmnBizCustomService> beans = SpringBeanUtils.getBeans(BpmnBizCustomService.class);
            if (!ObjectUtils.isEmpty(beans)) {
                for (BpmnBizCustomService bean : beans) {
                    bean.execute(task);
                }
            }
        }
    }

    /**
     * 判断提交目标节点是否为多人节点(会签/或签)
     */
    private boolean isMultiPlayerTargetNode(Task task, String targetNodeKey) {
        if (StringUtils.isEmpty(targetNodeKey)) {
            return false;
        }
        try {
            Map<String, Object> variables = taskService.getVariables(task.getId());
            Object processNumberObj = variables == null ? null : variables.get("processNumber");
            if (processNumberObj == null) {
                return false;
            }
            List<BpmVariableMultiplayer> moreNodes = bpmVariableMultiplayerService.getBaseMapper()
                    .isMoreNode(processNumberObj.toString(), targetNodeKey);
            return !CollectionUtils.isEmpty(moreNodes);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断当前流程实例是否只存在单一活动节点(无并行分支),避免moveTo误删其他并行分支任务
     */
    private boolean isSingleActiveNode(Task task) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        if (tasks.isEmpty()) {
            return false;
        }
        long distinctKeys = tasks.stream().map(TaskInfo::getTaskDefinitionKey).distinct().count();
        return distinctKeys <= 1;
    }

    /**
     * 多人节点作为提交目标时,使用moveTo重建多实例任务,避免强制跳转导致流程中断
     */
    private void moveToMultiPlayerTarget(Task task, String targetNodeKey) {
        TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(task.getProcessInstanceId());
        try {
            List<String> unMovedTasks = taskFlowControlService.moveTo(task.getTaskDefinitionKey(), targetNodeKey);
            List<String> strings = unMovedTasks.stream().distinct().collect(Collectors.toList());
            if (strings.size() > 0) {
                strings = strings.stream().filter(a -> !a.equals(task.getTaskDefinitionKey())).collect(Collectors.toList());
                taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(task.getProcessInstanceId(), strings);
            }
        } catch (Exception e) {
            throw new AFBizException("流程提交跳转到多人节点失败!");
        }
    }

    /**
     * 同意推进节点处理: 检测当前节点是否为同意推进节点, 若是则原事务 complete + Verifyinfo, 注册 afterCommit 推进.
     * 仅在同意按钮入口(经过 BpmnSendMessageAspect, ThreadLocal 有 businessDataVo)时启用;
     * 批量审批入口(ThreadLocal 为空)走默认 complete, 不推进.
     *
     * 性能优化: 先用 task.formKey(NodeExtraInfoDTO JSON, 含标签列表)快速短路,
     * 不含 approve_forward_node 标签的普通同意审批直接 return false, 零 DB 查询;
     * 含标签时再用 getNodeIdsByeElementId(elementId→nodeId主键)精确定位 t_bpmn_node, 取配置.
     *
     * @return true 表示已处理(调用方应直接 return); false 表示非同意推进节点, 调用方继续走默认逻辑
     */
    private boolean tryApproveForward(Task task, Map<String, Object> varMap) {
        // 从 ThreadLocal 取 businessDataVo (同意按钮入口经 BpmnSendMessageAspect 已注入; 批量审批入口为空)
        Object bdObj = ThreadLocalContainer.get(StringConstants.AF_RUNTIME_BUISINESS_INFO);
        if (!(bdObj instanceof BusinessDataVo)) {
            return false;
        }
        BusinessDataVo businessDataVo = (BusinessDataVo) bdObj;
        BpmnConfVo bpmnConfVo = businessDataVo.getBpmnConfVo();
        if (bpmnConfVo == null || bpmnConfVo.getId() == null) {
            return false;
        }
        Long confId = bpmnConfVo.getId();
        String processNumber = businessDataVo.getProcessNumber();
        if (StringUtils.isEmpty(processNumber)) {
            return false;
        }

        // === 快速短路: 通过 task.formKey 检查节点标签, 不含 approve_forward_node 直接 return ===
        // formKey 是 NodeExtraInfoDTO 的 JSON, 由 BpmnTaskListener 在任务创建时写入, 含 nodeLabelVOS
        String formKey = task.getFormKey();
        if (StringUtils.isEmpty(formKey)) {
            return false;
        }
        NodeExtraInfoDTO extraInfoDTO;
        try {
            extraInfoDTO = com.alibaba.fastjson2.JSON.parseObject(formKey, NodeExtraInfoDTO.class);
        } catch (Exception e) {
            log.warn("同意推进 formKey 解析失败, 退化为默认 complete. processNumber={}", processNumber, e);
            return false;
        }
        if (!NodeUtil.nodeLabelContainsAny(extraInfoDTO, StringConstants.APPROVE_FORWARD_NODE)) {
            // 普通同意审批: 无 approve_forward_node 标签, 直接 return, 走默认 complete
            return false;
        }

        // === 精确定位当前节点: elementId(taskDefKey) → t_bpmn_node 主键 ===
        List<String> currentNodeIds = bpmVariableMapper.getNodeIdsByeElementId(processNumber, task.getTaskDefinitionKey());
        if (CollectionUtils.isEmpty(currentNodeIds)) {
            log.warn("同意推进: 未找到 elementId 对应的节点主键, 退化为默认 complete. processNumber={}, taskDefKey={}",
                    processNumber, task.getTaskDefinitionKey());
            return false;
        }
        String currentNodePrimaryKey = currentNodeIds.get(0);
        BpmnNode currentNode = bpmnNodeService.getById(Long.valueOf(currentNodePrimaryKey));
        if (currentNode == null) {
            log.warn("同意推进: 节点主键查不到记录, 退化为默认 complete. processNumber={}, nodePrimaryKey={}",
                    processNumber, currentNodePrimaryKey);
            return false;
        }

        // 取当前节点配置, 校验 forwardType=2 且有 forwardNodeIds
        BpmnNodeConfigJson currentConfig = JsonConfUtil.parseNodeConfig(currentNode.getNodeConfigJson());
        if (currentConfig == null || currentConfig.getForwardType() == null
                || currentConfig.getForwardType() != 2) {
            log.warn("同意推进: 节点配置 forwardType 非 2, 退化为默认 complete. processNumber={}, nodePrimaryKey={}",
                    processNumber, currentNodePrimaryKey);
            return false;
        }
        List<String> forwardNodeIds = currentConfig.getForwardNodeIds();
        if (CollectionUtils.isEmpty(forwardNodeIds)) {
            log.warn("同意推进节点配置异常: forwardNodeIds 为空, 退化为默认 complete. processNumber={}, taskDefKey={}",
                    processNumber, task.getTaskDefinitionKey());
            return false;
        }

        // === 目标节点: UUID(node_id) → confId+node_id 查 t_bpmn_node → 主键 → elementId ===
        String targetNodeUuid = forwardNodeIds.get(0);
        BpmnNode targetNode = bpmnNodeService.getOne(
                Wrappers.<BpmnNode>lambdaQuery()
                        .eq(BpmnNode::getConfId, confId)
                        .eq(BpmnNode::getNodeId, targetNodeUuid)
                        .eq(BpmnNode::getIsDel, 0),
                false
        );
        if (targetNode == null) {
            log.warn("同意推进目标节点不存在, 退化为默认 complete. processNumber={}, targetNodeUuid={}",
                    processNumber, targetNodeUuid);
            return false;
        }
        String targetPrimaryKey = String.valueOf(targetNode.getId());
        List<String> targetElementIds = bpmVariableMapper.getElementIdsdByNodeId(processNumber, targetPrimaryKey);
        if (CollectionUtils.isEmpty(targetElementIds)) {
            log.warn("同意推进目标节点 elementId 转换失败, 退化为默认 complete. processNumber={}, targetPrimaryKey={}",
                    processNumber, targetPrimaryKey);
            return false;
        }
        String targetElementId = targetElementIds.get(0);

        // === Step 1(原事务): complete + Verifyinfo ===
        String loginEmpId = SecurityUtils.getLogInEmpId();
        String loginEmpName = SecurityUtils.getLogInEmpName();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, loginEmpName);
        taskService.complete(task.getId(), varMap);

        String comment = businessDataVo.getApprovalComment();
        String verifyDesc = "同意(推进至目标节点)" + (StringUtils.isNotBlank(comment) ? ",意见:" + comment : "");
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .verifyUserName(loginEmpName)
                .verifyUserId(loginEmpId)
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .processCode(processNumber)
                .runInfoId(task.getProcessInstanceId())
                .verifyDesc(verifyDesc)
                .taskName(task.getName())
                .taskId(task.getId())
                .taskDefKey(task.getTaskDefinitionKey())
                .build());

        // === Step 2(afterCommit 新事务): 查新任务 + moveTo 目标节点 ===
        String procInstId = task.getProcessInstanceId();
        String currentTaskDefKey = task.getTaskDefinitionKey();
        String targetNodeName = targetNode.getNodeName();
        String taskId = task.getId();
        String taskName = task.getName();

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        new TransactionTemplate(transactionManager).execute(status -> {
                            doApproveForwardAfterCommit(procInstId, currentTaskDefKey, targetElementId,
                                    targetNodeName, processNumber, taskId, taskName);
                            return null;
                        });
                    } catch (Exception e) {
                        // afterCommit 阶段原事务已提交, 异常无法回滚原事务.
                        // 新事务已回滚(TransactionTemplate 内部处理), 流程停在 complete 后的下一节点.
                        // 记录错误日志供人工排查和介入.
                        log.error("同意推进 afterCommit 执行失败, 流程停在 complete 后的下一节点, 请人工处理. procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                                procInstId, currentTaskDefKey, targetElementId, targetNodeName, e);
                    }
                }
            });
        } else {
            // 无事务上下文(异常路径), 直接在新事务执行
            try {
                new TransactionTemplate(transactionManager).execute(status -> {
                    doApproveForwardAfterCommit(procInstId, currentTaskDefKey, targetElementId,
                            targetNodeName, processNumber, taskId, taskName);
                    return null;
                });
            } catch (Exception e) {
                log.error("同意推进执行失败(无事务上下文), procInstId={}, currentTaskDefKey={}, targetElementId={}",
                        procInstId, currentTaskDefKey, targetElementId, e);
                throw new AFBizException("同意推进失败", e);
            }
        }
        return true;
    }

    /**
     * 在新事务里执行 moveTo 目标节点.
     * 此时原事务已提交, complete 和 Verifyinfo 已落库, 查询状态一致.
     */
    private void doApproveForwardAfterCommit(String procInstId, String currentTaskDefKey, String targetElementId,
                                              String targetNodeName, String processNumber,
                                              String taskId, String taskName) {
        log.info("同意推进 doApproveForwardAfterCommit 开始, procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                procInstId, currentTaskDefKey, targetElementId, targetNodeName);

        // 查询 complete 后的活动任务
        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(procInstId).active().list();
        if (CollectionUtils.isEmpty(currentTasks)) {
            log.info("同意推进: complete 后流程已结束, 无需推进. procInstId={}", procInstId);
            return;
        }

        // 找到新当前任务(complete 后的下一节点任务)
        // 不再用 currentTaskDefKey 过滤(它已是旧任务), 而是查所有活动任务
        List<String> newTaskDefKeys = currentTasks.stream()
                .map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
        if (newTaskDefKeys.size() == 1 && newTaskDefKeys.get(0).equals(targetElementId)) {
            // 目标就是下一节点, 无需额外跳转
            log.info("同意推进: 目标即为下一节点, 无需跳转. procInstId={}", procInstId);
            return;
        }
        if (newTaskDefKeys.size() == 1) {
            // 顺序流: 使用 moveTo 直接跳转
            String newCurrentTaskDefKey = newTaskDefKeys.get(0);
            forwardToNodeImpl.moveToTarget(procInstId, newCurrentTaskDefKey, targetElementId);
            log.info("同意推进: 顺序流 moveTo 完成. procInstId={}, from={}, to={}",
                    procInstId, newCurrentTaskDefKey, targetElementId);
            return;
        }
        // 多分支(并行网关): 选第一个非目标的分支任务作起点, moveTo 到目标
        // 复用 ForwardToNodeImpl 的跨并行网关处理逻辑: 找一个非目标的任务做 moveTo 起点
        // 简化处理: 遍历找一个非目标的 taskDefKey 作起点
        String startDefKey = newTaskDefKeys.stream()
                .filter(k -> !k.equals(targetElementId))
                .findFirst()
                .orElse(null);
        if (startDefKey == null) {
            log.warn("同意推进: 多分支场景未找到合适的 moveTo 起点, 流程停在当前节点. procInstId={}, newTaskDefKeys={}",
                    procInstId, newTaskDefKeys);
            return;
        }
        forwardToNodeImpl.moveToTarget(procInstId, startDefKey, targetElementId);
        log.info("同意推进: 跨并行网关 moveTo 完成. procInstId={}, from={}, to={}",
                procInstId, startDefKey, targetElementId);
    }
}
