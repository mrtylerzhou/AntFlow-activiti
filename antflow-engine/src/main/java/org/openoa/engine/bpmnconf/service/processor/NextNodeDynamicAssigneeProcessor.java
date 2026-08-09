package org.openoa.engine.bpmnconf.service.processor;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.adp.processoperation.AddAssigneeProcessImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 到达前设置(动态审批人)处理器.
 *
 * <p>触发: 节点审批人为虚拟人 {@link AFSpecialAssigneeEnum#ARRIVAL_DYNAMIC_ASSIGNEE}("-5").
 * 流程发起时该虚拟人作为节点审批人透传到 activiti assignee; 运行到该节点时 BpmnTaskListener 拉起本处理器,
 * 调用 {@link FormOperationAdaptor#provideCurrentNodeAssignees} 动态查询真实审批人,
 * 将虚拟人任务委托(setAssignee)给查到的真人, 达到动态目的.
 *
 * <p>处理规则:
 * <ul>
 *   <li>assignee != -5: 直接返回(门控, 不影响其它节点).</li>
 *   <li>assignee == -5 且 businessDataVo == null: 抛异常.
 *       null 只发生在 timer/async/重试/迁移等非 doProcessButton 链路, 回滚的是那些非用户事务(可重试),
 *       不会让上一步审批人的"同意"失败. happy path(submit/agree/back) ThreadLocal 必有 vo.</li>
 *   <li>查到人(非空): 首个 setAssignee 委托(同步, 安全, 同 NextNodeForwardProcessor);
 *       其余人延迟到事务 afterCommit 后循环调 {@link AddAssigneeProcessImpl} 加签
 *       (参考 DefaultTaskFlowControlService). 加签走 split/MultiCharSign, 依赖任务已落库,
 *       不能在 create-listener 里同步做(Activiti 重入反模式).</li>
 *   <li>查不到人(空/null): 复用 AUTO_NODE_SKIP 跳过模式, setAssignee + complete + 写 verifyInfo.</li>
 * </ul>
 *
 * <p>order=0: 先于 NextNodeForwardProcessor(entrust, order=1) 执行,
 * 使动态查出的人仍可叠加用户委托(若该人设了出差委托).
 * 与 NextNodeLabelsProcessor(0)/FormRelatedAssigneeRefreshProcessor(0) 门控互斥, 同级安全.
 *
 * <p>委托(setAssignee)与跳过(complete)在 create-listener 同步执行有先例:
 * NextNodeForwardProcessor(setAssignee)、NextNodeLabelsProcessor(((TaskEntity)delegateTask).complete(varMap,false)).
 */
@Slf4j
@Service
public class NextNodeDynamicAssigneeProcessor implements AntFlowNextNodeBeforeWriteProcessor {

    /** bpm_flowrun_entrust.action_type: 0=委托(虚拟人委托给动态查到的真人, 与用户委托同语义) */
    private static final int ACTION_TYPE_ARRIVAL_DYNAMIC = 0;

    @Resource
    private FormFactory formFactory;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public void postProcess(BpmNextTaskDto bpmNextTaskDto) {
        DelegateTask delegateTask = bpmNextTaskDto.getDelegateTask();
        if (delegateTask == null) {
            return;
        }
        String assignee = delegateTask.getAssignee();
        // 门控: 仅虚拟动态审批人节点生效
        if (!AFSpecialAssigneeEnum.ARRIVAL_DYNAMIC_ASSIGNEE.getId().equals(assignee)) {
            return;
        }

        BusinessDataVo businessDataVo = bpmNextTaskDto.getBusinessDataVo();
        // Q1: 仅动态节点 + vo 为 null(非 doProcessButton 链路) 时抛异常
        if (businessDataVo == null) {
            throw new AFBizException("到达前设置(动态审批人)节点到达时 businessDataVo 为空, 无法动态查询审批人. "
                    + "processNumber=" + bpmNextTaskDto.getProcessNumber()
                    + ", taskDefKey=" + bpmNextTaskDto.getTaskDefKey()
                    + "(此场景多见于 timer/async/重试/迁移等非用户触发链路)");
        }

        // 获取运行时 FormOperationAdaptor(参照 SubmitProcessImpl#doProcessButton)
        FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(businessDataVo);
        if (formAdaptor == null) {
            throw new AFBizException("到达前设置: 未找到 formCode=" + businessDataVo.getFormCode() + " 对应的 FormOperationAdaptor");
        }

        // 动态查询当前节点真实审批人
        List<BaseIdTranStruVo> assignees;
        try {
            assignees = formAdaptor.provideCurrentNodeAssignees(businessDataVo);
        } catch (Exception e) {
            log.error("到达前设置: provideCurrentNodeAssignees 调用异常, processNumber={}, taskDefKey={}",
                    bpmNextTaskDto.getProcessNumber(), bpmNextTaskDto.getTaskDefKey(), e);
            throw new AFBizException("到达前设置: 动态查询审批人异常: " + e.getMessage(), e);
        }

        // 查不到人: 复用 AUTO_NODE_SKIP 跳过模式
        if (CollectionUtils.isEmpty(assignees)) {
            skipCurrentNode(delegateTask, bpmNextTaskDto);
            return;
        }

        // 查到人: 首个 setAssignee 委托(同步)
        BaseIdTranStruVo first = assignees.get(0);
        if (first == null || StringUtils.isEmpty(first.getId())) {
            log.warn("到达前设置: provideCurrentNodeAssignees 返回的首个审批人 id 为空, 跳过节点. processNumber={}",
                    bpmNextTaskDto.getProcessNumber());
            skipCurrentNode(delegateTask, bpmNextTaskDto);
            return;
        }
        String oldUserId = assignee; // -5
        String oldUserName = AFSpecialAssigneeEnum.ARRIVAL_DYNAMIC_ASSIGNEE.getDesc();
        String newUserId = first.getId();
        String newUserName = first.getName();
        delegateTask.setAssignee(newUserId);
        if (delegateTask instanceof TaskEntity) {
            ((TaskEntity) delegateTask).setAssigneeName(newUserName);
        }
        // 审计 bpm_flowrun_entrust(action_type=5)
        bpmFlowrunEntrustService.addFlowrunEntrust(
                newUserId, newUserName, oldUserId, oldUserName,
                delegateTask.getId(), 1,
                delegateTask.getProcessInstanceId(), bpmNextTaskDto.getFormCode(),
                bpmNextTaskDto.getTaskDefKey(), ACTION_TYPE_ARRIVAL_DYNAMIC);
        log.info("到达前设置: 节点[{}] 虚拟人 {} -> 真人 {}({}), processNumber={}",
                bpmNextTaskDto.getTaskDefKey(), oldUserId, newUserId, newUserName, bpmNextTaskDto.getProcessNumber());

        // 其余人: 延迟到事务 afterCommit 后加签(任务落库后 split/MultiCharSign 才安全)
        if (assignees.size() > 1) {
            List<BaseIdTranStruVo> rest = new ArrayList<>(assignees.subList(1, assignees.size()));
            registerAddSignAfterCommit(bpmNextTaskDto, rest);
        }
    }

    /**
     * 查不到人时跳过当前虚拟人节点: setAssignee(AUTO_NODE_SKIP) + complete + 写 verifyInfo.
     * 复用 NextNodeLabelsProcessor 的自动跳过模式(((TaskEntity)delegateTask).complete(varMap,false)).
     */
    private void skipCurrentNode(DelegateTask delegateTask, BpmNextTaskDto dto) {
        String skipUserId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();
        String skipUserName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        delegateTask.setAssignee(skipUserId);
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, skipUserName);
        ((TaskEntity) delegateTask).complete(varMap, false);

        BpmVerifyInfo verifyInfo = BpmVerifyInfo.builder()
                .verifyDate(new Date())
                .taskName(delegateTask.getName())
                .taskId(delegateTask.getId())
                .runInfoId(delegateTask.getProcessInstanceId())
                .verifyUserId(skipUserId)
                .verifyUserName(skipUserName)
                .taskDefKey(delegateTask.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc("到达前设置: 动态查询审批人为空, 自动跳过")
                .processCode(dto.getProcessNumber())
                .build();
        try {
            bpmVerifyInfoBizService.addVerifyInfo(verifyInfo);
        } catch (Exception e) {
            // 审计写入失败不应阻断流程流转(任务已 complete)
            log.error("到达前设置: 跳过节点写 verifyInfo 失败, processNumber={}, taskDefKey={}",
                    dto.getProcessNumber(), dto.getTaskDefKey(), e);
        }
        log.info("到达前设置: 节点[{}] 动态查询审批人为空, 自动跳过, processNumber={}",
                dto.getTaskDefKey(), dto.getProcessNumber());
    }

    /**
     * 注册事务 afterCommit 回调, 在任务落库后循环调 AddAssigneeProcessImpl 为其余人加签.
     * <p>
     * 加签(split/MultiCharSign via executeCommand)依赖 createTaskQuery 查已落库任务,
     * 在 BpmnTaskListener(create 事件, 任务尚未提交)里同步执行是 Activiti 重入反模式, 故必须延迟到 afterCommit.
     * afterCommit 在 HTTP 响应前同线程执行, 早于用户在待办看到任务, 首人不可能在加签前审批, 竞态可忽略.
     * 加签失败仅告警, 不回滚首个(首人已 setAssignee 生效, 可正常审批).
     * <p>
     * 参照 ForwardToNodeImpl#advanceToTargetNode 的 afterCommit + TransactionTemplate 模式,
     * 以及 DefaultTaskFlowControlService line243-258 的 AddAssigneeProcessImpl 循环调用模式.
     */
    private void registerAddSignAfterCommit(BpmNextTaskDto dto, List<BaseIdTranStruVo> rest) {
        // afterCommit 阶段 delegateTask 不可用, 必须提前取出所需信息
        String processNumber = dto.getProcessNumber();
        String taskDefKey = dto.getTaskDefKey();
        String formCode = dto.getFormCode();

        log.info("到达前设置: 注册 afterCommit 加签回调, processNumber={}, taskDefKey={}, 加签人数={}",
                processNumber, taskDefKey, rest.size());

        Runnable addSignTask = () -> {
            AddAssigneeProcessImpl addAssignee = SpringBeanUtils.getBean(AddAssigneeProcessImpl.class);
            for (BaseIdTranStruVo person : rest) {
                if (person == null || StringUtils.isEmpty(person.getId())) {
                    continue;
                }
                BusinessDataVo vo = new BusinessDataVo();
                vo.setFormCode(formCode);
                vo.setProcessNumber(processNumber);
                vo.setTaskDefKey(taskDefKey);
                vo.setOperationType(ProcessOperationEnum.BUTTON_TYPE_ADD_ASSIGNEE.getCode());
                List<BaseIdTranStruVo> userInfos = new ArrayList<>();
                userInfos.add(person);
                vo.setUserInfos(userInfos);
                try {
                    addAssignee.doProcessButton(vo);
                    log.info("到达前设置: afterCommit 加签成功, processNumber={}, taskDefKey={}, 加签人={}({})",
                            processNumber, taskDefKey, person.getId(), person.getName());
                } catch (Exception e) {
                    // 单人加签失败不影响其余人/首人; 首人已 setAssignee 可正常审批
                    log.error("到达前设置: afterCommit 加签失败, 跳过此人. processNumber={}, taskDefKey={}, 加签人={}({})",
                            processNumber, taskDefKey, person.getId(), person.getName(), e);
                }
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 正常路径: 注册 afterCommit 回调, 延迟到当前事务提交后用新事务执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        new TransactionTemplate(transactionManager).execute(status -> {
                            addSignTask.run();
                            return null;
                        });
                    } catch (Exception e) {
                        // afterCommit 阶段原事务已提交, 异常无法回滚原事务.
                        // 新事务已回滚, 首人已 setAssignee 生效, 流程正常; 仅记录缺失加签人供人工补.
                        log.error("到达前设置: afterCommit 加签整体失败(首人已生效), processNumber={}, taskDefKey={}",
                                processNumber, taskDefKey, e);
                    }
                }
            });
        } else {
            // 降级路径: 无事务上下文, 直接新事务执行
            try {
                new TransactionTemplate(transactionManager).execute(status -> {
                    addSignTask.run();
                    return null;
                });
            } catch (Exception e) {
                log.error("到达前设置: 加签执行失败(首人已生效), processNumber={}, taskDefKey={}",
                        processNumber, taskDefKey, e);
            }
        }
    }

    @Override
    public int order() {
        // 先于 NextNodeForwardProcessor(entrust, order=1) 执行, 使委托基于动态查出的人叠加
        return 0;
    }
}
