package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.vo.ProcessCompareCandidateVo;
import org.openoa.base.vo.ProcessCompareEntrustVo;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessCompareBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程对比(实例级): 流程监控-更多-流程对比
 *
 * <p>设计: .scratch/process-instance-compare-design.md §4</p>
 *
 * <p>仅提供两个薄查询; 节点对齐(alignTrees)与审批人 diff 全部由前端完成,
 * 与版本比较"前端 diff 引擎"架构保持一致。</p>
 */
@Slf4j
@Service
public class ProcessCompareBizServiceImpl implements ProcessCompareBizService {

    /** 候选实例单次最大返回条数 */
    private static final int CANDIDATE_LIMIT = 50;

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmFlowrunEntrustService bpmFlowrunEntrustService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;

    // ==================================================================================
    // compareCandidates
    // ==================================================================================

    @Override
    public List<ProcessCompareCandidateVo> compareCandidates(String formCode, String keyword) {
        if (StringUtils.isEmpty(formCode)) {
            throw new AFBizException("formCode 不能为空");
        }
        QueryWrapper<BpmBusinessProcess> wrapper = new QueryWrapper<BpmBusinessProcess>()
                .eq("PROCESSINESS_KEY", formCode)
                .eq("is_del", 0)
                .orderByDesc("CREATE_TIME")
                .last("limit " + CANDIDATE_LIMIT);
        if (StringUtils.isNotEmpty(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like("BUSINESS_NUMBER", kw).or().like("user_name", kw));
        }
        List<BpmBusinessProcess> processes = bpmBusinessProcessService.getBaseMapper().selectList(wrapper);
        if (CollectionUtils.isEmpty(processes)) {
            return Collections.emptyList();
        }

        // 批量反查 t_bpmn_conf(bpmn_code → id/bpmn_name)
        Set<String> versions = processes.stream()
                .map(BpmBusinessProcess::getVersion)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, BpmnConf> confByVersion = new HashMap<>();
        if (!versions.isEmpty()) {
            List<BpmnConf> confs = bpmnConfService.list(
                    new QueryWrapper<BpmnConf>().in("bpmn_code", versions));
            for (BpmnConf conf : confs) {
                confByVersion.putIfAbsent(conf.getBpmnCode(), conf);
            }
        }

        // 批量补全发起人姓名(user_name 为空时)
        List<String> missingUserIds = processes.stream()
                .filter(p -> StringUtils.isEmpty(p.getUserName()) && StringUtils.isNotEmpty(p.getCreateUser()))
                .map(BpmBusinessProcess::getCreateUser)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> nameMap = Collections.emptyMap();
        if (!missingUserIds.isEmpty()) {
            try {
                nameMap = Optional.ofNullable(
                        employeeInfoProvider.provideEmployeeInfo(missingUserIds)).orElse(Collections.emptyMap());
            } catch (Exception e) {
                log.warn("compareCandidates: batch resolve user names failed", e);
            }
        }

        List<ProcessCompareCandidateVo> result = new ArrayList<>(processes.size());
        for (BpmBusinessProcess p : processes) {
            BpmnConf conf = confByVersion.get(p.getVersion());
            String userName = StringUtils.isNotEmpty(p.getUserName())
                    ? p.getUserName() : nameMap.get(p.getCreateUser());
            result.add(ProcessCompareCandidateVo.builder()
                    .processNumber(p.getBusinessNumber())
                    .version(p.getVersion())
                    .createUser(p.getCreateUser())
                    .userName(userName)
                    .createTime(p.getCreateTime())
                    .processState(p.getProcessState())
                    .confId(conf != null ? conf.getId() : null)
                    .bpmnName(conf != null ? conf.getBpmnName() : null)
                    .build());
        }
        return result;
    }

    // ==================================================================================
    // compareEntrusts
    // ==================================================================================

    @Override
    public List<ProcessCompareEntrustVo> compareEntrusts(String processNumber) {
        if (StringUtils.isEmpty(processNumber)) {
            throw new AFBizException("processNumber 不能为空");
        }
        BpmBusinessProcess process = bpmBusinessProcessService.getBaseMapper().selectOne(
                new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", processNumber));
        if (ObjectUtils.isEmpty(process)) {
            throw new AFBizException("流程实例不存在: " + processNumber);
        }
        if (StringUtils.isEmpty(process.getProcInstId())) {
            return Collections.emptyList();
        }
        List<BpmFlowrunEntrust> records = bpmFlowrunEntrustService.list(
                new QueryWrapper<BpmFlowrunEntrust>()
                        .eq("runinfoid", process.getProcInstId())
                        .orderByDesc("id"));
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream()
                .map(r -> ProcessCompareEntrustVo.builder()
                        .nodeId(r.getNodeId())
                        .actionType(r.getActionType())
                        .actionTypeName(actionTypeName(r.getActionType()))
                        .originalId(r.getOriginal())
                        .originalName(r.getOriginalName())
                        .actualId(r.getActual())
                        .actualName(r.getActualName())
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
}
