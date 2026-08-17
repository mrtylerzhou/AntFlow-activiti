package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.processoperation.AutoConditionEvaluator;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessDiagnosisBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ProcessStateEnum.HANDLING_STATE;

/**
 * 流程诊断 (流程管理-流程监控-更多-流程诊断)
 *
 * <p>设计: .scratch/process-diagnosis-design.md</p>
 *
 * <p>归因短路矩阵:</p>
 * <ol>
 *   <li>present: nodeId ∈ (af_hi_taskinst ∪ af_ru_task).node_id → EXISTS + 加批归因明细</li>
 *   <li>NOT_REACHED: 流程未结束 && 目标先序序 > 当前停留节点先序序</li>
 *   <li>CONDITION_MISS: 目标所在网关全分支横评, 目标分支未命中</li>
 *   <li>SIGN_SKIP: bpm_flowrun_entrust actionType=3 (减签) 记录存在</li>
 *   <li>UNKNOWN: 裸列原始数据</li>
 * </ol>
 */
@Slf4j
@Service
public class ProcessDiagnosisBizServiceImpl implements ProcessDiagnosisBizService {

    private static final int NODE_TYPE_START = 1;
    private static final int NODE_TYPE_GATEWAY = 2;
    private static final int NODE_TYPE_CONDITION_BRANCH = 3;
    /** 加批按钮 buttonType (const.js approvalButtonConf addApproval) */
    private static final int BUTTON_TYPE_ADD_APPROVAL = 19;
    /** bpm_verify_info.verify_status = 9 加批 */
    private static final int VERIFY_STATUS_ADD_APPROVAL = 9;

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmnConfBizServiceImpl bpmnConfBizService;
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private ActHiTaskinstServiceImpl actHiTaskinstService;
    @Autowired
    private BpmFlowrunEntrustService bpmFlowrunEntrustService;
    @Autowired
    private BpmVariableService bpmVariableService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;
    @Resource
    private BpmVerifyInfoMapper bpmVerifyInfoMapper;

    // ==================================================================================
    // diagnosisInit
    // ==================================================================================

    @Override
    public ProcessDiagnosisInitVo diagnosisInit(String processNumber) {
        BpmBusinessProcess process = queryProcess(processNumber);
        BpmnConfVo confVo = bpmnConfBizService.detail(process.getVersion());
        if (confVo == null || confVo.getId() == null) {
            throw new AFBizException("未找到流程版本对应的模板配置, bpmnCode=" + process.getVersion());
        }

        ProcessDiagnosisInitVo vo = ProcessDiagnosisInitVo.builder()
                .processNumber(processNumber)
                .confId(confVo.getId())
                .bpmnCode(process.getVersion())
                .formCode(confVo.getFormCode())
                .isLowCodeFlow(process.getIsLowCodeFlow())
                .processFinished(!Objects.equals(process.getProcessState(), HANDLING_STATE.getCode()))
                .initiatorUserId(process.getCreateUser())
                .initiatorUserName(resolveUserName(process))
                .formValues(loadFormValues(process, confVo.getFormCode()))
                .build();
        return vo;
    }

    // ==================================================================================
    // diagnoseNode
    // ==================================================================================

