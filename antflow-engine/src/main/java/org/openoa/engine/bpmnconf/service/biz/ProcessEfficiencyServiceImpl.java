package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmProcessEfficiency;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ProcessEfficiencyVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.service.AfUserService;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.mapper.BpmProcessEfficiencyMapper;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程效能统计 Service
 *
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Service
public class ProcessEfficiencyServiceImpl extends ServiceImpl<BpmProcessEfficiencyMapper, BpmProcessEfficiency> {

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

    /**
     * 执行效能统计计算
     *
     * @param formCodes 流程类型编码列表,为空则统计全部
     */
    @Transactional(rollbackFor = Exception.class)
    public void calculateEfficiency(List<String> formCodes) {
        // 1. 查询昨天更新的流程
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayStart = cal.getTime();

        LambdaQueryWrapper<BpmBusinessProcess> processWrapper = AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                .ge(BpmBusinessProcess::getUpdateTime, yesterdayStart)
                .lt(BpmBusinessProcess::getUpdateTime, todayStart);
        if (!CollectionUtils.isEmpty(formCodes)) {
            processWrapper.in(BpmBusinessProcess::getProcessinessKey, formCodes);
        }
        List<BpmBusinessProcess> processes = bpmBusinessProcessService.list(processWrapper);
        if (CollectionUtils.isEmpty(processes)) {
            log.info("效能统计:未查询到昨天更新的流程数据");
            return;
        }

        log.info("效能统计:查询到{}条待处理流程", processes.size());

        for (BpmBusinessProcess process : processes) {
            try {
                processSingleProcess(process);
            } catch (Exception e) {
                log.error("效能统计:处理流程[{}]异常", process.getBusinessNumber(), e);
            }
        }
    }

