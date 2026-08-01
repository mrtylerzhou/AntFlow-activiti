package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.InstanceEfficiencyAssigneeVo;
import org.openoa.base.vo.InstanceEfficiencyDetailVo;
import org.openoa.base.vo.InstanceEfficiencyNodeVo;
import org.openoa.base.vo.InstanceEfficiencySummaryVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程实例效能 Service(实时计算,不入库)
 *
 * <p>口径参见 .scratch/process-instance-efficiency-design.md:
 * <ul>
 *   <li>个人耗时:已完成取 af_hi_taskinst.duration;未完成取 now - start_time</li>
 *   <li>节点耗时:每轮 max(end_time) - min(start_time),退回多轮累加</li>
 *   <li>退回轮次:按 execution_id 分组</li>
 *   <li>节点详情:只返回最后一轮(execution_id 最新)人员</li>
 *   <li>TOP3:进行中节点不参与</li>
 * </ul>
 */
@Slf4j
@Service
public class ProcessInstanceEfficiencyServiceImpl {

    @Autowired
    private ActHiTaskinstServiceImpl actHiTaskinstService;

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Autowired
    private AfUserService afUserService;

    // ==================== 1. 顶部汇总 ====================

    public InstanceEfficiencySummaryVo getSummary(String processNumber) {
        BpmBusinessProcess process = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (process == null) {
            return null;
        }

        Integer processState = process.getProcessState();
        boolean finished = !Objects.equals(processState, ProcessStateEnum.HANDLING_STATE.getCode());
        Date createTime = process.getCreateTime();
        Date now = new Date();

        Long totalDuration;
        if (createTime == null) {
            totalDuration = 0L;
        } else if (finished) {
            // 已完成:取所有 task 中最晚的 end_time
            Date maxEnd = getMaxEndTime(process.getProcInstId());
            totalDuration = maxEnd != null ? maxEnd.getTime() - createTime.getTime() : now.getTime() - createTime.getTime();
        } else {
            // 进行中:now - createTime
            totalDuration = now.getTime() - createTime.getTime();
        }

        InstanceEfficiencySummaryVo vo = new InstanceEfficiencySummaryVo();
        vo.setProcessNumber(processNumber);
        vo.setProcessState(processState);
        vo.setProcessStateName(getProcessStateName(processState));
        vo.setCreateTime(createTime);
        vo.setTotalDuration(totalDuration);
        vo.setTotalDurationText(formatDuration(totalDuration));
        vo.setFinished(finished);
        return vo;
    }

    // ==================== 2. 节点列表 ====================

    public List<InstanceEfficiencyNodeVo> listNodes(String processNumber) {
        BpmBusinessProcess process = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (process == null || !StringUtils.hasText(process.getProcInstId())) {
            return Collections.emptyList();
        }

        List<ActHiTaskinst> tasks = actHiTaskinstService.queryRecordsByProcInstId(process.getProcInstId());
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }

        Integer processState = process.getProcessState();
        boolean processFinished = !Objects.equals(processState, ProcessStateEnum.HANDLING_STATE.getCode());
        Date now = new Date();

        // 按 taskDefKey 分组
        Map<String, List<ActHiTaskinst>> nodeGroup = tasks.stream()
                .filter(t -> StringUtils.hasText(t.getTaskDefKey()))
                .collect(Collectors.groupingBy(ActHiTaskinst::getTaskDefKey, LinkedHashMap::new, Collectors.toList()));

