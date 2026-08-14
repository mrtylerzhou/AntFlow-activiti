package org.openoa.engine.bpmnconf.service.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.BpmProcessForward;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeAutoNodeConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.FilterUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.engine.bpmnconf.adp.processoperation.ForwardToNodeImpl;
import org.openoa.engine.bpmnconf.adp.processoperation.BackToModifyImpl;
import org.openoa.engine.bpmnconf.adp.processoperation.EndProcessImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.service.biz.AutoSignUpAssigneeResolver;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpPersonnelBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NextNodeLabelsProcessor implements AntFlowNextNodeBeforeWriteProcessor {
    @Resource
    private BpmProcessForwardServiceImpl bpmProcessForwardService;
    @Resource
    private BpmVariableMapper bpmVariableMapper;
    @Resource
    private FormFactory formFactory;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Autowired
    private ForwardToNodeImpl forwardToNodeImpl;
    @Autowired
    private BackToModifyImpl backToModifyImpl;
    @Autowired
    private EndProcessImpl endProcessImpl;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmVariableSignUpPersonnelBizService bpmVariableSignUpPersonnelBizService;
    @Autowired
    private AutoSignUpAssigneeResolver autoSignUpAssigneeResolver;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Override
    public void postProcess(BpmNextTaskDto bpmnNextTaskDto) {
        List<BpmnNodeLabelVO> nodeLabelVOS = bpmnNextTaskDto.getNodeLabels();
        if(CollectionUtils.isEmpty(nodeLabelVOS)){
            return;
        }
        String procInstId= bpmnNextTaskDto.getProcessInstanceId();
        String elementId= bpmnNextTaskDto.getTaskDefKey();
        String processNumber= bpmnNextTaskDto.getProcessNumber();
        String assignee= bpmnNextTaskDto.getAssignee();
        String assigneeName= bpmnNextTaskDto.getTaskName();
        BusinessDataVo businessDataVo = bpmnNextTaskDto.getBusinessDataVo();
        String formCode= bpmnNextTaskDto.getFormCode();
        Boolean isOutSide = bpmnNextTaskDto.getIsOutSide();
        DelegateTask delegateTask = bpmnNextTaskDto.getDelegateTask();
        nodeLabelVOS=nodeLabelVOS.stream().filter(FilterUtil.distinctByKeys(BpmnNodeLabelVO::getLabelValue)).collect(Collectors.toList());
        for (BpmnNodeLabelVO nodeLabelVO : nodeLabelVOS) {
            processCopy(elementId, processNumber, procInstId,nodeLabelVO);
            processCopyV2(nodeLabelVO, procInstId, assignee, assigneeName, processNumber,delegateTask);
            processAutomaticNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processAutoAdvanceNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processAutoReturnNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processConditionReturnNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processAutoSkipNode(nodeLabelVO, assignee, procInstId, assigneeName, processNumber, delegateTask);
            processConditionApproveNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, delegateTask);
            processConditionAdvanceNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processConditionDisagreeNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, delegateTask);
            processConditionAutoSignUpNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, delegateTask);
            processConditionAutoTransferNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, delegateTask);
            processConditionCopyNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processPrevNodeAppointed(nodeLabelVO, businessDataVo, delegateTask, formCode, processNumber);
        }
    }

    /**
     * 上一节点指定审批人处理:
     * 当前节点贴有 af_syslabel_prev_node_appointed 标签时,将虚拟审批人 PREV_NODE_APPOINTED("-4")
     * 替换为上一节点审批人提交的 nextNodeApprovers 中的实际审批人。
     *
     * 简化规则: nextNodeApprovers 仅允许 1 人。
     * - 校验 nextNodeApprovers 非空且 size==1, 否则抛 AFBizException
     * - 替换 delegateTask.assignee: "-4" → user1
     * - 写 BpmFlowrunEntrust 委托记录 (original="-4", actual=user1) — 必然委托
     * - 清空 businessDataVo.nextNodeApprovers (供后续节点复用)
     *
     * 处理器顺序: 本方法在 NextNodeLabelsProcessor(order=0) 内执行,
     * 先于 NextNodeForwardProcessor(order=1); 后者会再对 user1 做委托检查。
     */
    private void processPrevNodeAppointed(BpmnNodeLabelVO nodeLabelVO, BusinessDataVo businessDataVo,
                                          DelegateTask delegateTask, String formCode, String processNumber) {
        if (!StringConstants.AF_SYSLABEL_PREV_NODE_APPOINTED.equals(nodeLabelVO.getLabelValue())) {
            return;
        }
        List<BaseIdTranStruVo> nextNodeApprovers = businessDataVo == null ? null : businessDataVo.getNextNodeApprovers();
        if (CollectionUtils.isEmpty(nextNodeApprovers)) {
            throw new AFBizException("上一节点指定审批人未指定,请在上一节点审批时通过[指定下一节点审批人]按钮选择审批人");
        }
        if (nextNodeApprovers.size() != 1) {
            throw new AFBizException("上一节点指定审批人仅允许指定1人,当前指定了" + nextNodeApprovers.size() + "人");
        }
        BaseIdTranStruVo user1 = nextNodeApprovers.get(0);
        if (user1 == null || org.apache.commons.lang3.StringUtils.isEmpty(user1.getId())) {
            throw new AFBizException("上一节点指定审批人信息不完整");
        }
        //替换虚拟审批人为实际审批人
        String oldUserId = delegateTask.getAssignee();
        String oldUserName = AFSpecialAssigneeEnum.PREV_NODE_APPOINTED.getDesc();
        delegateTask.setAssignee(user1.getId());
        if (delegateTask instanceof TaskEntity) {
            ((TaskEntity) delegateTask).setAssigneeName(user1.getName());
        }
        //必然委托:写 BpmFlowrunEntrust 记录 (original=虚拟用户, actual=实际用户)
        BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
        entrust.setType(1);
        entrust.setRuntaskid(delegateTask.getId());
        entrust.setActual(user1.getId());
        entrust.setActualName(user1.getName());
        entrust.setOriginal(oldUserId);
        entrust.setOriginalName(oldUserName);
        entrust.setIsRead(2);
        entrust.setProcDefId(formCode);
        entrust.setRuninfoid(delegateTask.getProcessInstanceId());
        bpmFlowrunEntrustService.addFlowrunEntrust(entrust);
        log.info("上一节点指定审批人替换: processNumber={}, original={}, actual={}", processNumber, oldUserId, user1.getId());
        //清空 nextNodeApprovers,供后续节点复用
        businessDataVo.setNextNodeApprovers(null);
    }

    /**
     * 自动节点处理(增强版):
     * - 条件评估 (formAdaptor.automaticCondition) + automaticAction 自定义钩子
     * - 异常兜底: conditionResult=null → 默认 complete (保持现状)
     * - true  → 按 autoNodeConf.satisfiedAction 分发: 0默认complete / 1跳转固定节点 / 2加批 / 3转办 / 4抄送
     * - false → 按 autoNodeConf.unsatisfiedAction 分发: 0默认complete / 1结束流程 / 2退回指定节点(重新开始)
     * - 旧数据新字段为空 → 两分支均默认 complete, 零迁移
     */
    private void processAutomaticNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, String procInstId, DelegateTask delegateTask) {

        if (!StringConstants.AUTOMATIC_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }
        log.info("自动节点处理开始, processNumber={}, elementId={}", processNumber, elementId);

        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
            log.info("自动节点条件评估结果, processNumber={}, elementId={}, conditionResult={}", processNumber, elementId, conditionResult);
            formAdaptor.automaticAction(businessDataVo, conditionResult);
        } catch (Exception e) {
            log.error("自动节点条件判断或动作执行异常, 回退默认complete, processNumber={}, elementId={}", processNumber, elementId, e);
            conditionResult = null;
        }

        BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
        BpmnNodeAutoNodeConfJson autoConf = configJson == null ? null : configJson.getAutoNodeConf();

        if (Boolean.TRUE.equals(conditionResult)) {
            executeAutoNodeSatisfiedAction(autoConf, businessDataVo, processNumber, elementId, formCode, procInstId, delegateTask);
        } else if (Boolean.FALSE.equals(conditionResult)) {
            executeAutoNodeUnsatisfiedAction(autoConf, businessDataVo, processNumber, elementId, procInstId, delegateTask);
        } else {
            // 异常兜底: 默认 complete (保持现状)
            completeAsAutoSkip(delegateTask, processNumber, String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT, conditionResult));
        }
    }

    /**
     * 自动节点满足分支动作分发: 0/null默认complete / 1跳转固定节点 / 2加批 / 3转办 / 4抄送
     */
    private void executeAutoNodeSatisfiedAction(BpmnNodeAutoNodeConfJson autoConf, BusinessDataVo businessDataVo,
                                                String processNumber, String elementId, String formCode,
                                                String procInstId, DelegateTask delegateTask) {
        Integer action = autoConf == null ? null : autoConf.getSatisfiedAction();
        String assigneeId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();
        String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        String skipComment = String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT, true);
        if (action == null || action == 0) {
            completeAsAutoSkip(delegateTask, processNumber, skipComment);
            return;
        }
        if (action == 1) {
            // 跳转至固定节点 (同自动推进)
            List<String> forwardNodeIds = autoConf.getForwardNodeIds();
            if (CollectionUtils.isEmpty(forwardNodeIds)) {
                throw new AFBizException("自动节点配置异常: 未配置跳转目标节点, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            BpmnNode targetNode = resolveTargetNode(businessDataVo, processNumber, forwardNodeIds.get(0), "自动节点跳转");
            String targetElementId = resolveElementIdByNode(processNumber, targetNode, "自动节点跳转");
            log.info("自动节点: 条件满足, 跳转固定节点, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                    processNumber, elementId, targetElementId, targetNode.getNodeName());
            forwardToNodeImpl.advanceToTargetNode(delegateTask, procInstId,
                    delegateTask.getTaskDefinitionKey(), targetElementId, targetNode.getNodeName(),
                    assigneeId, assigneeName);
            return;
        }
        if (action == 2) {
            // 加批 (同条件自动加批, 发布期已强制 afterSignUpWay=2 不回到审批人)
            if (bpmVariableSignUpPersonnelBizService.hasSignUpPersonnel(processNumber, elementId)) {
                log.info("自动节点已加批过, 跳过重复触发, processNumber={}, elementId={}", processNumber, elementId);
                completeAsAutoSkip(delegateTask, processNumber, skipComment);
                return;
            }
            List<BaseIdTranStruVo> signUpUsers = resolveAssigneeRule(autoConf.getAutoSignUpConf(), businessDataVo, processNumber, elementId, "自动节点加批");
            if (CollectionUtils.isEmpty(signUpUsers)) {
                log.error("自动节点加批规则解析结果为空, 回退默认complete, processNumber={}, elementId={}", processNumber, elementId);
                completeAsAutoSkip(delegateTask, processNumber, skipComment);
                return;
            }
            bpmVariableSignUpPersonnelBizService.insertSignUpPersonnel(taskService, delegateTask.getId(), processNumber, elementId,
                    delegateTask.getAssignee(), assigneeName, signUpUsers);
            Map<String, Object> varMap = new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo.builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(assigneeId)
                    .verifyUserName(assigneeName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_SIGN_UP.getCode())
                    .verifyDesc(String.format(StringConstants.AF_CONDITION_AUTO_SIGNUP_COMMENT, true))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
            ((TaskEntity) delegateTask).complete(varMap, false);
            return;
        }
        if (action == 3) {
            // 转办 (同条件自动转办类型1: 转给指定人), 不 complete, 任务转人工
            BaseIdTranStruVo target = autoConf.getTransferToUser() == null ? null
                    : com.alibaba.fastjson2.JSON.parseObject(com.alibaba.fastjson2.JSON.toJSONString(autoConf.getTransferToUser()), BaseIdTranStruVo.class);
            if (target == null || org.apache.commons.lang3.StringUtils.isEmpty(target.getId())) {
                log.error("自动节点未配置转办目标, 回退默认complete, processNumber={}, elementId={}", processNumber, elementId);
                completeAsAutoSkip(delegateTask, processNumber, skipComment);
                return;
            }
            String oldUserId = delegateTask.getAssignee();
            if (!target.getId().equals(oldUserId)) {
                delegateTask.setAssignee(target.getId());
                if (delegateTask instanceof TaskEntity) {
                    ((TaskEntity) delegateTask).setAssigneeName(target.getName());
                }
                BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
                entrust.setType(1);
                entrust.setRuntaskid(delegateTask.getId());
                entrust.setActual(target.getId());
                entrust.setActualName(target.getName());
                entrust.setOriginal(oldUserId);
                entrust.setOriginalName(assigneeName);
                entrust.setIsRead(2);
                entrust.setProcDefId(formCode);
                entrust.setRuninfoid(delegateTask.getProcessInstanceId());
                bpmFlowrunEntrustService.addFlowrunEntrust(entrust);
                log.info("自动节点: 条件满足, 转办生效, 转办前: {}, 转办后: {}, processNumber={}, elementId={}", oldUserId, target.getId(), processNumber, elementId);
            }
            // 不 complete, 不写审批记录 (转办人处理时自然产生)
            return;
        }
        if (action == 4) {
            // 抄送: 解析抄送人逐人写 BpmProcessForward + complete
            List<BaseIdTranStruVo> copyUsers = resolveAssigneeRule(autoConf.getAutoCopyConf(), businessDataVo, processNumber, elementId, "自动节点抄送");
            if (CollectionUtils.isEmpty(copyUsers)) {
                log.error("自动节点抄送规则解析结果为空, 回退默认complete, processNumber={}, elementId={}", processNumber, elementId);
                completeAsAutoSkip(delegateTask, processNumber, skipComment);
                return;
            }
            for (BaseIdTranStruVo copyUser : copyUsers) {
                List<BpmProcessForward> exists = bpmProcessForwardService.list(AFWrappers.<BpmProcessForward>lambdaTenantQuery()
                        .eq(BpmProcessForward::getProcessInstanceId, procInstId)
                        .eq(BpmProcessForward::getForwardUserId, copyUser.getId()));
                if (CollectionUtils.isEmpty(exists)) {
                    bpmProcessForwardService.addProcessForward(BpmProcessForward.builder()
                            .createTime(new Date())
                            .createUserId(assigneeId)
                            .forwardUserId(copyUser.getId())
                            .ForwardUserName(copyUser.getName())
                            .processInstanceId(procInstId)
                            .processNumber(processNumber)
                            .build());
                }
            }
            String copyNames = copyUsers.stream().map(BaseIdTranStruVo::getName).collect(Collectors.joining(","));
            completeAsAutoSkip(delegateTask, processNumber, "(抄送给" + copyNames + ")自动通过");
        }
    }

    /**
     * 自动节点不满足分支动作分发: 0/null默认complete / 1结束流程 / 2退回指定节点(重新开始)
     */
    private void executeAutoNodeUnsatisfiedAction(BpmnNodeAutoNodeConfJson autoConf, BusinessDataVo businessDataVo,
                                                  String processNumber, String elementId, String procInstId, DelegateTask delegateTask) {
        Integer action = autoConf == null ? null : autoConf.getUnsatisfiedAction();
        String assigneeId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();
        String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        if (action == null || action == 0) {
            completeAsAutoSkip(delegateTask, processNumber, String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT, false));
            return;
        }
        if (action == 1) {
            // 结束流程 (同条件拒绝: 拒绝记录 + endProcessWithoutVerify)
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo.builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(assigneeId)
                    .verifyUserName(assigneeName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessStateEnum.REJECT_STATE.getCode())
                    .verifyDesc("自动节点条件不满足, 结束流程")
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
            businessDataVo.setFlag(false);
            endProcessImpl.endProcessWithoutVerify(businessDataVo);
            return;
        }
        if (action == 2) {
            // 退回指定节点 (重新开始, backType=4)
            if (org.apache.commons.lang3.StringUtils.isEmpty(autoConf.getBackToNodeId())) {
                throw new AFBizException("自动节点配置异常: 未配置退回目标节点, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            BpmnNode targetNode = resolveTargetNode(businessDataVo, processNumber, autoConf.getBackToNodeId(), "自动节点退回");
            String targetElementId = resolveElementIdByNode(processNumber, targetNode, "自动节点退回");
            log.info("自动节点: 条件不满足, 开始退回, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                    processNumber, elementId, targetElementId, targetNode.getNodeName());
            backToModifyImpl.returnToTargetNode(delegateTask, procInstId, processNumber,
                    delegateTask.getTaskDefinitionKey(), targetElementId, targetNode.getNodeName(),
                    assigneeId, "自动节点条件不满足自动退回", businessDataVo, 4);
        }
    }

    /**
     * 公共前置: 填充 businessDataVo + 获取 formAdaptor + 低码流程补充 lfConditions
     */
    private FormOperationAdaptor prepareFormAdaptor(String processNumber, String elementId, String formCode,
                                                    BusinessDataVo businessDataVo, Boolean isOutSide) {
        businessDataVo.setProcessNumber(processNumber);
        businessDataVo.setTaskDefKey(elementId);
        businessDataVo.setFormCode(formCode);
        businessDataVo.setIsOutSideAccessProc(isOutSide);
        FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(businessDataVo);
        if (formAdaptor == null) {
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR, "未能根据流程formcode找到流程适配器信息!");
        }
        if (CollectionUtils.isEmpty(businessDataVo.getLfConditions()) && Objects.equals(businessDataVo.getIsLowCodeFlow(), 1)) {
            businessDataVo.setLfConditions(businessDataVo.getLfFields());
        }
        return formAdaptor;
    }

    /**
     * 公共: 设计态 UUID → BpmnNode (confId 限定当前流程)
     */
    private BpmnNode resolveTargetNode(BusinessDataVo businessDataVo, String processNumber, String targetNodeUuid, String logPrefix) {
        BpmnConfVo bpmnConfVo = businessDataVo.getBpmnConfVo();
        if (bpmnConfVo == null || bpmnConfVo.getId() == null) {
            throw new AFBizException(logPrefix + ": businessDataVo.bpmnConfVo 未填充, 无法定位流程配置, processNumber=" + processNumber);
        }
        Long confId = bpmnConfVo.getId();
        BpmnNode targetNode = bpmnNodeService.getOne(
                Wrappers.<BpmnNode>lambdaQuery()
                        .eq(BpmnNode::getConfId, confId)
                        .eq(BpmnNode::getNodeId, targetNodeUuid)
                        .eq(BpmnNode::getIsDel, 0),
                false
        );
        if (targetNode == null) {
            throw new AFBizException(logPrefix + "目标节点不存在, processNumber=" + processNumber + ", confId=" + confId + ", nodeUuid=" + targetNodeUuid);
        }
        return targetNode;
    }

    /**
     * 公共: BpmnNode → 运行期 elementId(taskDefKey)
     */
    private String resolveElementIdByNode(String processNumber, BpmnNode targetNode, String logPrefix) {
        List<String> targetElementIds = bpmVariableMapper.getElementIdsdByNodeId(processNumber, String.valueOf(targetNode.getId()));
        if (CollectionUtils.isEmpty(targetElementIds)) {
            throw new AFBizException(String.format("%s: 未能根据nodeId获取目标节点taskDefKey, processNumber=%s, targetNodeId=%s", logPrefix, processNumber, targetNode.getId()));
        }
        return targetElementIds.get(0);
    }

    /**
     * 公共: 自动节点式 complete + 虚拟人-3 审批记录
     */
    private void completeAsAutoSkip(DelegateTask delegateTask, String processNumber, String comment) {
        String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
        ((TaskEntity) delegateTask).complete(varMap, false);
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo.builder()
                .verifyDate(new Date())
                .taskName(delegateTask.getName())
                .taskId(delegateTask.getId())
                .runInfoId(delegateTask.getProcessInstanceId())
                .verifyUserId(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId())
                .verifyUserName(assigneeName)
                .taskDefKey(delegateTask.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(comment)
                .processCode(processNumber)
                .build();
        bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
    }

    /**
     * 公共: 加批/抄送规则配置解析为具体用户 (基准人为发起人, 同条件自动加批)
     */
    private List<BaseIdTranStruVo> resolveAssigneeRule(Object conf, BusinessDataVo businessDataVo, String processNumber, String elementId, String logPrefix) {
        if (conf == null) {
            return null;
        }
        String startUserId = null;
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (bpmBusinessProcess != null) {
            startUserId = bpmBusinessProcess.getCreateUser();
        }
        return autoSignUpAssigneeResolver.resolve(conf, startUserId, businessDataVo);
    }

    /**
     * 自动推进节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 与 auto node 的关键差异:
     *   conditionResult==true  → 推进到指定目标节点 (调 ForwardToNodeImpl.advanceToTargetNode)
     *   conditionResult==false/null → 和 auto node 一样 complete (不跳跃)
     * - 条件评估异常视为不满足 (问题7子点2), 走 complete 路径
     * - 推进失败抛异常, 由外层事务回滚 (问题8方案A)
     * - forwardNodeIds 存的是前端 UUID (t_bpmn_node.node_id), 需两步转换:
     *   UUID → t_bpmn_node.id (主键) → getElementIdsdByNodeId → elementId
     */
    private void processAutoAdvanceNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId,
                                        String formCode, BusinessDataVo businessDataVo, Boolean isOutSide,
                                        String procInstId, DelegateTask delegateTask) {
        if (!StringConstants.AUTO_ADVANCE_NODE.equals(nodeLabelVO.getLabelValue())) {
            return;
        }
        log.info("自动推进节点处理开始, processNumber={}, elementId={}", processNumber, elementId);

        // === 条件评估 (复用 auto node 逻辑) ===
        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
            log.info("自动推进条件评估结果, processNumber={}, elementId={}, conditionResult={}", processNumber, elementId, conditionResult);
            // 保留 automaticAction 调用作为额外副作用钩子, 推进本身不在此钩子里做 (问题7子点1)
            formAdaptor.automaticAction(businessDataVo, conditionResult);
        } catch (Exception e) {
            log.error("自动推进条件评估或动作执行异常, 视为条件不满足, processNumber={}, elementId={}", processNumber, elementId, e);
            conditionResult = false;  // 异常视为不满足 (问题7子点2)
        }

        String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        String assigneeId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();

        if (Boolean.TRUE.equals(conditionResult)) {
            // === 推进路径: 推进到指定目标节点 ===
            BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
            if (configJson == null) {
                throw new AFBizException("自动推进节点配置读取失败, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            Integer forwardType = configJson.getForwardType();
            List<String> forwardNodeIds = configJson.getForwardNodeIds();
            if (forwardType == null || forwardType != 2 || CollectionUtils.isEmpty(forwardNodeIds)) {
                throw new AFBizException("自动推进节点配置异常: 未配置固定目标节点, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            String targetNodeUuid = forwardNodeIds.get(0);
            BpmnNode targetNode = resolveTargetNode(businessDataVo, processNumber, targetNodeUuid, "自动推进");
            String targetNodeName = targetNode.getNodeName();
            String targetElementId = resolveElementIdByNode(processNumber, targetNode, "自动推进");

            log.info("自动推进: 条件满足, 开始推进, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                    processNumber, elementId, targetElementId, targetNodeName);
            try {
                forwardToNodeImpl.advanceToTargetNode(delegateTask, procInstId,
                        delegateTask.getTaskDefinitionKey(), targetElementId, targetNodeName,
                        assigneeId, assigneeName);
            } catch (Exception e) {
                log.error("自动推进失败, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                        processNumber, elementId, targetElementId, targetNodeName, e);
                throw new AFBizException(String.format("自动推进失败, processNumber=%s, elementId=%s, targetNodeId=%s, targetNodeName=%s",
                        processNumber, elementId, targetElementId, targetNodeName), e);
            }
        } else {
            // === 跳过路径: 和自动节点一样 complete (不跳跃) ===
            log.info("自动推进: 条件不满足, 执行自动跳过, processNumber={}, elementId={}", processNumber, elementId);
            completeAsAutoSkip(delegateTask, processNumber, String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT, conditionResult));
        }
    }

    /**
     * 自动退回节点处理:
     * 与 processAutoAdvanceNode 对称, 但方向相反(向后退回).
     * 满足条件 → 退回到 drawBackNodeIds 指定的目标节点(FOUR_DISAGREE)
     * 不满足条件 → 和自动节点一样 complete
     * UUID → 主键 → elementId 转换链路与自动推进一致
     */
    private void processAutoReturnNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId,
                                       String formCode, BusinessDataVo businessDataVo, Boolean isOutSide,
                                       String procInstId, DelegateTask delegateTask) {
        if (!StringConstants.AUTO_RETURN_NODE.equals(nodeLabelVO.getLabelValue())) {
            return;
        }
        log.info("自动退回节点处理开始, processNumber={}, elementId={}", processNumber, elementId);

        // === 条件评估 (复用 auto node 逻辑) ===
        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
            log.info("自动退回条件评估结果, processNumber={}, elementId={}, conditionResult={}", processNumber, elementId, conditionResult);
            formAdaptor.automaticAction(businessDataVo, conditionResult);
        } catch (Exception e) {
            log.error("自动退回条件评估或动作执行异常, 视为条件不满足, processNumber={}, elementId={}", processNumber, elementId, e);
            conditionResult = false;
        }

        String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        String assigneeId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();

        if (Boolean.TRUE.equals(conditionResult)) {
            // === 退回路径: 退回到指定目标节点 ===
            BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
            if (configJson == null) {
                throw new AFBizException("自动退回节点配置读取失败, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            Integer drawBackType = configJson.getDrawBackType();
            List<String> drawBackNodeIds = configJson.getDrawBackNodeIds();
            if (drawBackType == null || (drawBackType != 4 && drawBackType != 2) || CollectionUtils.isEmpty(drawBackNodeIds)) {
                throw new AFBizException("自动退回节点配置异常: 未配置退回目标节点, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            String targetNodeUuid = drawBackNodeIds.get(0);
            BpmnNode targetNode = resolveTargetNode(businessDataVo, processNumber, targetNodeUuid, "自动退回");
            String targetNodeName = targetNode.getNodeName();
            String targetElementId = resolveElementIdByNode(processNumber, targetNode, "自动退回");

            log.info("自动退回: 条件满足, 开始退回, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                    processNumber, elementId, targetElementId, targetNodeName);
            try {
                backToModifyImpl.returnToTargetNode(delegateTask, procInstId, processNumber,
                        delegateTask.getTaskDefinitionKey(), targetElementId, targetNodeName,
                        assigneeId, "自动退回节点自动退回", businessDataVo);
            } catch (Exception e) {
                log.error("自动退回失败, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                        processNumber, elementId, targetElementId, targetNodeName, e);
                throw new AFBizException(String.format("自动退回失败, processNumber=%s, elementId=%s, targetNodeId=%s, targetNodeName=%s",
                        processNumber, elementId, targetElementId, targetNodeName), e);
            }
        } else {
            // === 跳过路径: 和自动节点一样 complete (不跳跃) ===
            log.info("自动退回: 条件不满足, 执行自动跳过, processNumber={}, elementId={}", processNumber, elementId);
            completeAsAutoSkip(delegateTask, processNumber, String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT, conditionResult));
        }
    }

    /**
     * 条件退回节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 与自动退回的关键差异: 保留真实审批人, 条件不满足时不 complete, 留给审批人人工处理
     * - 退回目标从不同意按钮配置(backType + backToNodeId)读取
     * - 不调用 formAdaptor.automaticAction (那是 auto node 专属副作用)
     */
    private void processConditionReturnNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId,
                                            String formCode, BusinessDataVo businessDataVo, Boolean isOutSide,
                                            String procInstId, DelegateTask delegateTask) {
        boolean isConditionReturn = StringConstants.CONDITION_RETURN_NODE.equals(nodeLabelVO.getLabelValue());
        boolean isConditionReturnStarter = StringConstants.CONDITION_RETURN_STARTER_NODE.equals(nodeLabelVO.getLabelValue());
        if (!isConditionReturn && !isConditionReturnStarter) {
            return;
        }
        String logPrefix = isConditionReturnStarter ? "条件退回发起人" : "条件退回";
        log.info("{}节点处理开始, processNumber={}, elementId={}", logPrefix, processNumber, elementId);

        // === 条件评估 ===
        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
            log.info("{}条件评估结果, processNumber={}, elementId={}, conditionResult={}", logPrefix, processNumber, elementId, conditionResult);
        } catch (Exception e) {
            log.error("{}条件评估异常, 视为条件不满足, processNumber={}, elementId={}", logPrefix, processNumber, elementId, e);
            conditionResult = false;
        }

        if (Boolean.TRUE.equals(conditionResult)) {
            // === 退回路径: 按标签分支读取目标节点 ===
            BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
            if (configJson == null) {
                throw new AFBizException(logPrefix + "节点配置读取失败, processNumber=" + processNumber + ", elementId=" + elementId);
            }

            Integer backType;
            String targetNodeUuid;
            if (isConditionReturnStarter) {
                // 条件退回发起人: 从 DrawBackType + DrawBackNodeIds[0] 读取
                backType = configJson.getDrawBackType();
                java.util.List<String> drawBackNodeIds = configJson.getDrawBackNodeIds();
                targetNodeUuid = (drawBackNodeIds != null && !drawBackNodeIds.isEmpty()) ? drawBackNodeIds.get(0) : null;
            } else {
                // 条件退回: 从 BackType + BackToNodeId 读取
                backType = configJson.getBackType();
                targetNodeUuid = configJson.getBackToNodeId();
            }
            if (backType == null || (backType != 4 && backType != 5) || org.apache.commons.lang3.StringUtils.isEmpty(targetNodeUuid)) {
                throw new AFBizException(logPrefix + "节点配置异常: 未配置退回目标节点, processNumber=" + processNumber + ", elementId=" + elementId);
            }

            BpmnNode targetNode = resolveTargetNode(businessDataVo, processNumber, targetNodeUuid, logPrefix);
            String targetNodeName = targetNode.getNodeName();
            String targetElementId = resolveElementIdByNode(processNumber, targetNode, logPrefix);

            log.info("{}: 条件满足, 开始退回, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}, backType={}",
                    logPrefix, processNumber, elementId, targetElementId, targetNodeName, backType);
            String assigneeId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();
            try {
                backToModifyImpl.returnToTargetNode(delegateTask, procInstId, processNumber,
                        delegateTask.getTaskDefinitionKey(), targetElementId, targetNodeName,
                        assigneeId, logPrefix + "节点自动退回", businessDataVo, backType);
            } catch (Exception e) {
                log.error("{}失败, processNumber={}, elementId={}, targetElementId={}",
                        logPrefix, processNumber, elementId, targetElementId, e);
                throw new AFBizException(String.format("%s失败, processNumber=%s, elementId=%s, targetNodeId=%s",
                        logPrefix, processNumber, elementId, targetElementId), e);
            }
        }
        // conditionResult == false 或 null: 不操作, 留给真实审批人人工处理
    }

    /**
     * 条件审批节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 与 auto node 的关键差异: 仅当 conditionResult==true 时才 complete 任务;
     *   conditionResult==false 或 null 时, 不 complete, 留给真实审批人人工处理
     * - 不调用 formAdaptor.automaticAction (那是 auto node 专属副作用)
     */
    /**
     * 条件推进节点处理: 条件审批(nodeType=12)子类型, 自动勾选推进按钮(42,别名"同意"), 强制 forwardType=2(固定目标).
     * - 满足条件: 自动推进到固定目标节点(用虚拟人-3标识系统自动推进), 复用自动推进的推进逻辑(advanceToTargetNode)
     * - 不满足: 不 complete, 留给真实审批人人工处理(审批人点"同意"=推进按钮, 推进到配置的固定目标)
     */
    private void processConditionAdvanceNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId,
                                             String formCode, BusinessDataVo businessDataVo, Boolean isOutSide,
                                             String procInstId, DelegateTask delegateTask) {

        //条件完成节点复用此处理器(与条件推进运行时逻辑完全一致, 仅设计时目标来源不同)
        if (!StringConstants.CONDITION_ADVANCE_NODE.equals(nodeLabelVO.getLabelValue())
                && !StringConstants.CONDITION_FINISH_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }
        log.info("条件推进/条件完成节点处理开始, processNumber={}, elementId={}", processNumber, elementId);

        // === 条件评估 (复用条件审批/自动推进逻辑) ===
        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
            log.info("条件推进条件评估结果, processNumber={}, elementId={}, conditionResult={}", processNumber, elementId, conditionResult);
        } catch (Exception e) {
            log.error("条件推进节点条件判断异常, 视为条件不满足, processNumber={}, elementId={}", processNumber, elementId, e);
            conditionResult = false;
        }

        // 满足条件: 自动推进到固定目标节点 (用虚拟人-3标识系统自动推进)
        if (Boolean.TRUE.equals(conditionResult)) {
            String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
            String assigneeId = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId();

            BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
            if (configJson == null) {
                throw new AFBizException("条件推进节点配置读取失败, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            Integer forwardType = configJson.getForwardType();
            List<String> forwardNodeIds = configJson.getForwardNodeIds();
            if (forwardType == null || forwardType != 2 || CollectionUtils.isEmpty(forwardNodeIds)) {
                throw new AFBizException("条件推进节点配置异常: 未配置固定目标节点, processNumber=" + processNumber + ", elementId=" + elementId);
            }
            String targetNodeUuid = forwardNodeIds.get(0);
            BpmnNode targetNode = resolveTargetNode(businessDataVo, processNumber, targetNodeUuid, "条件推进");
            String targetNodeName = targetNode.getNodeName();
            String targetElementId = resolveElementIdByNode(processNumber, targetNode, "条件推进");

            log.info("条件推进: 条件满足, 开始推进, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                    processNumber, elementId, targetElementId, targetNodeName);
            try {
                forwardToNodeImpl.advanceToTargetNode(delegateTask, procInstId,
                        delegateTask.getTaskDefinitionKey(), targetElementId, targetNodeName,
                        assigneeId, assigneeName);
            } catch (Exception e) {
                log.error("条件推进失败, processNumber={}, elementId={}, targetElementId={}, targetNodeName={}",
                        processNumber, elementId, targetElementId, targetNodeName, e);
                throw new AFBizException(String.format("条件推进失败, processNumber=%s, elementId=%s, targetNodeId=%s, targetNodeName=%s",
                        processNumber, elementId, targetElementId, targetNodeName), e);
            }
        }
        // conditionResult == false 或 null: 不 complete, 留给真实审批人人工处理(点"同意"=推进按钮, 推进到配置的固定目标)
    }

    private void processConditionApproveNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, DelegateTask delegateTask) {

        if (!StringConstants.CONDITION_APPROVE_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
        } catch (Exception e) {
            log.error("条件审批节点条件判断异常, processNumber={}, elementId={}", processNumber, elementId, e);
        }

        //仅当条件满足时才自动 complete; 否则留给真实审批人
        if (Boolean.TRUE.equals(conditionResult)) {
            completeAsAutoSkip(delegateTask, processNumber, String.format(StringConstants.AF_CONDITION_APPROVE_AUTO_PASS_COMMENT, conditionResult));
        }
        //conditionResult == false 或 null: 不 complete, 留给真实审批人
    }

    /**
     * 条件拒绝节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 与条件审批的关键差异: 满足条件时自动拒绝(固定终止流程, 忽略不同意退回配置),
     *   写虚拟人-3 拒绝记录(verifyStatus=6) + endProcessWithoutVerify(状态=6 + 删实例 + cancellationData 回调)
     * - 不复用 EndProcessImpl.doProcessButton: 其依赖 SecurityUtils 登录用户查任务/写记录, 自动场景不可用
     * - conditionResult==false 或 null 时不 complete, 留给真实审批人人工处理
     */
    private void processConditionDisagreeNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, DelegateTask delegateTask) {

        if (!StringConstants.CONDITION_DISAGREE_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
        } catch (Exception e) {
            log.error("条件拒绝节点条件判断异常, processNumber={}, elementId={}", processNumber, elementId, e);
        }

        //仅当条件满足时才自动拒绝(固定终止流程); 否则留给真实审批人
        if (Boolean.TRUE.equals(conditionResult)) {
            String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId())
                    .verifyUserName(assigneeName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessStateEnum.REJECT_STATE.getCode())
                    .verifyDesc(String.format(StringConstants.AF_CONDITION_DISAGREE_AUTO_REJECT_COMMENT, conditionResult))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
            //固定终止流程: 更新状态=6 + 删除流程实例 + cancellationData 业务回调 (不走不同意退回分叉)
            businessDataVo.setFlag(false);
            endProcessImpl.endProcessWithoutVerify(businessDataVo);
        }
        //conditionResult == false 或 null: 不 complete, 留给真实审批人
    }

    /**
     * 条件自动加批节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 满足条件: 幂等检查(已加批则跳过) → 读 configJson.autoSignUpUsers → insertSignUpPersonnel 写入 signUp 子元素
     *   → 写虚拟人-3 加批记录(verifyStatus=9) → complete 当前任务(流程进入加批子节点)
     * - 不满足: 不 complete, 留给真实审批人(加批按钮已屏蔽)
     */
    private void processConditionAutoSignUpNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, DelegateTask delegateTask) {

        if (!StringConstants.CONDITION_AUTO_SIGN_UP_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
        } catch (Exception e) {
            log.error("条件自动加批节点条件判断异常, processNumber={}, elementId={}", processNumber, elementId, e);
        }

        //仅当条件满足时才自动加批; 否则留给真实审批人
        if (Boolean.TRUE.equals(conditionResult)) {
            //幂等检查: 已加批过则跳过(防止加批后回到审批人时重复触发)
            if (bpmVariableSignUpPersonnelBizService.hasSignUpPersonnel(processNumber, elementId)) {
                log.info("条件自动加批节点已加批过, 跳过重复触发, processNumber={}, elementId={}", processNumber, elementId);
                return;
            }
            BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
            List<BaseIdTranStruVo> autoSignUpUsers = null;
            if (configJson != null && configJson.getAutoSignUpConf() != null) {
                // 增强规则: 运行期实时解析(角色/领导链/HRBP 等, 基准人为发起人)
                String startUserId = null;
                BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
                if (bpmBusinessProcess != null) {
                    startUserId = bpmBusinessProcess.getCreateUser();
                }
                autoSignUpUsers = autoSignUpAssigneeResolver.resolve(configJson.getAutoSignUpConf(), startUserId, businessDataVo);
                if (autoSignUpUsers != null && autoSignUpUsers.isEmpty()) {
                    log.info("条件自动加批规则解析结果为空, 视为条件不满足, 留给审批人, processNumber={}, elementId={}", processNumber, elementId);
                    return;
                }
            } else if (configJson != null) {
                // 旧数据回退: 直接人员列表
                autoSignUpUsers = configJson.getAutoSignUpUsers();
            }

            if (CollectionUtils.isEmpty(autoSignUpUsers)) {
                log.error("条件自动加批节点未配置加批人, 跳过, processNumber={}, elementId={}", processNumber, elementId);
                return;
            }
            //解析真实 assignee 名称(回路 personnel 名称用, 从节点配置 personnelConf.employees 匹配)
            String assigneeId = delegateTask.getAssignee();
            String assigneeName = "";
            if (configJson != null && configJson.getApproverConf() != null
                    && configJson.getApproverConf().getPersonnelConf() != null
                    && !CollectionUtils.isEmpty(configJson.getApproverConf().getPersonnelConf().getEmployees())) {
                assigneeName = configJson.getApproverConf().getPersonnelConf().getEmployees().stream()
                        .filter(o -> o.getEmplId() != null && o.getEmplId().equals(assigneeId))
                        .map(BpmnNodeApproverConfJson.EmployeeInfo::getEmplName)
                        .findFirst().orElse("");
            }
            bpmVariableSignUpPersonnelBizService.insertSignUpPersonnel(taskService, delegateTask.getId(), processNumber, elementId, assigneeId, assigneeName, autoSignUpUsers);

            String assigneeSkipName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeSkipName);
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId())
                    .verifyUserName(assigneeSkipName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_SIGN_UP.getCode())
                    .verifyDesc(String.format(StringConstants.AF_CONDITION_AUTO_SIGNUP_COMMENT, conditionResult))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
            ((TaskEntity) delegateTask).complete(varMap, false);
        }
        //conditionResult == false 或 null: 不 complete, 留给真实审批人
    }

    /**
     * 条件自动转办节点处理:
     * - 复用条件评估逻辑 (formAdaptor.automaticCondition)
     * - 满足条件: 读 autoTransferConf → 类型1 target=transferToUser; 类型2 按 assignee 查映射
     *   target 非空且 ≠ 当前 assignee → setAssignee + 写 BpmFlowrunEntrust 委托记录(original→actual)
     * - 不在映射/不满足: 不操作, 任务保留原审批人; 不 complete(任务继续, 仅换人)
     */
    private void processConditionAutoTransferNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, DelegateTask delegateTask) {

        if (!StringConstants.CONDITION_AUTO_TRANSFER_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
        } catch (Exception e) {
            log.error("条件自动转办节点条件判断异常, processNumber={}, elementId={}", processNumber, elementId, e);
        }

        //仅当条件满足时才自动转办; 否则留给真实审批人
        if (Boolean.TRUE.equals(conditionResult)) {
            BpmnNodeConfigJson configJson = formAdaptor.loadNodeConfigJson(businessDataVo);
            Object autoTransferConf = configJson == null ? null : configJson.getAutoTransferConf();
            if (autoTransferConf == null) {
                log.error("条件自动转办节点未配置转办设置, 跳过, processNumber={}, elementId={}", processNumber, elementId);
                return;
            }
            com.alibaba.fastjson2.JSONObject confJson = com.alibaba.fastjson2.JSON.parseObject(com.alibaba.fastjson2.JSON.toJSONString(autoTransferConf));
            Integer transferType = confJson.getInteger("transferType");
            String oldUserId = delegateTask.getAssignee();
            String oldUserName = delegateTask instanceof TaskEntity ? ((TaskEntity) delegateTask).getAssigneeName() : "";
            BaseIdTranStruVo target = null;
            if (transferType != null && transferType == 1) {
                target = confJson.getObject("transferToUser", BaseIdTranStruVo.class);
            } else if (transferType != null && transferType == 2) {
                com.alibaba.fastjson2.JSONArray pairs = confJson.getJSONArray("transferPairs");
                if (pairs != null) {
                    for (int i = 0; i < pairs.size(); i++) {
                        com.alibaba.fastjson2.JSONObject pair = pairs.getJSONObject(i);
                        BaseIdTranStruVo from = pair.getObject("from", BaseIdTranStruVo.class);
                        if (from != null && from.getId() != null && from.getId().equals(oldUserId)) {
                            target = pair.getObject("to", BaseIdTranStruVo.class);
                            break;
                        }
                    }
                }
            }
            if (target != null && target.getId() != null && !target.getId().equals(oldUserId)) {
                delegateTask.setAssignee(target.getId());
                if (delegateTask instanceof TaskEntity) {
                    ((TaskEntity) delegateTask).setAssigneeName(target.getName());
                }
                BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
                entrust.setType(1);
                entrust.setRuntaskid(delegateTask.getId());
                entrust.setActual(target.getId());
                entrust.setActualName(target.getName());
                entrust.setOriginal(oldUserId);
                entrust.setOriginalName(oldUserName);
                entrust.setIsRead(2);
                entrust.setProcDefId(formCode);
                entrust.setRuninfoid(delegateTask.getProcessInstanceId());
                bpmFlowrunEntrustService.addFlowrunEntrust(entrust);
                log.info("条件自动转办生效, 转办前: {}, 转办后: {}, processNumber={}, elementId={}", oldUserId, target.getId(), processNumber, elementId);
            }
        }
        //conditionResult == false 或 null: 不转办, 留给真实审批人
    }

    /**
     * 条件抄送节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 与 copyNodeV2 的关键差异: 仅当 conditionResult==true 时才写 BpmProcessForward 抄送记录
     * - 与条件审批节点的关键差异: 无论条件结果如何都 complete; 条件不满足时仅跳过抄送动作
     * - 不调用 formAdaptor.automaticAction (那是 auto node 专属副作用)
     */
    private void processConditionCopyNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, String procInstId, DelegateTask delegateTask) {

        if (!StringConstants.CONDITION_COPY_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        // === 条件评估 (复用 auto node 的逻辑) ===
        FormOperationAdaptor formAdaptor = prepareFormAdaptor(processNumber, elementId, formCode, businessDataVo, isOutSide);

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
        } catch (Exception e) {
            log.error("条件抄送节点条件判断异常, processNumber={}, elementId={}", processNumber, elementId, e);
        }

        // === 无论条件如何都 complete (与 copyNodeV2 一致, assignee=CC_NODE) ===
        String assignee = AFSpecialAssigneeEnum.CC_NODE.getId();
        String assigneeName = AFSpecialAssigneeEnum.CC_NODE.getDesc();
        if(delegateTask instanceof TaskEntity){
            delegateTask.setAssignee(assignee);
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
        }

        // === 仅条件满足时写抄送记录 ===
        if (Boolean.TRUE.equals(conditionResult)) {
            List<BpmProcessForward> bpmProcessForwards = bpmProcessForwardService.list(AFWrappers.<BpmProcessForward>lambdaTenantQuery()
                    .eq(BpmProcessForward::getProcessInstanceId, procInstId)
                    .eq(BpmProcessForward::getForwardUserId, assignee));
            if(CollectionUtils.isEmpty(bpmProcessForwards)){
                bpmProcessForwardService.addProcessForward(BpmProcessForward.builder()
                        .createTime(new Date())
                        .createUserId(assignee)
                        .forwardUserId(assignee)
                        .ForwardUserName(assigneeName)
                        .processInstanceId(procInstId)
                        .processNumber(processNumber)
                        .build());
            }
        }

        // === 写 verifyInfo (文案随条件结果变化) ===
        String comment = Boolean.TRUE.equals(conditionResult)
                ? "(抄送给"+assigneeName+")自动通过"
                : String.format(StringConstants.AF_CONDITION_COPY_SKIP_COMMENT, conditionResult);
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(delegateTask.getName())
                .taskId(delegateTask.getId())
                .runInfoId(procInstId)
                .verifyUserId(assignee)
                .verifyUserName(assigneeName)
                .taskDefKey(delegateTask.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(comment)
                .processCode(processNumber)
                .build();
        bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
    }

    private void processCopyV2(BpmnNodeLabelVO nodeLabelVO, String procInstId, String assignee, String assigneeName, String processNumber,DelegateTask delegateTask) {
        if(!StringConstants.COPY_NODEV2.equals(nodeLabelVO.getLabelValue())){
            return;
        }
        List<BpmProcessForward> bpmProcessForwards = bpmProcessForwardService.list(AFWrappers.<BpmProcessForward>lambdaTenantQuery()
                .eq(BpmProcessForward::getProcessInstanceId, procInstId)
                .eq(BpmProcessForward::getForwardUserId, assignee));

        String asseeName="";
        if(delegateTask instanceof TaskEntity){
            delegateTask.setAssignee(AFSpecialAssigneeEnum.CC_NODE.getId());
            assigneeName=AFSpecialAssigneeEnum.CC_NODE.getDesc()+"("+((TaskEntity) delegateTask).getAssigneeName()+")";
            ((TaskEntity)delegateTask).setAssigneeName(asseeName);
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME,asseeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
        }
        if(CollectionUtils.isEmpty(bpmProcessForwards)){
            bpmProcessForwardService.addProcessForward(BpmProcessForward.builder()
                    .createTime(new Date())
                    .createUserId(assignee)
                    .forwardUserId(assignee)
                    .ForwardUserName(assigneeName)
                    .processInstanceId(procInstId)
                    .processNumber(processNumber)
                    .build());
        }
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(delegateTask.getName())
                .taskId(delegateTask.getId())
                .runInfoId(procInstId)
                .verifyUserId(delegateTask.getAssignee())
                .verifyUserName(assigneeName)
                .taskDefKey(delegateTask.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc("(抄送给"+assigneeName+")"+"自动通过")
                .processCode(processNumber)
                .build();
        bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);

    }

    private void processCopy(String elementId, String processNumber, String procInstId,BpmnNodeLabelVO nodeLabelVO) {
        if (!StringConstants.COPY_NODE.equals(nodeLabelVO.getLabelValue())) {
            return; 
        }
        //如果是最后一个节点通知,在BpmnExecutionListener里面处理,这里跳过,减少数据库查询
        if(StringConstants.LASTNODE_COPY.equals(elementId)){
            return;
        }
        List<String> nodeIdsByeElementId = bpmVariableMapper.getNodeIdsByeElementId(processNumber, elementId);
        if(!CollectionUtils.isEmpty(nodeIdsByeElementId)){
            String nodeId = nodeIdsByeElementId.get(0);
            LambdaQueryWrapper<BpmProcessForward> qryWrapper = Wrappers.<BpmProcessForward>lambdaQuery()
                    .eq(BpmProcessForward::getProcessNumber, processNumber)
                    .eq(BpmProcessForward::getNodeId, nodeId);
            BpmProcessForward processForward=new BpmProcessForward();
            processForward.setProcessInstanceId(procInstId);
            processForward.setIsDel(0);//recover the default state,so that the forward record can be visible
            bpmProcessForwardService.update(processForward, qryWrapper);
        }
    }

    private void processAutoSkipNode(BpmnNodeLabelVO nodeLabelVO,String nodeAssignee,String procInstId,String startUserName,String processNumber,DelegateTask delegateTask){
        if (!StringConstants.SKIPPED_ASSIGNEE.equals(nodeLabelVO.getLabelValue())) {
            return;
        }
        String labelName = nodeLabelVO.getLabelName();
        List<String> skippedAssigneeIds = Arrays.stream(labelName.split(",")).filter(StringUtils::hasText).collect(Collectors.toList());
        List<String> currentSkippedAssignee = skippedAssigneeIds.stream().filter(a -> a.contains(nodeAssignee)).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(currentSkippedAssignee)){
            String assigneeName=((TaskEntity)delegateTask).getAssigneeName();
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME,assigneeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
            //save process verify info
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(procInstId)
                    .verifyUserId(delegateTask.getAssignee())
                    .verifyUserName(startUserName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc(StringConstants.AF_AUTO_SKIP_COMMENT)
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
        }
    }
    @Override
    public int order() {
        return 0;
    }
}