    /**
     * 处理单个流程的效能统计
     */
    private void processSingleProcess(BpmBusinessProcess process) {
        String processNumber = process.getBusinessNumber();
        Integer processState = process.getProcessState();
        boolean isHandling = Objects.equals(processState, ProcessStateEnum.HANDLING_STATE.getCode());

        // 检查是否已有统计记录
        long existCount = this.count(AFWrappers.<BpmProcessEfficiency>lambdaTenantQuery()
                .eq(BpmProcessEfficiency::getProcessNumber, processNumber));

        if (existCount > 0) {
            if (!isHandling) {
                // 已终结且已统计 -> 跳过
                return;
            }
            // 审批中且已统计 -> 删除旧记录重新计算
            this.remove(AFWrappers.<BpmProcessEfficiency>lambdaTenantQuery()
                    .eq(BpmProcessEfficiency::getProcessNumber, processNumber));
        }

        // 查询该流程的所有历史任务
        String procInstId = process.getProcInstId();
        if (!StringUtils.hasText(procInstId)) {
            log.warn("效能统计:流程[{}]无流程实例ID,跳过", processNumber);
            return;
        }
        List<ActHiTaskinst> tasks = actHiTaskinstService.queryRecordsByProcInstId(procInstId);
        if (CollectionUtils.isEmpty(tasks)) {
            log.warn("效能统计:流程[{}]无历史任务记录,跳过", processNumber);
            return;
        }

        String formCode = process.getProcessinessKey();
        Date processCreateTime = process.getCreateTime();
        String tenantId = MultiTenantUtil.getCurrentTenantId();
        Date now = new Date();
        List<BpmProcessEfficiency> records = new ArrayList<>();

        // === 任务级别 ===
        for (ActHiTaskinst task : tasks) {
            Date startTime = task.getStartTime();
            Date endTime = task.getEndTime();
            long duration;
            if (endTime != null) {
                duration = endTime.getTime() - startTime.getTime();
            } else {
                duration = now.getTime() - startTime.getTime();
            }

            String nodeName = resolveNodeName(processNumber, task.getTaskDefKey(), task.getName());
            String assigneeName = resolveAssigneeName(task);

            records.add(BpmProcessEfficiency.builder()
                    .formCode(formCode)
                    .processNumber(processNumber)
                    .procInstId(procInstId)
                    .executionId(task.getExecutionId())
                    .taskDefKey(task.getTaskDefKey())
                    .nodeName(nodeName)
                    .assignee(task.getAssignee())
                    .assigneeName(assigneeName)
                    .staticType(BpmProcessEfficiency.TYPE_TASK)
                    .startTime(startTime)
                    .endTime(endTime)
                    .duration(duration)
                    .processState(processState)
                    .processCreateTime(processCreateTime)
                    .tenantId(tenantId)
                    .isDel(0)
                    .createTime(now)
                    .updateTime(now)
                    .build());
        }

        // === 节点级别 ===
        Map<String, List<ActHiTaskinst>> nodeGroup = tasks.stream()
                .filter(t -> StringUtils.hasText(t.getTaskDefKey()))
                .collect(Collectors.groupingBy(ActHiTaskinst::getTaskDefKey));

        for (Map.Entry<String, List<ActHiTaskinst>> entry : nodeGroup.entrySet()) {
            String taskDefKey = entry.getKey();
            List<ActHiTaskinst> nodeTasks = entry.getValue();

            Date minStart = nodeTasks.stream()
                    .map(ActHiTaskinst::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Date::compareTo).orElse(null);
            boolean hasUnfinished = nodeTasks.stream().anyMatch(t -> t.getEndTime() == null);
            Date maxEnd = hasUnfinished ? null : nodeTasks.stream()
                    .map(ActHiTaskinst::getEndTime)
                    .filter(Objects::nonNull)
                    .max(Date::compareTo).orElse(null);

            long duration;
            if (minStart == null) {
                duration = 0;
            } else if (maxEnd != null) {
                duration = maxEnd.getTime() - minStart.getTime();
            } else {
                duration = now.getTime() - minStart.getTime();
            }

            String nodeName = resolveNodeName(processNumber, taskDefKey,
                    nodeTasks.get(0).getName());
            String assignees = nodeTasks.stream()
                    .map(ActHiTaskinst::getAssignee)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .collect(Collectors.joining(","));
            String assigneeNames = nodeTasks.stream()
                    .map(this::resolveAssigneeName)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .collect(Collectors.joining(","));

            records.add(BpmProcessEfficiency.builder()
                    .formCode(formCode)
                    .processNumber(processNumber)
                    .procInstId(procInstId)
                    .executionId(nodeTasks.get(0).getExecutionId())
                    .taskDefKey(taskDefKey)
                    .nodeName(nodeName)
                    .assignee(assignees)
                    .assigneeName(assigneeNames)
                    .staticType(BpmProcessEfficiency.TYPE_NODE)
                    .startTime(minStart)
                    .endTime(maxEnd)
                    .duration(duration)
                    .processState(processState)
                    .processCreateTime(processCreateTime)
                    .tenantId(tenantId)
                    .isDel(0)
                    .createTime(now)
                    .updateTime(now)
                    .build());
        }

        // === 流程级别 ===
        boolean processFinished = !isHandling;
        Date processEndTime = null;
        if (processFinished) {
            processEndTime = tasks.stream()
                    .map(ActHiTaskinst::getEndTime)
                    .filter(Objects::nonNull)
                    .max(Date::compareTo).orElse(null);
        }
        long processDuration;
        if (processCreateTime == null) {
            processDuration = 0;
        } else if (processEndTime != null) {
            processDuration = processEndTime.getTime() - processCreateTime.getTime();
        } else {
            processDuration = now.getTime() - processCreateTime.getTime();
        }

        records.add(BpmProcessEfficiency.builder()
                .formCode(formCode)
                .processNumber(processNumber)
                .procInstId(procInstId)
                .executionId(null)
                .taskDefKey(null)
                .nodeName(null)
                .assignee(null)
                .assigneeName(null)
                .staticType(BpmProcessEfficiency.TYPE_PROCESS)
                .startTime(processCreateTime)
                .endTime(processEndTime)
                .duration(processDuration)
                .processState(processState)
                .processCreateTime(processCreateTime)
                .tenantId(tenantId)
                .isDel(0)
                .createTime(now)
                .updateTime(now)
                .build());

        // 批量写入
        this.saveBatch(records);
        log.info("效能统计:流程[{}]统计完成,写入{}条记录", processNumber, records.size());
    }

    /**
     * 解析节点名称:优先从node表取,取不到降级用任务NAME_
     */
    private String resolveNodeName(String processNumber, String taskDefKey, String taskName) {
        if (!StringUtils.hasText(taskDefKey)) {
            return taskName;
        }
        try {
            String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(processNumber, taskDefKey);
            if (StringUtils.hasText(nodeId)) {
                BpmnNode node = bpmnNodeService.getById(nodeId);
                if (node != null && StringUtils.hasText(node.getNodeName())) {
                    return node.getNodeName();
                }
            }
        } catch (Exception e) {
            log.debug("效能统计:获取节点名称失败,processNumber={},taskDefKey={}", processNumber, taskDefKey);
        }
        return taskName;
    }

