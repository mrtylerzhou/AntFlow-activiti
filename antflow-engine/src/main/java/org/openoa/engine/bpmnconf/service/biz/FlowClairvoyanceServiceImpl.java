package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.FlowClairvoyanceResultVo;
import org.openoa.base.vo.FlowClairvoyanceResultVo.MatchedNode;
import org.openoa.base.vo.FlowClairvoyanceResultVo.MatchedPerson;
import org.openoa.base.vo.FlowClairvoyanceResultVo.ProcessMatchResult;
import org.openoa.base.vo.FlowClairvoyanceVo;
import org.openoa.engine.bpmnconf.mapper.FlowClairvoyanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程千里眼 - 核心搜索服务
 *
 * @author AntFlow
 */
@Slf4j
@Service
public class FlowClairvoyanceServiceImpl {

    private static final int BATCH_SIZE = 100;

    @Autowired
    private FlowClairvoyanceMapper flowClairvoyanceMapper;

    public FlowClairvoyanceResultVo search(FlowClairvoyanceVo vo) {
        // 参数校验
        if (CollectionUtils.isEmpty(vo.getUserIds())) {
            return emptyResult(0, false);
        }
        if (vo.getTimeRange() == null || vo.getTimeRange() <= 0) {
            vo.setTimeRange(1);
        }
        if (!StringUtils.hasText(vo.getNodeScope())) {
            vo.setNodeScope("CURRENT_FUTURE");
        }
        int offset = vo.getOffset() != null ? vo.getOffset() : 0;

        // 计算时间下界
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -vo.getTimeRange());
        Date timeLowerBound = cal.getTime();

        // 1. 分页查询审批中的流程
        List<Map<String, Object>> processBatch = flowClairvoyanceMapper.selectProcessBatch(
                timeLowerBound, offset, BATCH_SIZE);

        if (CollectionUtils.isEmpty(processBatch)) {
            return emptyResult(offset, false);
        }

        boolean hasMore = processBatch.size() == BATCH_SIZE;
        int nextOffset = offset + BATCH_SIZE;

        // 构建 processNumber -> processInfo 映射
        Map<String, Map<String, Object>> processMap = new LinkedHashMap<>();
        List<String> processNumbers = new ArrayList<>();
        List<String> procInstIds = new ArrayList<>();
        for (Map<String, Object> proc : processBatch) {
            String pn = (String) proc.get("processNumber");
            processNumbers.add(pn);
            processMap.put(pn, proc);
            Object procInstId = proc.get("procInstId");
            if (procInstId != null) {
                procInstIds.add(procInstId.toString());
            }
        }

        // 2. 根据nodeScope查询匹配
        List<Map<String, Object>> matches;
        if ("CURRENT".equals(vo.getNodeScope())) {
            // 仅当前节点: 查AF_RU_TASK
            matches = CollectionUtils.isEmpty(procInstIds)
                    ? Collections.emptyList()
                    : flowClairvoyanceMapper.selectCurrentNodeMatches(procInstIds, vo.getUserIds());
            // 将procInstId映射回processNumber
            Map<String, String> instToPn = new HashMap<>();
            for (Map<String, Object> proc : processBatch) {
                Object instId = proc.get("procInstId");
                if (instId != null) {
                    instToPn.put(instId.toString(), (String) proc.get("processNumber"));
                }
            }
            for (Map<String, Object> match : matches) {
                Object instId = match.get("procInstId");
                if (instId != null) {
                    match.put("processNumber", instToPn.get(instId.toString()));
                }
            }
        } else {
            // CURRENT_FUTURE / FUTURE / ALL: 查multiplayer表
            matches = flowClairvoyanceMapper.selectMultiplayerMatches(
                    processNumbers, vo.getUserIds(), vo.getNodeScope());
        }

        // 3. 按processNumber -> elementId 分组聚合
        List<ProcessMatchResult> results = buildResults(matches, processMap);

        return FlowClairvoyanceResultVo.builder()
                .results(results)
                .hasMore(hasMore)
                .nextOffset(nextOffset)
                .scannedCount(processBatch.size())
                .build();
    }

    /**
     * 将扁平匹配结果聚合为嵌套结构
     */
    private List<ProcessMatchResult> buildResults(List<Map<String, Object>> matches,
                                                   Map<String, Map<String, Object>> processMap) {
        if (CollectionUtils.isEmpty(matches)) {
            return Collections.emptyList();
        }

        // processNumber -> elementId -> List<MatchedPerson>
        Map<String, Map<String, List<MatchedPerson>>> grouped = new LinkedHashMap<>();
        // elementId -> elementName
        Map<String, String> elementNames = new HashMap<>();

        for (Map<String, Object> match : matches) {
            String pn = (String) match.get("processNumber");
            String elementId = (String) match.get("elementId");
            String elementName = match.get("elementName") != null ? match.get("elementName").toString() : "";
            String assignee = match.get("assignee") != null ? match.get("assignee").toString() : "";
            String assigneeName = match.get("assigneeName") != null ? match.get("assigneeName").toString() : "";

            if (!StringUtils.hasText(pn) || !StringUtils.hasText(elementId)) {
                continue;
            }

            elementNames.putIfAbsent(elementId, elementName);
            grouped.computeIfAbsent(pn, k -> new LinkedHashMap<>())
                    .computeIfAbsent(elementId, k -> new ArrayList<>())
                    .add(MatchedPerson.builder().assignee(assignee).assigneeName(assigneeName).build());
        }

        // 组装结果
        List<ProcessMatchResult> results = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<MatchedPerson>>> entry : grouped.entrySet()) {
            String pn = entry.getKey();
            Map<String, List<MatchedPerson>> nodeMap = entry.getValue();
            Map<String, Object> procInfo = processMap.get(pn);
            if (procInfo == null) {
                continue;
            }

            List<MatchedNode> matchedNodes = new ArrayList<>();
            int totalPersons = 0;
            for (Map.Entry<String, List<MatchedPerson>> nodeEntry : nodeMap.entrySet()) {
                String elementId = nodeEntry.getKey();
                // 去重
                List<MatchedPerson> persons = nodeEntry.getValue().stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toCollection(() -> new TreeSet<>(
                                        Comparator.comparing(MatchedPerson::getAssignee))),
                                ArrayList::new));
                totalPersons += persons.size();
                matchedNodes.add(MatchedNode.builder()
                        .elementId(elementId)
                        .elementName(elementNames.getOrDefault(elementId, ""))
                        .matchedPersons(persons)
                        .build());
            }

            results.add(ProcessMatchResult.builder()
                    .processNumber(pn)
                    .processKey((String) procInfo.get("processKey"))
                    .userName((String) procInfo.get("userName"))
                    .createTime((Date) procInfo.get("createTime"))
                    .processState(procInfo.get("processState") != null
                            ? ((Number) procInfo.get("processState")).intValue() : null)
                    .matchedNodeCount(matchedNodes.size())
                    .matchedPersonCount(totalPersons)
                    .matchedNodes(matchedNodes)
                    .build());
        }

        // 按创建时间倒序
        results.sort((a, b) -> {
            if (a.getCreateTime() == null && b.getCreateTime() == null) return 0;
            if (a.getCreateTime() == null) return 1;
            if (b.getCreateTime() == null) return -1;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        return results;
    }

    private FlowClairvoyanceResultVo emptyResult(int offset, boolean hasMore) {
        return FlowClairvoyanceResultVo.builder()
                .results(Collections.emptyList())
                .hasMore(hasMore)
                .nextOffset(offset + BATCH_SIZE)
                .scannedCount(0)
                .build();
    }
}