    @Override
    public NodeDiagnosisVo diagnoseNode(NodeDiagnosisRequestVo request) {
        if (request == null || StringUtils.isEmpty(request.getProcessNumber()) || request.getNodeId() == null) {
            throw new AFBizException("processNumber / nodeId 不能为空");
        }
        String processNumber = request.getProcessNumber();
        Long targetId = request.getNodeId();

        BpmBusinessProcess process = queryProcess(processNumber);
        BpmnConfVo confVo = bpmnConfBizService.detail(process.getVersion());
        boolean finished = !Objects.equals(process.getProcessState(), HANDLING_STATE.getCode());

        // ---- 设计树索引 ----
        List<BpmnNodeVo> nodes = confVo == null || confVo.getNodes() == null
                ? Collections.emptyList() : confVo.getNodes();
        Map<String, BpmnNodeVo> byUuid = new HashMap<>();
        Map<Long, BpmnNodeVo> byId = new HashMap<>();
        for (BpmnNodeVo n : nodes) {
            if (n.getNodeId() != null) {
                byUuid.put(n.getNodeId(), n);
            }
            if (n.getId() != null) {
                byId.put(n.getId(), n);
            }
        }
        BpmnNodeVo target = byId.get(targetId);
        String targetName = target != null && StringUtils.isNotEmpty(target.getNodeName())
                ? target.getNodeName() : String.valueOf(targetId);

        // ---- 审批真实路径 ----
        List<ActHiTaskinst> hiTasks = actHiTaskinstService.getBaseMapper().selectList(
                new QueryWrapper<ActHiTaskinst>().eq("PROC_INST_ID_", process.getProcInstId()));
        List<BpmVerifyInfoVo> ruTasks = Optional.ofNullable(
                bpmVerifyInfoMapper.findTaskInfor(process.getProcInstId())).orElse(Collections.emptyList());

        boolean present = hiTasks.stream().anyMatch(t -> Objects.equals(String.valueOf(targetId), t.getNodeId()))
                || ruTasks.stream().anyMatch(t -> Objects.equals(String.valueOf(targetId), t.getNodeId()));

        // 当前停留节点: 优先运行中任务, 否则最近一条历史任务
        String currentNodeId = ruTasks.stream()
                .map(BpmVerifyInfoVo::getNodeId)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElseGet(() -> hiTasks.stream()
                        .filter(t -> StringUtils.isNotEmpty(t.getNodeId()))
                        .max(Comparator.comparing(ActHiTaskinst::getStartTime,
                                Comparator.nullsFirst(Comparator.naturalOrder())))
                        .map(ActHiTaskinst::getNodeId)
                        .orElse(null));
        BpmnNodeVo currentNode = StringUtils.isNotEmpty(currentNodeId)
                ? byId.get(Long.valueOf(currentNodeId)) : null;
        String currentNodeName = currentNode != null && StringUtils.isNotEmpty(currentNode.getNodeName())
                ? currentNode.getNodeName()
                : (StringUtils.isNotEmpty(currentNodeId) ? currentNodeId : null);

        NodeDiagnosisVo.NodeDiagnosisVoBuilder builder = NodeDiagnosisVo.builder()
                .present(present)
                .expectationMismatch(request.getExpectedPresent() != null
                        && !request.getExpectedPresent().equals(present))
                .nodeName(targetName)
                .currentNodeId(currentNodeId)
                .currentNodeName(currentNodeName);

        // ---- 公共明细: 加减签/委托记录 (4.3), 加批记录 (4.2), 兜底 task 列表 ----
        List<NodeDiagnosisVo.EntrustRecordVo> entrustRecords = loadEntrustRecords(process.getProcInstId(), targetId);
        builder.entrustRecords(entrustRecords);
        builder.signupRecords(loadSignupRecords(processNumber, targetId));

        // 前驱真实节点(跳过网关/分支头)及其加批按钮配置
        BpmnNodeVo prevNode = findPrevRealNode(target, byUuid);
        boolean prevHasAddApproval = false;
        if (prevNode != null && prevNode.getButtons() != null
                && !CollectionUtils.isEmpty(prevNode.getButtons().getApprovalPage())) {
            prevHasAddApproval = prevNode.getButtons().getApprovalPage().stream()
                    .anyMatch(b -> b.getButtonType() != null && b.getButtonType() == BUTTON_TYPE_ADD_APPROVAL);
        }
        builder.prevNodeHasAddApproval(prevHasAddApproval)
                .prevNodeName(prevNode != null ? prevNode.getNodeName() : null);

        builder.rawTasks(loadRawTasks(hiTasks, ruTasks, targetId));

        // ---- 短路矩阵 ----
        if (present) {
            // 人员维度(4.3): 应审人/实际审批人/配置规则; 前提=节点存在
            String ruleDesc = ruleDescOf(target);
            List<NodeDiagnosisVo.ApproverVo> expected = evaluateExpectedApprovers(process, confVo, targetId);
            List<NodeDiagnosisVo.ApproverVo> actual = loadActualApprovers(hiTasks, ruTasks, targetId);
            builder.ruleDesc(ruleDesc)
                    .expectedApprovers(expected)
                    .actualApprovers(actual);
            if (StringUtils.isNotEmpty(request.getPersonId())) {
                builder.personDiagnosis(buildPersonDiagnosis(
                        request, expected, actual, entrustRecords, ruleDesc, targetName));
            }
            return builder.conclusionType("EXISTS")
                    .message("该节点在流程审批路径中实际存在过。")
                    .build();
        }

        // ② 尚未到达 (仅流程未结束时判断, 排在条件求值之前防伪结论)
        if (!finished) {
            Map<Long, Integer> orderMap = buildPreOrderIndex(nodes);
            Integer targetIdx = orderMap.get(targetId);
            Integer currentIdx = StringUtils.isNotEmpty(currentNodeId)
                    ? orderMap.get(Long.valueOf(currentNodeId)) : null;
            if (targetIdx != null && currentIdx != null && targetIdx > currentIdx) {
                return builder.conclusionType("NOT_REACHED")
                        .message(String.format("流程尚未执行到该节点, 当前停留节点: %s。",
                                currentNodeName != null ? currentNodeName : "未知"))
                        .build();
            }
        }

        // ③ 条件分支横评
        Map<String, Object> formValues = loadFormValues(process, confVo != null ? confVo.getFormCode() : null);
        BpmnNodeVo branchHead = findAncestorBranchHead(target, byUuid);
        if (branchHead != null) {
            List<NodeDiagnosisVo.BranchEvaluation> branches = evaluateBranchFamily(
                    branchHead, targetId, nodes, formValues);
            builder.branches(branches);
            NodeDiagnosisVo.BranchEvaluation targetBranch = branches.stream()
                    .filter(b -> Boolean.TRUE.equals(b.getContainsTarget()))
                    .findFirst().orElse(null);
            boolean someBranchHit = branches.stream().anyMatch(b -> Boolean.TRUE.equals(b.getHit()));
            NodeDiagnosisVo.BranchEvaluation hitBranch = branches.stream()
                    .filter(b -> Boolean.TRUE.equals(b.getHit()))
                    .findFirst().orElse(null);
            if (hitBranch == null) {
                hitBranch = branches.stream()
                        .filter(b -> Boolean.TRUE.equals(b.getIsDefault()))
                        .findFirst().orElse(null);
            }
            boolean targetBranchNotHit = targetBranch != null
                    && !Boolean.TRUE.equals(targetBranch.getHit())
                    && !Boolean.TRUE.equals(targetBranch.getIsDefault())
                    && (someBranchHit || hitBranch != null);
            if (targetBranchNotHit) {
                String hitName = hitBranch != null
                        ? (hitBranch.getBranchName() != null ? hitBranch.getBranchName() : "其他分支") : "其他分支";
                return builder.conclusionType("CONDITION_MISS")
                        .message(String.format("目标节点所在分支的条件未命中, 实际命中分支: %s (条件按当前表单值求值)。", hitName))
                        .build();
            }
            // 目标分支按当前值命中但流程未经过 → 表单后续被修改过或流程未到, 继续走后续归因
        }

        // ④ 减签跳过
        boolean hasRemoveSign = Optional.ofNullable(loadEntrustRecords(process.getProcInstId(), targetId))
                .orElse(Collections.emptyList()).stream()
                .anyMatch(r -> r.getActionType() != null && r.getActionType() == 3);
        if (hasRemoveSign) {
            return builder.conclusionType("SIGN_SKIP")
                    .message("该节点存在减签记录, 节点可能因人员被减签而跳过, 详见下方加减签记录。")
                    .build();
        }

        // ⑤ 兜底
        return builder.conclusionType("UNKNOWN")
                .message("无法自动归因, 以下为该节点相关的原始记录, 请结合审批记录与表单变更记录人工分析。")
                .build();
    }

