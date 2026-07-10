package org.openoa.engine.bpmnconf.service.processor;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.NodeFormAssigneePropertyEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.BpmnNodeFormRelatedUserConf;
import org.openoa.base.service.AfRoleService;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeFormRelatedUserConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.LFMainFieldService;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表单中相关人员(node_property=16)审批人实时刷新处理器.
 *
 * <p>背景:流程发起时 {@code LowFlowApprovalService#submitData/previewSetCondition} 会把表单字段值解析成
 * 审批人并固化到 activiti 变量中,后续表单字段变更不会反映到已确定的审批人.
 * 本处理器在节点到达(BpmnTaskListener)时,按当前最新的表单数据重新解析审批人,
 * 若当前任务审批人已不再是表单所指人员则替换之,并写入 bpm_flowrun_entrust 审计记录.</p>
 *
 * <p>不借用 t_user_entrust(用户主动委托配置表),避免在用户委托列表中产生假记录;
 * 审计复用 bpm_flowrun_entrust,其自带 node_id 与 action_type,这里使用 action_type=4 表示表单人员刷新.</p>
 *
 * <p>覆盖场景:单人节点、会签(多实例,每个 task 已有 assignee)同数量替换.
 * 局限:或签未认领(assignee 为空)时不处理;会签人员数量发生增减时只能尽力而为
 * (数量变更需在变量层 t_bpm_variable_multiplayer_personnel 更新,属于后续 Variant B).</p>
 */
@Slf4j
@Service
public class FormRelatedAssigneeRefreshProcessor implements AntFlowNextNodeBeforeWriteProcessor {

    /** bpm_flowrun_entrust.action_type: 表单中相关人员刷新 */
    private static final int ACTION_TYPE_FORM_RELATED_REFRESH = 4;

    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private LFMainFieldService lfMainFieldService;
    @Autowired
    private AfUserService afUserService;
    @Autowired
    private AfRoleService afRoleService;
    @Autowired
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Autowired
    private BpmnNodeFormRelatedUserConfService bpmnNodeFormRelatedUserConfService;

    @Override
    public void postProcess(BpmNextTaskDto bpmNextTaskDto) {
        DelegateTask delegateTask = bpmNextTaskDto.getDelegateTask();
        if (delegateTask == null) {
            return;
        }
        String bpmnCode = bpmNextTaskDto.getBpmnCode();
        String elementId = bpmNextTaskDto.getTaskDefKey();
        String businessId = bpmNextTaskDto.getBusinessId();
        String processNumber = bpmNextTaskDto.getProcessNumber();
        if (StringUtils.isEmpty(bpmnCode) || StringUtils.isEmpty(elementId) || StringUtils.isEmpty(businessId)) {
            return;
        }
        // 第一层门控:目前仅支持低代码LF流程,非低代码流程直接跳过
        BusinessDataVo businessDataVo = bpmNextTaskDto.getBusinessDataVo();
        if(businessDataVo==null){
            return;
        }
        if ( !Integer.valueOf(1).equals(businessDataVo.getIsLowCodeFlow())) {
            return;
        }
        // 第二层门控:流程不含表单中选取人员节点时直接跳过,减少查询次数
        BpmnConfVo bpmnConfVo = businessDataVo.getBpmnConfVo();
        if (bpmnConfVo == null) {
            return;
        }
        Integer extraFlags = bpmnConfVo.getExtraFlags();
        if (extraFlags == null || !BpmnConfFlagsEnum.HAS_FORM_RELATED_ASSIGNEES.flagsContainsCurrent(extraFlags)) {
            return;
        }
        // 仅对"表单中相关人员"节点生效
        BpmnNode node = findFormRelatedNode(bpmnCode, elementId);
        if (node == null) {
            return;
        }
        List<BpmnNodeFormRelatedUserConf> confs = getFormRelatedConfs(node);
        if (CollectionUtils.isEmpty(confs)) {
            return;
        }

        Long mainId;
        try {
            mainId = Long.parseLong(businessId);
        } catch (NumberFormatException e) {
            // 非低代码流程的 businessId,不处理
            return;
        }

        // 读取当前最新的表单字段值,解析出人员/角色id
        List<String> formValueIds = collectFormValueIds(confs, mainId);
        if (formValueIds.isEmpty()) {
            return;
        }

        // 根据 valueType 解析最终审批人
        Integer formAssigneeProperty = confs.get(0).getValueType();
        List<BaseIdTranStruVo> resolved = resolveAssignees(formAssigneeProperty, formValueIds);
        if (resolved.isEmpty()) {
            return;
        }

        String oldUserId = delegateTask.getAssignee();
        if (StringUtils.isEmpty(oldUserId)) {
            // 或签未认领时 assignee 为空,候选人的刷新需在变量层处理,此处跳过
            return;
        }

        Set<String> resolvedIds = resolved.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toSet());
        if (resolvedIds.contains(oldUserId)) {
            // 当前审批人仍是表单所指人员,无需变更
            return;
        }

        // 选择替换目标:优先按发起时审批人列表中的位置对应到新列表同位置(会签同数量替换场景),
        // 找不到位置时退化为新列表首个
        BaseIdTranStruVo target = pickReplacement(oldUserId, resolved, processNumber, elementId);
        if (target == null) {
            return;
        }
        String newUserId = target.getId();
        String newUserName = target.getName();
        if (StringUtils.isEmpty(newUserId) || newUserId.equals(oldUserId)) {
            return;
        }

        String oldUserName = (delegateTask instanceof TaskEntity) ? ((TaskEntity) delegateTask).getAssigneeName() : "";
        delegateTask.setAssignee(newUserId);
        if (delegateTask instanceof TaskEntity) {
            ((TaskEntity) delegateTask).setAssigneeName(newUserName);
        }

        bpmFlowrunEntrustService.addFlowrunEntrust(
                newUserId, newUserName, oldUserId, oldUserName,
                delegateTask.getId(), 1,
                delegateTask.getProcessInstanceId(), bpmNextTaskDto.getFormCode(),
                elementId, ACTION_TYPE_FORM_RELATED_REFRESH);
        log.info("表单中相关人员刷新: 节点[{}] 审批人 {} -> {}", elementId, oldUserId, newUserId);
    }

    private BpmnNode findFormRelatedNode(String bpmnCode, String elementId) {
        List<BpmnNode> nodes = bpmVariableMultiplayerMapper.getNodeByElementId(bpmnCode, elementId);
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        return nodes.stream()
                .filter(n -> n.getNodeProperty() != null
                        && n.getNodeProperty().equals(NodePropertyEnum.NODE_PROPERTY_FORM_RELATED.getCode())
                        && (n.getIsDel() == null || n.getIsDel() == 0))
                .findFirst()
                .orElse(null);
    }

    /**
     * 从配置表 t_bpmn_node_form_related_user_conf 查询当前节点的表单关联用户配置.
     * (当前分支尚未迁移到 node_config_json,仍使用独立配置表)
     */
    private List<BpmnNodeFormRelatedUserConf> getFormRelatedConfs(BpmnNode node) {
        List<BpmnNodeFormRelatedUserConf> confs = bpmnNodeFormRelatedUserConfService.list(
                Wrappers.<BpmnNodeFormRelatedUserConf>lambdaQuery()
                        .eq(BpmnNodeFormRelatedUserConf::getBpmnNodeId, node.getId())
                        .eq(BpmnNodeFormRelatedUserConf::getIsDel, 0));
        return confs == null ? Collections.emptyList() : confs;
    }

    private List<String> collectFormValueIds(List<BpmnNodeFormRelatedUserConf> confs, Long mainId) {
        List<String> ids = new ArrayList<>();
        for (BpmnNodeFormRelatedUserConf conf : confs) {
            List<String> fieldIds = parseFieldIds(conf.getValueJson());
            for (String fieldId : fieldIds) {
                List<LFMainField> rows = lfMainFieldService.list(
                        Wrappers.<LFMainField>lambdaQuery()
                                .eq(LFMainField::getMainId, mainId)
                                .eq(LFMainField::getFieldId, fieldId));
                ids.addAll(extractIds(rows));
            }
        }
        return ids;
    }

    private List<String> parseFieldIds(String valueJson) {
        if (StringUtils.isEmpty(valueJson)) {
            return Collections.emptyList();
        }
        List<BaseIdTranStruVo> formInfos = JSON.parseArray(valueJson, BaseIdTranStruVo.class);
        List<String> fieldIds = new ArrayList<>();
        for (BaseIdTranStruVo formInfo : formInfos) {
            if (formInfo != null && StringUtils.isNotEmpty(formInfo.getId())) {
                fieldIds.add(formInfo.getId());
            }
        }
        return fieldIds;
    }

    /**
     * 从 LFMainField 行中提取人员/角色id.
     * 兼容单值字符串、JSON数组字符串、多行多值三种存储形式.
     */
    private List<String> extractIds(List<LFMainField> rows) {
        List<String> ids = new ArrayList<>();
        if (rows == null) {
            return ids;
        }
        for (LFMainField row : rows) {
            String fv = row.getFieldValue();
            if (StringUtils.isEmpty(fv)) {
                if (row.getFieldValueNumber() != null) {
                    ids.add(String.valueOf(row.getFieldValueNumber().longValue()));
                }
                continue;
            }
            String trimmed = fv.trim();
            if (trimmed.startsWith("[")) {
                try {
                    List<Object> arr = JSON.parseArray(trimmed);
                    for (Object o : arr) {
                        if (o != null) {
                            ids.add(o.toString());
                        }
                    }
                    continue;
                } catch (Exception ignore) {
                    // 非标准JSON数组,按单值处理
                }
            }
            ids.add(fv);
        }
        return ids;
    }

    /**
     * 根据 valueType 调用对应服务解析审批人,逻辑与 FormRelatedPersonnelProvider 保持一致.
     */
    private List<BaseIdTranStruVo> resolveAssignees(Integer formAssigneeProperty, List<String> ids) {
        if (formAssigneeProperty == null) {
            return afUserService.queryUserByIds(ids);
        }
        NodeFormAssigneePropertyEnum propertyEnum = NodeFormAssigneePropertyEnum.getByCode(formAssigneeProperty);
        if (propertyEnum == null) {
            return afUserService.queryUserByIds(ids);
        }
        switch (propertyEnum) {
            case FORM_ASSIGNEE:
                return afUserService.queryUserByIds(ids);
            case FORM_ROLE:
                return afRoleService.queryUserByRoleIds(ids);
            case FORM_USER_HRBP:
                return afUserService.queryEmployeeHrpbByEmployeeIds(ids);
            case FORM_USER_DIRECT_LEADER:
                return afUserService.queryEmployeeDirectLeaderByIds(ids);
            case FORM_USER_DEPART_LEADER:
                return afUserService.queryDepartmentLeaderByIds(ids);
            default:
                // FORM_DEPART_LEADER/FORM_USER_LEVEL_LEADER/FORM_USER_LOOP_LEADER 等尚未实现的类型返回空,
                // 与 FormRelatedPersonnelProvider 保持一致
                return Collections.emptyList();
        }
    }

    private BaseIdTranStruVo pickReplacement(String oldUserId, List<BaseIdTranStruVo> resolved,
                                             String processNumber, String elementId) {
        if (CollectionUtils.isEmpty(resolved)) {
            return null;
        }
        List<BaseIdTranStruVo> startAssignees = bpmVariableMultiplayerMapper.getAssigneeByElementId(processNumber, elementId);
        if (!CollectionUtils.isEmpty(startAssignees)) {
            int idx = -1;
            for (int i = 0; i < startAssignees.size(); i++) {
                if (oldUserId.equals(startAssignees.get(i).getId())) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0 && idx < resolved.size()) {
                return resolved.get(idx);
            }
        }
        return resolved.get(0);
    }

    @Override
    public int order() {
        // 先于 NextNodeForwardProcessor(委托,order=1) 执行,使委托基于刷新后的审批人叠加
        return 0;
    }
}