    /**
     * 解析审批人姓名:优先取ASSIGNEE_NAME_,为空则调AfUserService
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
                log.debug("效能统计:获取审批人姓名失败,assignee={}", task.getAssignee());
            }
        }
        return null;
    }

    // ==================== 查询接口 ====================

    /**
     * 分页查询流程级效能数据
     */
    public ResultAndPage<BpmProcessEfficiency> pageProcessLevel(ProcessEfficiencyVo vo) {
        PageDto pageDto = vo.getPageDto() != null ? vo.getPageDto() : PageDto.first();
        Page<BpmProcessEfficiency> page = new Page<>(pageDto.getPage(), pageDto.getPageSize());

        LambdaQueryWrapper<BpmProcessEfficiency> wrapper = AFWrappers.<BpmProcessEfficiency>lambdaTenantQuery()
                .eq(BpmProcessEfficiency::getStaticType, BpmProcessEfficiency.TYPE_PROCESS);

        if (StringUtils.hasText(vo.getFormCode())) {
            wrapper.eq(BpmProcessEfficiency::getFormCode, vo.getFormCode());
        }
        if (StringUtils.hasText(vo.getProcessNumber())) {
            wrapper.like(BpmProcessEfficiency::getProcessNumber, vo.getProcessNumber());
        }
        if (vo.getProcessState() != null) {
            wrapper.eq(BpmProcessEfficiency::getProcessState, vo.getProcessState());
        }
        if (vo.getStartTimeBegin() != null) {
            wrapper.ge(BpmProcessEfficiency::getStartTime, vo.getStartTimeBegin());
        }
        if (vo.getStartTimeEnd() != null) {
            wrapper.le(BpmProcessEfficiency::getStartTime, vo.getStartTimeEnd());
        }
        if (StringUtils.hasText(vo.getAssignee())) {
            // 流程级不存审批人,需通过子级匹配:先查出匹配的procInstId
            List<BpmProcessEfficiency> matched = this.list(
                    AFWrappers.<BpmProcessEfficiency>lambdaTenantQuery()
                            .eq(BpmProcessEfficiency::getStaticType, BpmProcessEfficiency.TYPE_TASK)
                            .and(w -> w.like(BpmProcessEfficiency::getAssigneeName, vo.getAssignee())
                                    .or().like(BpmProcessEfficiency::getAssignee, vo.getAssignee())));
            if (CollectionUtils.isEmpty(matched)) {
                return PageUtils.getResultAndPage(page);
            }
            Set<String> procInstIds = matched.stream()
                    .map(BpmProcessEfficiency::getProcInstId)
                    .collect(Collectors.toSet());
            wrapper.in(BpmProcessEfficiency::getProcInstId, procInstIds);
        }
        wrapper.orderByDesc(BpmProcessEfficiency::getStartTime);
        page = this.page(page, wrapper);
        return PageUtils.getResultAndPage(page);
    }

    /**
     * 查询节点级效能数据
     */
    public List<BpmProcessEfficiency> listNodeLevel(String procInstId) {
        return this.list(AFWrappers.<BpmProcessEfficiency>lambdaTenantQuery()
                .eq(BpmProcessEfficiency::getProcInstId, procInstId)
                .eq(BpmProcessEfficiency::getStaticType, BpmProcessEfficiency.TYPE_NODE)
                .orderByAsc(BpmProcessEfficiency::getStartTime));
    }

    /**
     * 查询任务级效能数据
     */
    public List<BpmProcessEfficiency> listTaskLevel(String procInstId, String taskDefKey) {
        return this.list(AFWrappers.<BpmProcessEfficiency>lambdaTenantQuery()
                .eq(BpmProcessEfficiency::getProcInstId, procInstId)
                .eq(BpmProcessEfficiency::getTaskDefKey, taskDefKey)
                .eq(BpmProcessEfficiency::getStaticType, BpmProcessEfficiency.TYPE_TASK)
                .orderByAsc(BpmProcessEfficiency::getStartTime));
    }
}
