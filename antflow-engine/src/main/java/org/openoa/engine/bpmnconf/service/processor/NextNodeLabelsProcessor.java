package org.openoa.engine.bpmnconf.service.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.BpmProcessForward;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.FilterUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.engine.bpmnconf.adp.processoperation.ForwardToNodeImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
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
            processAutomaticNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide,delegateTask);
            processAutoAdvanceNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, procInstId, delegateTask);
            processAutoSkipNode(nodeLabelVO, assignee, procInstId, assigneeName, processNumber, delegateTask);
            processConditionApproveNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide, delegateTask);
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

    private void processAutomaticNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide,DelegateTask delegateTask) {

        if (!StringConstants.AUTOMATIC_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        businessDataVo.setProcessNumber(processNumber);
        businessDataVo.setTaskDefKey(elementId);
        businessDataVo.setFormCode(formCode);
        businessDataVo.setIsOutSideAccessProc(isOutSide);
        FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(businessDataVo);
        if(formAdaptor==null){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR,"未能根据流程formcode找到流程适配器信息!");
        }
        businessDataVo.setLfConditions(businessDataVo.getLfFields());
        String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
            formAdaptor.automaticAction(businessDataVo, conditionResult);
        } catch (Exception e) {
            log.error("自动节点条件判断或动作执行异常, processNumber={}, elementId={}", processNumber, elementId, e);
        } finally {
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId())
                    .verifyUserName(assigneeName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc(String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT,conditionResult))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
        }
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

            // UUID → 主键 (t_bpmn_node.node_id → t_bpmn_node.id)
            // 同一 node_id 在不同流程模板(复制)中可能重复, 必须用 confId 限定到当前流程
            // 同一流程内 node_id 唯一, 不使用 LIMIT 1 以兼容多数据库
            BpmnConfVo bpmnConfVo = businessDataVo.getBpmnConfVo();
            if (bpmnConfVo == null || bpmnConfVo.getId() == null) {
                throw new AFBizException("自动推进: businessDataVo.bpmnConfVo 未填充, 无法定位流程配置, processNumber=" + processNumber + ", elementId=" + elementId);
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
                throw new AFBizException("自动推进目标节点不存在, processNumber=" + processNumber + ", confId=" + confId + ", nodeUuid=" + targetNodeUuid);
            }
            String targetNodeName = targetNode.getNodeName();
            String targetPrimaryKey = String.valueOf(targetNode.getId());

            // 主键 → elementId (taskDefKey)
            List<String> targetElementIds = bpmVariableMapper.getElementIdsdByNodeId(processNumber, targetPrimaryKey);
            if (CollectionUtils.isEmpty(targetElementIds)) {
                throw new AFBizException(String.format("自动推进: 未能根据nodeId获取目标节点taskDefKey, processNumber=%s, targetNodeId=%s", processNumber, targetPrimaryKey));
            }
            String targetElementId = targetElementIds.get(0);

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
            Map<String, Object> varMap = new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
            ((TaskEntity) delegateTask).complete(varMap, false);
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo.builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(assigneeId)
                    .verifyUserName(assigneeName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc(String.format(StringConstants.AF_AUTO_EVALUATE_SKIP_COMMENT, conditionResult))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
        }
    }

    /**
     * 条件审批节点处理:
     * - 复用 auto node 的条件评估逻辑 (formAdaptor.automaticCondition)
     * - 与 auto node 的关键差异: 仅当 conditionResult==true 时才 complete 任务;
     *   conditionResult==false 或 null 时, 不 complete, 留给真实审批人人工处理
     * - 不调用 formAdaptor.automaticAction (那是 auto node 专属副作用)
     */
    private void processConditionApproveNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide, DelegateTask delegateTask) {

        if (!StringConstants.CONDITION_APPROVE_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }

        businessDataVo.setProcessNumber(processNumber);
        businessDataVo.setTaskDefKey(elementId);
        businessDataVo.setFormCode(formCode);
        businessDataVo.setIsOutSideAccessProc(isOutSide);
        FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(businessDataVo);
        if(formAdaptor==null){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR,"未能根据流程formcode找到流程适配器信息!");
        }
        if(CollectionUtils.isEmpty(businessDataVo.getLfConditions())&&Objects.equals(businessDataVo.getIsLowCodeFlow(),1)){
            UDLFApplyVo vo=(UDLFApplyVo)businessDataVo;
            vo.setLfConditions(vo.getLfFields());
        }

        Boolean conditionResult = null;
        try {
            conditionResult = formAdaptor.automaticCondition(businessDataVo);
        } catch (Exception e) {
            log.error("条件审批节点条件判断异常, processNumber={}, elementId={}", processNumber, elementId, e);
        }

        //仅当条件满足时才自动 complete; 否则留给真实审批人
        if (Boolean.TRUE.equals(conditionResult)) {
            String assigneeName = AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc();
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(delegateTask.getProcessInstanceId())
                    .verifyUserId(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId())
                    .verifyUserName(assigneeName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc(String.format(StringConstants.AF_CONDITION_APPROVE_AUTO_PASS_COMMENT, conditionResult))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
        }
        //conditionResult == false 或 null: 不 complete, 留给真实审批人
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
        businessDataVo.setProcessNumber(processNumber);
        businessDataVo.setTaskDefKey(elementId);
        businessDataVo.setFormCode(formCode);
        businessDataVo.setIsOutSideAccessProc(isOutSide);
        FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(businessDataVo);
        if(formAdaptor==null){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR,"未能根据流程formcode找到流程适配器信息!");
        }
        if(CollectionUtils.isEmpty(businessDataVo.getLfConditions())&&Objects.equals(businessDataVo.getIsLowCodeFlow(),1)){
            businessDataVo.setLfConditions(businessDataVo.getLfFields());
        }

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