    // ==================================================================================
    // helpers: business process / form values
    // ==================================================================================

    private BpmBusinessProcess queryProcess(String processNumber) {
        BpmBusinessProcess process = bpmBusinessProcessService.getBaseMapper().selectOne(
                new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", processNumber));
        if (ObjectUtils.isEmpty(process)) {
            throw new AFBizException("流程实例不存在: " + processNumber);
        }
        return process;
    }

    private String resolveUserName(BpmBusinessProcess process) {
        if (StringUtils.isNotEmpty(process.getUserName())) {
            return process.getUserName();
        }
        try {
            Map<String, String> map = employeeInfoProvider.provideEmployeeInfo(
                    Collections.singletonList(process.getCreateUser()));
            return map != null ? map.get(process.getCreateUser()) : null;
        } catch (Exception e) {
            log.warn("query initiator name failed, user={}", process.getCreateUser(), e);
            return null;
        }
    }

    /**
     * 当前业务表单真实值: LF → lfFields + lfFieldsMulti 合并(key=fieldId);
     * DIY → 业务实体 declared fields(key=字段名)。查询失败不阻塞诊断, 返回空 map。
     */
    private Map<String, Object> loadFormValues(BpmBusinessProcess process, String formCode) {
        Map<String, Object> values = new LinkedHashMap<>();
        try {
            JSONObject params = new JSONObject();
            params.put("processNumber", process.getBusinessNumber());
            params.put("isLowCodeFlow", process.getIsLowCodeFlow());
            params.put("isOutSideAccessProc", false);
            BusinessDataVo vo = formFactory.dataFormConversion(params.toJSONString(), formCode);
            vo.setBusinessId(process.getBusinessId());
            FormOperationAdaptor adaptor = formFactory.getFormAdaptor(vo);
            adaptor.queryData(vo);

            if (Objects.equals(vo.getIsLowCodeFlow(), 1)) {
                if (vo.getLfFields() != null) {
                    values.putAll(vo.getLfFields());
                }
                Map<String, Map<String, Object>> multi = readLfFieldsMulti(vo);
                if (multi != null) {
                    for (Map<String, Object> inner : multi.values()) {
                        if (inner != null) {
                            values.putAll(inner);
                        }
                    }
                }
            } else {
                Class<?> c = vo.getClass();
                while (c != null && c != BusinessDataVo.class) {
                    for (Field f : c.getDeclaredFields()) {
                        int mod = f.getModifiers();
                        if (Modifier.isStatic(mod) || Modifier.isTransient(mod) || f.isSynthetic()) {
                            continue;
                        }
                        f.setAccessible(true);
                        values.put(f.getName(), f.get(vo));
                    }
                    c = c.getSuperclass();
                }
            }
        } catch (Exception e) {
            log.warn("load form values failed, processNumber={}", process.getBusinessNumber(), e);
        }
        return values;
    }

    /** 反射读取 vo.lfFieldsMulti (UDLFApplyVo 字段), 不存在返回 null。 */
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> readLfFieldsMulti(BusinessDataVo vo) {
        try {
            Field f = vo.getClass().getDeclaredField("lfFieldsMulti");
            f.setAccessible(true);
            return (Map<String, Map<String, Object>>) f.get(vo);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================================================================================
    // helpers: design tree
    // ==================================================================================

    /** 从发起节点先序遍历, 建立节点主键 id → 全序 index (分支按优先级序在前, 主干在后)。 */
    private Map<Long, Integer> buildPreOrderIndex(List<BpmnNodeVo> nodes) {
        Map<Long, Integer> order = new HashMap<>();
        Map<String, List<BpmnNodeVo>> childrenByFrom = new HashMap<>();
        for (BpmnNodeVo n : nodes) {
            if (StringUtils.isNotEmpty(n.getNodeFrom())) {
                childrenByFrom.computeIfAbsent(n.getNodeFrom(), k -> new ArrayList<>()).add(n);
            }
        }
        // 分支头(type3)按优先级排前, 其余(主干/并行)排后
        for (List<BpmnNodeVo> children : childrenByFrom.values()) {
            children.sort(Comparator
                    .comparing((BpmnNodeVo n) -> n.getNodeType() != null
                            && n.getNodeType() == NODE_TYPE_CONDITION_BRANCH ? 0 : 1)
                    .thenComparing(n -> n.getProperty() != null && n.getProperty().getSort() != null
                            ? n.getProperty().getSort() : Integer.MAX_VALUE));
        }
        BpmnNodeVo start = nodes.stream()
                .filter(n -> n.getNodeType() != null && n.getNodeType() == NODE_TYPE_START)
                .findFirst().orElse(null);
        int[] counter = {0};
        if (start != null) {
            dfsOrder(start, childrenByFrom, order, counter);
        }
        return order;
    }

    private void dfsOrder(BpmnNodeVo node, Map<String, List<BpmnNodeVo>> childrenByFrom,
                          Map<Long, Integer> order, int[] counter) {
        if (node.getId() == null || order.containsKey(node.getId())) {
            return;
        }
        order.put(node.getId(), counter[0]++);
        for (BpmnNodeVo child : childrenByFrom.getOrDefault(node.getNodeId(), Collections.emptyList())) {
            dfsOrder(child, childrenByFrom, order, counter);
        }
    }

    /** 沿 nodeFrom 向上找最近的"真实"前驱节点(跳过条件分支头与网关)。 */
    private BpmnNodeVo findPrevRealNode(BpmnNodeVo target, Map<String, BpmnNodeVo> byUuid) {
        if (target == null || StringUtils.isEmpty(target.getNodeFrom())) {
            return null;
        }
        BpmnNodeVo p = byUuid.get(target.getNodeFrom());
        int guard = 0;
        while (p != null && guard++ < 100) {
            if (p.getNodeType() != null
                    && (p.getNodeType() == NODE_TYPE_CONDITION_BRANCH || p.getNodeType() == NODE_TYPE_GATEWAY)) {
                p = StringUtils.isEmpty(p.getNodeFrom()) ? null : byUuid.get(p.getNodeFrom());
            } else {
                return p;
            }
        }
        return null;
    }

    /** 沿 nodeFrom 向上找目标节点所在的分支头(type3); 不在条件分支上返回 null。 */
    private BpmnNodeVo findAncestorBranchHead(BpmnNodeVo target, Map<String, BpmnNodeVo> byUuid) {
        BpmnNodeVo q = target;
        int guard = 0;
        while (q != null && guard++ < 100) {
            if (q.getNodeType() != null && q.getNodeType() == NODE_TYPE_CONDITION_BRANCH) {
                return q;
            }
            q = StringUtils.isEmpty(q.getNodeFrom()) ? null : byUuid.get(q.getNodeFrom());
        }
        return null;
    }

    /**
     * 目标节点所在网关的全分支横评: 每分支 条件/当前实际值/求值结果,
     * 求值复用引擎同款 {@link AutoConditionEvaluator}。
     */
    private List<NodeDiagnosisVo.BranchEvaluation> evaluateBranchFamily(BpmnNodeVo branchHead,
                                                                        Long targetId,
                                                                        List<BpmnNodeVo> nodes,
                                                                        Map<String, Object> formValues) {
        // 兄弟分支: 同一 nodeFrom 下的所有 type3 节点
        List<BpmnNodeVo> siblings = nodes.stream()
                .filter(n -> n.getNodeType() != null
                        && n.getNodeType() == NODE_TYPE_CONDITION_BRANCH
                        && Objects.equals(n.getNodeFrom(), branchHead.getNodeFrom()))
                .sorted(Comparator.comparing(n -> n.getProperty() != null && n.getProperty().getSort() != null
                        ? n.getProperty().getSort() : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        Map<String, List<BpmnNodeVo>> childrenByFrom = new HashMap<>();
        for (BpmnNodeVo n : nodes) {
            if (StringUtils.isNotEmpty(n.getNodeFrom())) {
                childrenByFrom.computeIfAbsent(n.getNodeFrom(), k -> new ArrayList<>()).add(n);
            }
        }

        List<NodeDiagnosisVo.BranchEvaluation> result = new ArrayList<>();
        for (BpmnNodeVo branch : siblings) {
            Set<Long> subtreeIds = new HashSet<>();
            collectSubtreeIds(branch, childrenByFrom, subtreeIds);

            boolean isDefault = branch.getProperty() != null
                    && Objects.equals(branch.getProperty().getIsDefault(), 1);
            List<List<BpmnNodeConditionsConfVueVo>> conditionList = branch.getProperty() == null
                    ? null : branch.getProperty().getConditionList();
            Boolean groupRelation = branch.getProperty() == null
                    ? Boolean.FALSE : branch.getProperty().getGroupRelation();

            List<NodeDiagnosisVo.ConditionItemResult> items = new ArrayList<>();
            Boolean hit = null;
            if (!isDefault && !CollectionUtils.isEmpty(conditionList)) {
                hit = AutoConditionEvaluator.evaluate(conditionList, groupRelation, formValues);
                for (List<BpmnNodeConditionsConfVueVo> group : conditionList) {
                    for (BpmnNodeConditionsConfVueVo cond : group) {
                        Boolean single = AutoConditionEvaluator.evaluate(
                                Collections.singletonList(Collections.singletonList(cond)), false, formValues);
                        items.add(toConditionItemResult(cond, formValues, single));
                    }
                }
            }

            result.add(NodeDiagnosisVo.BranchEvaluation.builder()
                    .branchName(branch.getNodeName())
                    .priority(branch.getProperty() != null ? branch.getProperty().getSort() : null)
                    .isDefault(isDefault)
                    .hit(hit)
                    .containsTarget(subtreeIds.contains(targetId))
                    .conditions(items)
                    .build());
        }
        return result;
    }

    private NodeDiagnosisVo.ConditionItemResult toConditionItemResult(BpmnNodeConditionsConfVueVo cond,
                                                                      Map<String, Object> formValues,
                                                                      Boolean pass) {
        Object actual = cond.getColumnDbname() != null ? formValues.get(cond.getColumnDbname()) : null;
        String expect = StringUtils.isEmpty(cond.getZdy1()) ? "" : cond.getZdy1();
        if (!StringUtils.isEmpty(cond.getZdy2())) {
            expect = expect + (StringUtils.isEmpty(cond.getOpt2()) ? "~" : cond.getOpt2()) + cond.getZdy2();
        }
        return NodeDiagnosisVo.ConditionItemResult.builder()
                .label(cond.getShowName())
                .fieldName(cond.getColumnDbname())
                .fieldTypeName(cond.getFieldTypeName())
                .opText(opText(cond))
                .expectText(expect)
                .actualValue(actual == null ? "" : String.valueOf(actual))
                .pass(pass)
                .build();
    }

    private String opText(BpmnNodeConditionsConfVueVo cond) {
        Integer optType = cond.getOptType();
        if (optType == null) {
            return "=";
        }
        switch (optType) {
            case 1: return ">=";
            case 2: return ">";
            case 3: return "<=";
            case 4: return "<";
            case 5: return "==";
            case 6:
            case 7:
            case 8:
            case 9: return "介于";
            default: return "=";
        }
    }

    private void collectSubtreeIds(BpmnNodeVo node, Map<String, List<BpmnNodeVo>> childrenByFrom, Set<Long> acc) {
        if (node == null || node.getId() == null || !acc.add(node.getId())) {
            return;
        }
        for (BpmnNodeVo child : childrenByFrom.getOrDefault(node.getNodeId(), Collections.emptyList())) {
            collectSubtreeIds(child, childrenByFrom, acc);
        }
    }

    // ==================================================================================
    // helpers: records
    // ==================================================================================

    /** 该节点相关的加减签/委托记录 (bpm_flowrun_entrust, 自带 node_id)。 */
    private List<NodeDiagnosisVo.EntrustRecordVo> loadEntrustRecords(String procInstId, Long nodeId) {
        List<BpmFlowrunEntrust> records = bpmFlowrunEntrustService.list(
                new QueryWrapper<BpmFlowrunEntrust>()
                        .eq("runinfoid", procInstId)
                        .eq("node_id", nodeId)
                        .orderByDesc("id"));
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(r -> NodeDiagnosisVo.EntrustRecordVo.builder()
                        .actionType(r.getActionType())
                        .actionTypeName(actionTypeName(r.getActionType()))
                        .originalId(r.getOriginal())
                        .originalName(r.getOriginalName())
                        .actualId(r.getActual())
                        .actualName(r.getActualName())
                        .nodeId(r.getNodeId())
                        .build())
                .collect(Collectors.toList());
    }

    private String actionTypeName(Integer actionType) {
        if (actionType == null) {
            return "未知";
        }
        switch (actionType) {
            case 0:
            case 1: return "转办";
            case 2: return "加签";
            case 3: return "减签";
            case 4: return "表单关联刷新";
            default: return "未知(" + actionType + ")";
        }
    }

    /** 加批记录: bpm_verify_info(verifyStatus=9) + variableConfigJson.signUps。 */
    private List<NodeDiagnosisVo.SignupRecordVo> loadSignupRecords(String processNumber, Long nodeId) {
        List<NodeDiagnosisVo.SignupRecordVo> result = new ArrayList<>();
        try {
            List<BpmVerifyInfo> verifies = bpmVerifyInfoMapper.selectList(
                    new QueryWrapper<BpmVerifyInfo>()
                            .eq("process_code", processNumber)
                            .eq("verify_status", VERIFY_STATUS_ADD_APPROVAL));
            for (BpmVerifyInfo v : verifies) {
                result.add(NodeDiagnosisVo.SignupRecordVo.builder()
                        .userName(v.getVerifyUserName())
                        .verifyDate(v.getVerifyDate())
                        .verifyDesc(v.getVerifyDesc())
                        .source("verify_info")
                        .build());
            }
        } catch (Exception e) {
            log.warn("query signup verify info failed, processNumber={}", processNumber, e);
        }
        try {
            org.openoa.base.entity.BpmVariable variable = bpmVariableService.getBaseMapper().selectOne(
                    new QueryWrapper<org.openoa.base.entity.BpmVariable>()
                            .eq("process_num", processNumber)
                            .eq("is_del", 0));
            if (variable != null && StringUtils.isNotEmpty(variable.getVariableConfigJson())) {
                // SignUpItem 无全量 getter, 用 JSONObject 解析 signUps
                JSONObject config = JSON.parseObject(variable.getVariableConfigJson());
                com.alibaba.fastjson2.JSONArray signUps = config == null ? null : config.getJSONArray("signUps");
                if (signUps != null) {
                    for (int i = 0; i < signUps.size(); i++) {
                        JSONObject signUp = signUps.getJSONObject(i);
                        if (signUp != null && Objects.equals(String.valueOf(nodeId), signUp.getString("nodeId"))) {
                            String names = extractSignUpNames(signUp);
                            result.add(NodeDiagnosisVo.SignupRecordVo.builder()
                                    .userName(names)
                                    .source("sign_up_config")
                                    .build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("query signUps config failed, processNumber={}", processNumber, e);
        }
        return result;
    }

    /** 从 signUp.potentialElement 提取加批人姓名, 逗号拼接 */
    private String extractSignUpNames(JSONObject signUp) {
        try {
            JSONObject personnel = signUp.getJSONObject("personnelByElement");
            if (personnel == null) {
                return null;
            }
            Set<String> names = new LinkedHashSet<>();
            for (Object v : personnel.values()) {
                com.alibaba.fastjson2.JSONArray arr = v instanceof com.alibaba.fastjson2.JSONArray
                        ? (com.alibaba.fastjson2.JSONArray) v : null;
                if (arr == null) {
                    continue;
                }
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject p = arr.getJSONObject(i);
                    if (p != null) {
                        String n = p.getString("assigneeName");
                        if (StringUtils.isEmpty(n)) {
                            n = p.getString("assignee");
                        }
                        if (StringUtils.isNotEmpty(n)) {
                            names.add(n);
                        }
                    }
                }
            }
            return names.isEmpty() ? null : String.join(",", names);
        } catch (Exception e) {
            return null;
        }
    }

    /** 兜底原始 task 记录: 该节点相关的 hi + ru 任务。 */
    private List<NodeDiagnosisVo.RawTaskVo> loadRawTasks(List<ActHiTaskinst> hiTasks,
                                                         List<BpmVerifyInfoVo> ruTasks,
                                                         Long nodeId) {
        List<NodeDiagnosisVo.RawTaskVo> result = new ArrayList<>();
        String idStr = String.valueOf(nodeId);
        for (ActHiTaskinst t : hiTasks) {
            if (Objects.equals(idStr, t.getNodeId())) {
                result.add(NodeDiagnosisVo.RawTaskVo.builder()
                        .taskId(t.getId())
                        .taskName(t.getName())
                        .assigneeName(t.getAssigneeName())
                        .startTime(t.getStartTime())
                        .endTime(t.getEndTime())
                        .deleteReason(t.getDeleteReason())
                        .nodeId(t.getNodeId())
                        .source("hi")
                        .build());
            }
        }
        for (BpmVerifyInfoVo t : ruTasks) {
            if (Objects.equals(idStr, t.getNodeId())) {
                result.add(NodeDiagnosisVo.RawTaskVo.builder()
                        .taskId(t.getId())
                        .taskName(t.getTaskName())
                        .assigneeName(t.getVerifyUserName())
                        .nodeId(t.getNodeId())
                        .source("ru")
                        .build());
            }
        }
        return result;
    }

    // ==================================================================================
    // helpers: 人员维度诊断 (4.3)
    // ==================================================================================

    /** 配置规则描述: nodeProperty 汉字。 */
    private String ruleDescOf(BpmnNodeVo node) {
        if (node == null) {
            return null;
        }
        if (node.getNodeProperty() != null) {
            String desc = NodePropertyEnum.getDescByCode(node.getNodeProperty());
            if (StringUtils.isNotEmpty(desc)) {
                return desc;
            }
        }
        return node.getNodePropertyName();
    }

    /**
     * 应审人: 复用 preview 链路(引擎同源规则评估)后, 镜像 reTreatNodeAssignee 语义
     * 把该节点的加减签/委托记录应用到 emplList(name 后缀 +加签 / -减签 / *转办)。
     * 评估失败/节点未命中分支 → 空列表(前端按"未评估"展示)。
     */
    private List<NodeDiagnosisVo.ApproverVo> evaluateExpectedApprovers(BpmBusinessProcess process,
                                                                       BpmnConfVo confVo,
                                                                       Long targetId) {
        try {
            Map<String, Object> formValues = loadFormValues(process, confVo != null ? confVo.getFormCode() : null);
            JSONObject params = new JSONObject();
            params.put("isStartPreview", false);
            params.put("formCode", confVo != null ? confVo.getFormCode() : null);
            params.put("isLowCodeFlow", Objects.equals(process.getIsLowCodeFlow(), 1));
            params.put("isOutSideAccessProc", false);
            params.put("bpmnCode", process.getVersion());
            params.put("startUserId", process.getCreateUser());
            if (Objects.equals(process.getIsLowCodeFlow(), 1)) {
                params.put("lfFields", formValues);
                params.put("lfConditions", new JSONObject());
            } else {
                params.put("lfFields", new JSONObject());
                JSONObject conds = new JSONObject();
                for (String f : collectConditionFieldNames(confVo)) {
                    if (formValues.containsKey(f)) {
                        conds.put(f, formValues.get(f));
                    }
                }
                params.put("lfConditions", conds);
            }

            PreviewNode previewNode = bpmnConfBizService.taskPagePreviewNode(params.toJSONString());
            List<BpmnNodeVo> nodeList = previewNode == null ? null : previewNode.getBpmnNodeList();
            if (CollectionUtils.isEmpty(nodeList)) {
                return Collections.emptyList();
            }
            BpmnNodeVo node = nodeList.stream()
                    .filter(n -> n.getId() != null && n.getId().equals(targetId))
                    .findFirst().orElse(null);
            if (node == null || node.getProperty() == null
                    || CollectionUtils.isEmpty(node.getProperty().getEmplList())) {
                return Collections.emptyList();
            }

            List<NodeDiagnosisVo.ApproverVo> result = new ArrayList<>();
            for (BaseIdTranStruVo emp : node.getProperty().getEmplList()) {
                result.add(NodeDiagnosisVo.ApproverVo.builder()
                        .userId(emp.getId())
                        .name(emp.getName())
                        .source("config")
                        .build());
            }

            List<BpmFlowrunEntrust> entrusts = bpmFlowrunEntrustService.list(
                    new QueryWrapper<BpmFlowrunEntrust>()
                            .eq("runinfoid", process.getProcInstId())
                            .eq("node_id", targetId));
            if (!CollectionUtils.isEmpty(entrusts)) {
                for (BpmFlowrunEntrust r : entrusts) {
                    if (r.getActionType() == null) {
                        continue;
                    }
                    if (r.getActionType() == 0 || r.getActionType() == 1) {
                        // 转办: original → actual, 标记 *
                        for (NodeDiagnosisVo.ApproverVo a : result) {
                            if (Objects.equals(a.getUserId(), r.getOriginal())) {
                                a.setUserId(r.getActual());
                                a.setName((StringUtils.isNotEmpty(r.getActualName()) ? r.getActualName() : r.getActual()) + "*");
                                a.setMark("*");
                            }
                        }
                    } else if (r.getActionType() == 2) {
                        // 加签: 追加, 标记 +
                        result.add(NodeDiagnosisVo.ApproverVo.builder()
                                .userId(r.getActual())
                                .name((StringUtils.isNotEmpty(r.getActualName()) ? r.getActualName() : r.getActual()) + "+")
                                .mark("+")
                                .source("addSign")
                                .build());
                    } else if (r.getActionType() == 3) {
                        // 减签: 原列表标记 -
                        for (NodeDiagnosisVo.ApproverVo a : result) {
                            if (Objects.equals(a.getUserId(), r.getActual())) {
                                a.setName(a.getName() + "-");
                                a.setMark("-");
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("evaluate expected approvers failed, processNumber={}, nodeId={}",
                    process.getBusinessNumber(), targetId, e);
            return Collections.emptyList();
        }
    }

    /** 收集设计树上所有条件分支字段名(type==2, columnDbname), DIY 模式 lfConditions 用。 */
    private Set<String> collectConditionFieldNames(BpmnConfVo confVo) {
        Set<String> names = new LinkedHashSet<>();
        if (confVo == null || CollectionUtils.isEmpty(confVo.getNodes())) {
            return names;
        }
        for (BpmnNodeVo n : confVo.getNodes()) {
            if (n.getNodeType() == null || n.getNodeType() != NODE_TYPE_CONDITION_BRANCH
                    || n.getProperty() == null) {
                continue;
            }
            List<List<BpmnNodeConditionsConfVueVo>> conds = n.getProperty().getConditionList();
            if (conds == null) {
                continue;
            }
            for (List<BpmnNodeConditionsConfVueVo> g : conds) {
                if (g == null) {
                    continue;
                }
                for (BpmnNodeConditionsConfVueVo c : g) {
                    if (c.getType() != null && c.getType() == 2 && StringUtils.isNotEmpty(c.getColumnDbname())) {
                        names.add(c.getColumnDbname());
                    }
                }
            }
        }
        return names;
    }

    /** 实际审批人: 该节点 task assignee (hi ∪ ru), 按 userId 去重。 */
    private List<NodeDiagnosisVo.ApproverVo> loadActualApprovers(List<ActHiTaskinst> hiTasks,
                                                                 List<BpmVerifyInfoVo> ruTasks,
                                                                 Long targetId) {
        Map<String, NodeDiagnosisVo.ApproverVo> map = new LinkedHashMap<>();
        String idStr = String.valueOf(targetId);
        for (ActHiTaskinst t : hiTasks) {
            if (Objects.equals(idStr, t.getNodeId()) && StringUtils.isNotEmpty(t.getAssignee())) {
                map.putIfAbsent(t.getAssignee(), NodeDiagnosisVo.ApproverVo.builder()
                        .userId(t.getAssignee())
                        .name(t.getAssigneeName())
                        .source("hi")
                        .time(t.getEndTime() != null ? String.valueOf(t.getEndTime()) : null)
                        .build());
            }
        }
        for (BpmVerifyInfoVo t : ruTasks) {
            if (Objects.equals(idStr, t.getNodeId()) && StringUtils.isNotEmpty(t.getVerifyUserId())) {
                map.putIfAbsent(t.getVerifyUserId(), NodeDiagnosisVo.ApproverVo.builder()
                        .userId(t.getVerifyUserId())
                        .name(t.getVerifyUserName())
                        .source("ru")
                        .build());
            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 人员维度结论: 前提=节点存在。判定 该人是否实际审批 + 与预期对账 + 归因。
     * 归因证据优先级: 加签引入 > 转办引入 > 配置命中 > 应审人命中 > 推断(动态评估差异);
     * 不存在侧: 减签移除 > 转办给他人 > 配置不含 > 推断。
     */
    private NodeDiagnosisVo.PersonDiagnosis buildPersonDiagnosis(NodeDiagnosisRequestVo request,
                                                                 List<NodeDiagnosisVo.ApproverVo> expected,
                                                                 List<NodeDiagnosisVo.ApproverVo> actual,
                                                                 List<NodeDiagnosisVo.EntrustRecordVo> entrusts,
                                                                 String ruleDesc,
                                                                 String nodeName) {
        String personId = request.getPersonId();
        boolean presentPerson = actual.stream().anyMatch(a -> Objects.equals(a.getUserId(), personId));
        boolean mismatch = request.getExpectedPersonPresent() != null
                && !request.getExpectedPersonPresent().equals(presentPerson);
        String personName = resolvePersonName(personId, expected, actual, entrusts);

        boolean fromAddSign = entrusts.stream().anyMatch(r -> r.getActionType() != null
                && r.getActionType() == 2 && Objects.equals(r.getActualId(), personId));
        boolean fromRemoveSign = entrusts.stream().anyMatch(r -> r.getActionType() != null
                && r.getActionType() == 3 && Objects.equals(r.getActualId(), personId));
        boolean fromDelegateOut = entrusts.stream().anyMatch(r -> r.getActionType() != null
                && (r.getActionType() == 0 || r.getActionType() == 1) && Objects.equals(r.getOriginalId(), personId));
        boolean fromDelegateIn = entrusts.stream().anyMatch(r -> r.getActionType() != null
                && (r.getActionType() == 0 || r.getActionType() == 1) && Objects.equals(r.getActualId(), personId));
        boolean inConfig = expected.stream().anyMatch(a -> Objects.equals(a.getUserId(), personId)
                && StringUtils.isEmpty(a.getMark()));
        boolean inExpected = expected.stream().anyMatch(a -> Objects.equals(a.getUserId(), personId));

        String reason;
        boolean inference = false;
        if (presentPerson) {
            if (fromAddSign) {
                reason = "由加签引入: 该节点存在加签记录, 此人被加签为审批人";
            } else if (fromDelegateIn) {
                reason = "由转办引入: 原审批人将该节点转办给此人(代审)";
            } else if (inConfig) {
                reason = "配置规则命中: 节点规则「" + safeRule(ruleDesc) + "」评估的应审人包含此人";
            } else if (inExpected) {
                reason = "应审人评估命中(含运行期人员调整)";
            } else {
                inference = true;
                reason = "实际审批出现此人, 但配置规则与应审人评估均未直接命中, 可能为动态评估差异(如角色/表单相关人在审批时变更)";
            }
        } else {
            if (fromRemoveSign) {
                reason = "被减签移除: 该节点存在减签记录, 此人被移出审批人";
            } else if (fromDelegateOut) {
                reason = "转办给他人: 此人将该节点转办给其他审批人";
            } else if (!inConfig) {
                reason = "配置规则不含此人: 节点规则「" + safeRule(ruleDesc) + "」评估的应审人中无此人";
            } else {
                inference = true;
                reason = "配置规则含此人但实际未审批, 可能为动态评估差异(如角色成员/表单相关人在审批时变更)";
            }
        }

        String head = presentPerson ? "该节点实际有此审批人" : "该节点实际无此审批人";
        String msg = head + (mismatch ? " (与你的预期相反)" : " (与你的预期一致)") + "。原因: " + reason;
        return NodeDiagnosisVo.PersonDiagnosis.builder()
                .personId(personId)
                .personName(personName)
                .presentPerson(presentPerson)
                .expectationMismatch(mismatch)
                .message(msg)
                .inference(inference)
                .inferenceNote(inference ? reason : null)
                .build();
    }

    private String safeRule(String ruleDesc) {
        return StringUtils.isEmpty(ruleDesc) ? "未知" : ruleDesc;
    }

    private String resolvePersonName(String personId,
                                     List<NodeDiagnosisVo.ApproverVo> expected,
                                     List<NodeDiagnosisVo.ApproverVo> actual,
                                     List<NodeDiagnosisVo.EntrustRecordVo> entrusts) {
        for (NodeDiagnosisVo.ApproverVo a : actual) {
            if (Objects.equals(a.getUserId(), personId) && StringUtils.isNotEmpty(a.getName())) {
                return a.getName();
            }
        }
        for (NodeDiagnosisVo.ApproverVo a : expected) {
            if (Objects.equals(a.getUserId(), personId) && StringUtils.isNotEmpty(a.getName())) {
                String n = a.getName();
                if (n.endsWith("+") || n.endsWith("-") || n.endsWith("*")) {
                    n = n.substring(0, n.length() - 1);
                }
                return n;
            }
        }
        for (NodeDiagnosisVo.EntrustRecordVo r : entrusts) {
            if (Objects.equals(r.getActualId(), personId) && StringUtils.isNotEmpty(r.getActualName())) {
                return r.getActualName();
            }
            if (Objects.equals(r.getOriginalId(), personId) && StringUtils.isNotEmpty(r.getOriginalName())) {
                return r.getOriginalName();
            }
        }
        try {
            Map<String, String> map = employeeInfoProvider.provideEmployeeInfo(
                    Collections.singletonList(personId));
            return map != null ? map.get(personId) : personId;
        } catch (Exception e) {
            return personId;
        }
    }
}