        List<InstanceEfficiencyNodeVo> nodes = new ArrayList<>();
        for (Map.Entry<String, List<ActHiTaskinst>> entry : nodeGroup.entrySet()) {
            String taskDefKey = entry.getKey();
            List<ActHiTaskinst> nodeTasks = entry.getValue();

            // 按 execution_id 分轮次
            Map<String, List<ActHiTaskinst>> rounds = nodeTasks.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getExecutionId() != null ? t.getExecutionId() : "null",
                            LinkedHashMap::new,
                            Collectors.toList()));

            boolean hasRollback = rounds.size() > 1;

            // 每轮算 max(end)-min(start),累加
            long totalDuration = 0L;
            boolean inProgress = false;
            for (List<ActHiTaskinst> roundTasks : rounds.values()) {
                Date minStart = roundTasks.stream()
                        .map(ActHiTaskinst::getStartTime)
                        .filter(Objects::nonNull)
                        .min(Date::compareTo).orElse(null);
                boolean hasUnfinished = roundTasks.stream().anyMatch(t -> t.getEndTime() == null);
                Date maxEnd = hasUnfinished ? null : roundTasks.stream()
                        .map(ActHiTaskinst::getEndTime)
                        .filter(Objects::nonNull)
                        .max(Date::compareTo).orElse(null);

                if (minStart != null) {
                    if (maxEnd != null) {
                        totalDuration += maxEnd.getTime() - minStart.getTime();
                    } else {
                        // 这一轮有未完成任务
                        totalDuration += now.getTime() - minStart.getTime();
                        inProgress = true;
                    }
                }
            }

            // 进行中判定:流程未结束且该节点有未完成 task
            if (!processFinished) {
                boolean anyUnfinished = nodeTasks.stream().anyMatch(t -> t.getEndTime() == null);
                if (anyUnfinished) {
                    inProgress = true;
                }
            }

            InstanceEfficiencyNodeVo vo = new InstanceEfficiencyNodeVo();
            vo.setTaskDefKey(taskDefKey);
            vo.setNodeName(resolveNodeName(processNumber, taskDefKey, nodeTasks.get(0).getName()));
            vo.setDuration(totalDuration);
            vo.setDurationText(formatDuration(totalDuration));
            vo.setHasRollback(hasRollback);
            vo.setInProgress(inProgress);

            // 填充 nodeType
            BpmnNode nodeInfo = resolveBpmnNode(processNumber, taskDefKey);
            if (nodeInfo != null) {
                vo.setNodeType(nodeInfo.getNodeType());
                vo.setNodeTypeName(getNodeTypeName(nodeInfo.getNodeType()));
            }

            nodes.add(vo);
        }

        // 按各节点 min(start_time) 升序排列
        nodes.sort(Comparator.comparing(n -> {
            List<ActHiTaskinst> nt = nodeGroup.get(n.getTaskDefKey());
            return nt.stream()
                    .map(ActHiTaskinst::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Date::compareTo).orElse(new Date(0));
        }));

        // 编号
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setOrderNo(i + 1);
        }

        // TOP3(进行中节点不参与)
        List<InstanceEfficiencyNodeVo> rankable = nodes.stream()
                .filter(n -> !Boolean.TRUE.equals(n.getInProgress()))
                .sorted(Comparator.comparing(InstanceEfficiencyNodeVo::getDuration).reversed())
                .limit(3)
                .collect(Collectors.toList());
        for (int i = 0; i < rankable.size(); i++) {
            rankable.get(i).setTopRank(i + 1);
        }

        return nodes;
    }

    // ==================== 3. 节点详情 ====================

    public InstanceEfficiencyDetailVo getNodeDetail(String processNumber, String taskDefKey) {
        BpmBusinessProcess process = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (process == null || !StringUtils.hasText(process.getProcInstId())) {
            return null;
        }

        List<ActHiTaskinst> tasks = actHiTaskinstService.queryRecordsByProcInstId(process.getProcInstId());
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }

        // 过滤出该节点的 task
        List<ActHiTaskinst> nodeTasks = tasks.stream()
                .filter(t -> taskDefKey.equals(t.getTaskDefKey()))
                .collect(Collectors.toList());
        if (nodeTasks.isEmpty()) {
            return null;
        }

        // 按 execution_id 分轮次,取最后一轮(execution_id 排序最新)
        Map<String, List<ActHiTaskinst>> rounds = nodeTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getExecutionId() != null ? t.getExecutionId() : "null",
                        LinkedHashMap::new,
                        Collectors.toList()));

        boolean hasRollback = rounds.size() > 1;

        // 最后一轮:按 execution_id 字符串排序取最后一个
        String lastExecutionId = rounds.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElse(null);
        List<ActHiTaskinst> lastRoundTasks = rounds.get(lastExecutionId);
        if (lastRoundTasks == null) {
            lastRoundTasks = nodeTasks;
        }

        Date now = new Date();
        List<InstanceEfficiencyAssigneeVo> assignees = new ArrayList<>();
        for (ActHiTaskinst task : lastRoundTasks) {
            InstanceEfficiencyAssigneeVo av = new InstanceEfficiencyAssigneeVo();
            av.setAssignee(task.getAssignee());
            av.setAssigneeName(resolveAssigneeName(task));
            av.setStartTime(task.getStartTime());
            av.setEndTime(task.getEndTime());

            boolean finished = task.getEndTime() != null;
            av.setFinished(finished);
            // 个人耗时:已完成取 duration 字段,未完成取 now - start_time
            if (finished) {
                // 优先用 duration 字段;为空时降级用 end-start
                Long dur = task.getDuration();
                if (dur != null) {
                    av.setDuration(dur);
                } else if (task.getStartTime() != null) {
                    av.setDuration(task.getEndTime().getTime() - task.getStartTime().getTime());
                } else {
                    av.setDuration(0L);
                }
            } else {
                if (task.getStartTime() != null) {
                    av.setDuration(now.getTime() - task.getStartTime().getTime());
                } else {
                    av.setDuration(0L);
                }
            }
            av.setDurationText(formatDuration(av.getDuration()));
            assignees.add(av);
        }

        InstanceEfficiencyDetailVo vo = new InstanceEfficiencyDetailVo();
        vo.setTaskDefKey(taskDefKey);
        vo.setNodeName(resolveNodeName(processNumber, taskDefKey, nodeTasks.get(0).getName()));
        vo.setHasRollback(hasRollback);
        vo.setAssignees(assignees);

        // 签署信息:从 BpmnNode 取 nodeType、nodeProperty、signType
        BpmnNode nodeInfo = resolveBpmnNode(processNumber, taskDefKey);
        if (nodeInfo != null) {
            vo.setNodeType(nodeInfo.getNodeType());
            vo.setNodeTypeName(getNodeTypeName(nodeInfo.getNodeType()));
            vo.setNodeProperty(nodeInfo.getNodeProperty());
            vo.setNodePropertyName(getNodePropertyName(nodeInfo.getNodeProperty()));

            Integer signType = resolveSignType(nodeInfo);
            vo.setSignType(signType);
            vo.setSignTypeName(getSignTypeName(signType));
        }

        return vo;
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取流程所有 task 中最晚的 end_time
     */
    private Date getMaxEndTime(String procInstId) {
        List<ActHiTaskinst> tasks = actHiTaskinstService.queryRecordsByProcInstId(procInstId);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        return tasks.stream()
                .map(ActHiTaskinst::getEndTime)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);
    }

    /**
     * 根据 processNumber + taskDefKey 查 BpmnNode
     */
    private BpmnNode resolveBpmnNode(String processNumber, String taskDefKey) {
        if (!StringUtils.hasText(taskDefKey)) {
            return null;
        }
        try {
            String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(processNumber, taskDefKey);
            if (StringUtils.hasText(nodeId)) {
                return bpmnNodeService.getById(nodeId);
            }
        } catch (Exception e) {
            log.debug("效能:获取节点定义失败,processNumber={},taskDefKey={}", processNumber, taskDefKey);
        }
        return null;
    }

    /**
     * 解析节点名称:优先 t_bpmn_node.node_name,降级用 task.NAME_
     */
    private String resolveNodeName(String processNumber, String taskDefKey, String taskName) {
        BpmnNode node = resolveBpmnNode(processNumber, taskDefKey);
        if (node != null && StringUtils.hasText(node.getNodeName())) {
            return node.getNodeName();
        }
        return taskName;
    }

    /**
     * 解析审批人姓名:优先取 ASSIGNEE_NAME,为空则调 AfUserService
     */
    private String resolveAssigneeName(ActHiTaskinst task) {
        if (StringUtils.hasText(task.getAssigneeName())) {
            return task.getAssigneeName();
        }
        if (StringUtils.hasText(task.getAssignee())) {
            try {
                List<BaseIdTranStruVo> users = afUserService.queryUserByIds(
                        Collections.singletonList(task.getAssignee()));
                if (!CollectionUtils.isEmpty(users) && users.get(0) != null) {
                    return users.get(0).getName();
                }
            } catch (Exception e) {
                log.debug("效能:获取审批人姓名失败,assignee={}", task.getAssignee());
            }
        }
        return null;
    }

    /**
     * 从 BpmnNode 解析 signType(按 nodeProperty 分支取对应 Conf)
     */
    private Integer resolveSignType(BpmnNode node) {
        if (node == null || !StringUtils.hasText(node.getNodeConfigJson())) {
            return null;
        }
        try {
            BpmnNodeConfigJson config = JsonConfUtil.parseNodeConfig(node.getNodeConfigJson());
            if (config == null || config.getApproverConf() == null) {
                return null;
            }
            BpmnNodeApproverConfJson conf = config.getApproverConf();
            Integer prop = node.getNodeProperty();
            if (prop == null) {
                return null;
            }
            // 按 nodeProperty 取对应 Conf 的 signType
            if (prop == NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode() && conf.getPersonnelConf() != null) {
                return conf.getPersonnelConf().getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_ROLE.getCode() && !CollectionUtils.isEmpty(conf.getRoleConfList())) {
                return conf.getRoleConfList().get(0).getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE.getCode() && conf.getCustomizeConf() != null) {
                return conf.getCustomizeConf().getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_ZDY_RULES.getCode() && !CollectionUtils.isEmpty(conf.getUdrConfList())) {
                return conf.getUdrConfList().get(0).getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_FORM_RELATED.getCode() && !CollectionUtils.isEmpty(conf.getFormRelatedUserConfList())) {
                return conf.getFormRelatedUserConfList().get(0).getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_PREV_NODE_RELATED.getCode() && !CollectionUtils.isEmpty(conf.getPrevNodeRelatedUserConfList())) {
                return conf.getPrevNodeRelatedUserConfList().get(0).getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_OUT_SIDE_ACCESS.getCode() && conf.getOutSideAccessConf() != null) {
                return conf.getOutSideAccessConf().getSignType();
            }
            if (prop == NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE.getCode() && conf.getBusinessTableConf() != null) {
                return conf.getBusinessTableConf().getSignType();
            }
        } catch (Exception e) {
            log.debug("效能:解析 signType 失败,nodeId={}", node.getId());
        }
        return null;
    }

    private String getProcessStateName(Integer state) {
        if (state == null) {
            return "未知";
        }
        for (ProcessStateEnum e : ProcessStateEnum.values()) {
            if (e.getCode().equals(state)) {
                return e.getDesc();
            }
        }
        return "未知";
    }

    private String getNodeTypeName(Integer nodeType) {
        if (nodeType == null) {
            return null;
        }
        for (NodeTypeEnum e : NodeTypeEnum.values()) {
            if (e.getCode().equals(nodeType)) {
                return e.getDesc();
            }
        }
        return null;
    }

    private String getNodePropertyName(Integer nodeProperty) {
        if (nodeProperty == null) {
            return null;
        }
        // 优先用 PersonnelEnum(中文更友好),取不到降级 NodePropertyEnum
        for (PersonnelEnum e : PersonnelEnum.values()) {
            if (e.getNodePropertyEnum() != null
                    && e.getNodePropertyEnum().getCode().equals(nodeProperty)) {
                return e.getDesc();
            }
        }
        for (NodePropertyEnum e : NodePropertyEnum.values()) {
            if (e.getCode().equals(nodeProperty)) {
                return e.getDesc();
            }
        }
        return null;
    }

    private String getSignTypeName(Integer signType) {
        if (signType == null) {
            return null;
        }
        for (SignTypeEnum e : SignTypeEnum.values()) {
            if (e.getCode().equals(signType)) {
                return e.getDesc();
            }
        }
        return null;
    }

    /**
     * 耗时格式化:
     * < 1min → "<1min"
     * ≥1min → "Xm Xs"
     * ≥1h → "Xh Xm"
     * ≥1d → "Xd Xh"
     */
    public static String formatDuration(Long ms) {
        if (ms == null || ms < 0) {
            return "0s";
        }
        long totalSeconds = ms / 1000;
        if (totalSeconds < 60) {
            return "<1min";
        }
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) {
            return days + "d " + hours + "h";
        }
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m " + seconds + "s";
    }
}
