package org.openoa.engine.bpmnconf.service.processor;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmUserAutoApprove;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.jsonconf.BpmnNodeAutoNodeConfJson;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.base.vo.UserAutoApproveVo;
import org.openoa.engine.bpmnconf.adp.processoperation.AutoConditionEvaluator;
import org.openoa.engine.bpmnconf.service.biz.UserAutoApproveBizServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户自动审批设置 运行时处理器。
 * order=2: 在委托(order=1)之后执行, 评估实际处理人的自动审批配置。
 * 命中条件: enabled=1 ∧ 归属人==当前assignee ∧ config.bpmnCode==活跃bpmnCode ∧ 节点范围命中 ∧ (无条件 或 条件评估为true)。
 * Fail-safe: 任何异常仅log, 不阻断流程。
 */
@Service
@Slf4j
public class NextNodeAutoApproveProcessor implements AntFlowNextNodeBeforeWriteProcessor {

    @Resource
    private UserAutoApproveBizServiceImpl userAutoApproveBizService;
    @Resource
    private FormFactory formFactory;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;

    @Override
    public void postProcess(BpmNextTaskDto bpmNextTaskDto) {
        try {
            doProcess(bpmNextTaskDto);
        } catch (Exception e) {
            log.error("自动审批处理异常, processNumber={}", bpmNextTaskDto.getProcessNumber(), e);
        }
    }

    private void doProcess(BpmNextTaskDto dto) {
        DelegateTask delegateTask = dto.getDelegateTask();
        BusinessDataVo vo = dto.getBusinessDataVo();
        String formCode = dto.getFormCode();
        if (delegateTask == null || vo == null || !StringUtils.hasText(formCode)) {
            return;
        }
        String assignee = delegateTask.getAssignee();
        if (!StringUtils.hasText(assignee)) {
            return;
        }
        String activeBpmnCode = vo.getBpmnCode();

        if (!StringUtils.hasText(activeBpmnCode)) {
            return;
        }
        List<BpmUserAutoApprove> configs = userAutoApproveBizService.listForRuntime(assignee, formCode, activeBpmnCode);
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }
        String taskDefKey = dto.getTaskDefKey();
        for (BpmUserAutoApprove config : configs) {
            HitResult hit = matchAndEvaluate(config, dto, vo, taskDefKey);
            if (hit == null) {
                continue;
            }
            completeAsAutoApprove(delegateTask, assignee, config, hit, dto);
            break;
        }
    }

    /**
     * 命中结果: hit=是否命中; hasCondition/conditionResult 用于审批意见文案
     */
    private static class HitResult {
        private final boolean hasCondition;
        private final Boolean conditionResult;

        private HitResult(boolean hasCondition, Boolean conditionResult) {
            this.hasCondition = hasCondition;
            this.conditionResult = conditionResult;
        }
    }

    /**
     * 返回命中结果; 未命中返回 null。
     */
    private HitResult matchAndEvaluate(BpmUserAutoApprove config, BpmNextTaskDto dto, BusinessDataVo vo, String taskDefKey) {
        //节点范围
        if (StringUtils.hasText(config.getNodeScopeJson())) {
            List<UserAutoApproveVo.NodeScopeItem> scope =
                    JSON.parseArray(config.getNodeScopeJson(), UserAutoApproveVo.NodeScopeItem.class);
            boolean inScope = scope != null && scope.stream().anyMatch(i -> Objects.equals(i.getElementId(), taskDefKey));
            if (!inScope) {
                return null;
            }
        }
        //无条件 → 直接命中
        BpmnNodeAutoNodeConfJson cond = StringUtils.hasText(config.getConditionJson())
                ? JSON.parseObject(config.getConditionJson(), BpmnNodeAutoNodeConfJson.class)
                : null;
        if (cond == null || CollectionUtils.isEmpty(cond.getConditionList())) {
            return new HitResult(false, null);
        }
        //有条件: 仅LF可评估
        if (!(vo instanceof UDLFApplyVo)) {
            return null;
        }
        UDLFApplyVo lfVo = (UDLFApplyVo) vo;
        vo.setProcessNumber(dto.getProcessNumber());
        vo.setTaskDefKey(taskDefKey);
        vo.setFormCode(dto.getFormCode());
        if (CollectionUtils.isEmpty(lfVo.getLfFields())) {
            try {
                FormOperationAdaptor adaptor = formFactory.getFormAdaptor(vo);
                if (adaptor == null) {
                    return null;
                }
                adaptor.queryData(vo);
            } catch (Exception e) {
                log.warn("自动审批拉取表单数据失败, processNumber={}, msg={}", dto.getProcessNumber(), e.getMessage());
                return null;
            }
        }
        if (CollectionUtils.isEmpty(lfVo.getLfFields())) {
            return null;
        }
        Boolean result = AutoConditionEvaluator.evaluate(cond.getConditionList(), cond.getGroupRelation(), lfVo.getLfFields());
        if (!Boolean.TRUE.equals(result)) {
            return null;
        }
        return new HitResult(true, result);
    }

    private void completeAsAutoApprove(DelegateTask delegateTask, String assignee, BpmUserAutoApprove config,
                                       HitResult hit, BpmNextTaskDto dto) {
        String assigneeName = delegateTask instanceof TaskEntity ? ((TaskEntity) delegateTask).getAssigneeName() : "";
        try {
            Map<String, Object> varMap = new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, assigneeName);
            ((TaskEntity) delegateTask).complete(varMap, false);
        } catch (Exception e) {
            //任务可能已被前序处理器complete, 静默跳过
            log.warn("自动审批complete失败(任务可能已完成), processNumber={}, msg={}", dto.getProcessNumber(), e.getMessage());
            return;
        }
        String desc;
        if (StringUtils.hasText(config.getDefaultComment())) {
            desc = StringConstants.AF_AUTO_APPROVE_PREFIX + config.getDefaultComment();
        } else if (hit.hasCondition) {
            desc = StringConstants.AF_AUTO_APPROVE_PREFIX
                    + String.format(StringConstants.AF_AUTO_APPROVE_COMMENT, hit.conditionResult);
        } else {
            desc = StringConstants.AF_AUTO_APPROVE_PREFIX + StringConstants.AF_AUTO_APPROVE_UNCONDITIONAL_COMMENT;
        }
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(delegateTask.getName())
                .taskId(delegateTask.getId())
                .runInfoId(delegateTask.getProcessInstanceId())
                .verifyUserId(assignee)
                .verifyUserName(assigneeName)
                .taskDefKey(delegateTask.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(desc)
                .processCode(dto.getProcessNumber())
                .build();
        bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
        log.info("自动审批命中: processNumber={}, assignee={}, taskDefKey={}", dto.getProcessNumber(), assignee, dto.getTaskDefKey());
    }

    @Override
    public int order() {
        return 2;
    }
}
