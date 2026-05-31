package org.openoa.engine.bpmnconf.es;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.PageDto;
import org.openoa.base.interf.EsClientAdaptor;
import org.openoa.base.util.DateUtil;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ES-based process approval query service.
 * Provides ES-backed queries for newly created, already done, and todo process lists.
 *
 * @Author tylerzhou
 */
@Slf4j
@Service
public class ProcessApprovalEsServiceImpl {

    @Autowired(required = false)
    private EsClientAdaptor esClientAdaptor;

    @Value("${antflow.es.index.name:antflow-process-data}")
    private String indexName;

    /**
     * Query newly created list from ES (PC and APP).
     */
    public List<TaskMgmtVO> viewNewlyCreatedList(PageDto pageDto, TaskMgmtVO vo, com.baomidou.mybatisplus.extension.plugins.pagination.Page<TaskMgmtVO> page) {
        EsQueryParam param = generateQueryParam(pageDto, vo, QueryTypeEnum.QRY_NEW_LIST);
        return qryResult(param, page, vo, QueryTypeEnum.QRY_NEW_LIST);
    }

    /**
     * Query already done list from ES (PC and APP).
     */
    public List<TaskMgmtVO> viewAlreadyDoneList(PageDto pageDto, TaskMgmtVO vo, com.baomidou.mybatisplus.extension.plugins.pagination.Page<TaskMgmtVO> page) {
        EsQueryParam param = generateQueryParam(pageDto, vo, QueryTypeEnum.QRY_ALREADYDONE_LIST);
        return qryResult(param, page, vo, QueryTypeEnum.QRY_ALREADYDONE_LIST);
    }

    /**
     * Query todo list from ES (PC and APP).
     */
    public List<TaskMgmtVO> viewToDoList(PageDto pageDto, TaskMgmtVO vo, com.baomidou.mybatisplus.extension.plugins.pagination.Page<TaskMgmtVO> page) {
        EsQueryParam param = generateQueryParam(pageDto, vo, QueryTypeEnum.QRY_TODO_LIST);
        return qryResult(param, page, vo, QueryTypeEnum.QRY_TODO_LIST);
    }

    /**
     * Query all (app combined: todo + new + done).
     */
    public List<TaskMgmtVO> appAllSearch(PageDto pageDto, TaskMgmtVO vo, com.baomidou.mybatisplus.extension.plugins.pagination.Page<TaskMgmtVO> page) {
        EsQueryParam param = generateQueryParam(pageDto, vo, QueryTypeEnum.QRY_ALL_LIST);
        return qryResult(param, page, vo, QueryTypeEnum.QRY_ALL_LIST);
    }

    private EsQueryParam generateQueryParam(PageDto pageDto, TaskMgmtVO vo, QueryTypeEnum queryTypeEnum) {
        EsQueryParam param = new EsQueryParam();
        param.setPageNum(pageDto.getPage());
        param.setPageSize(pageDto.getPageSize());
        param.setEqualsConditions(generateEqCondition(vo, queryTypeEnum));
        param.setMatchPhraseConditions(generateMatchPhraseCondition(vo));
        param.setNoEqualsConditions(generateNoEqualCondition(queryTypeEnum));
        param.setSortFields(generateSortField(queryTypeEnum));
        param.setNestedSortConfigs(generateNestedSortConfig(vo, queryTypeEnum));
        param.setOrLikeConditions(generateLikeMap(vo));
        param.setTermsConditions(generateTermsCondition(vo));
        if (queryTypeEnum == QueryTypeEnum.QRY_ALL_LIST) {
            param.setOrConditions(generateOrSearchCondition(vo));
        }
        return param;
    }

    private List<TaskMgmtVO> qryResult(EsQueryParam param,
                                        com.baomidou.mybatisplus.extension.plugins.pagination.Page<TaskMgmtVO> page,
                                        TaskMgmtVO vo, QueryTypeEnum queryTypeEnum) {
        if (esClientAdaptor == null) {
            log.debug("ES client not configured, returning empty list");
            return Collections.emptyList();
        }
        EsQueryResult result = esClientAdaptor.queryData(param, indexName);
        List<ProcessDataESDto> dtos = buildProcessDataDtos(result);
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        page.setTotal(result.getTotal());
        return dtos.stream().map(dto -> processDataDtoToTaskMgmtVo(dto, vo, queryTypeEnum)).collect(Collectors.toList());
    }

    private Map<String, Object> generateEqCondition(TaskMgmtVO vo, QueryTypeEnum queryTypeEnum) {
        if (vo == null) {
            return new HashMap<>();
        }
        Map<String, Object> eqMap = new HashMap<>();
        switch (queryTypeEnum) {
            case QRY_NEW_LIST:
                eqMap.put("startUserId", vo.getApplyUser());
                break;
            case QRY_TODO_LIST:
                eqMap.put("currentProcessingUsers", vo.getApplyUser());
                break;
            case QRY_ALREADYDONE_LIST:
                eqMap.put("alreadyProcessedUsers", vo.getApplyUser());
                break;
            default:
                break;
        }
        if (!ObjectUtils.isEmpty(vo.getApplyUserId())) {
            eqMap.put("startUserId", vo.getApplyUserId());
        }
        eqMap.put("processState", vo.getProcessState());
        eqMap.put("isDel", 0);
        return eqMap;
    }

    private Map<String, String> generateMatchPhraseCondition(TaskMgmtVO vo) {
        if (vo == null) {
            return new HashMap<>();
        }
        Map<String, String> matchMap = new HashMap<>();
        matchMap.put("processDigest", vo.getProcessDigest());
        return matchMap;
    }

    private Map<String, Object> generateLikeMap(TaskMgmtVO vo) {
        if (vo == null) {
            return new HashMap<>();
        }
        Map<String, Object> likeMap = new HashMap<>();
        if (StringUtils.isNotBlank(vo.getSearch())) {
            likeMap.put("processCode", vo.getSearch());
            likeMap.put("description", vo.getSearch());
        } else {
            likeMap.put("processCode", vo.getProcessNumber());
        }
        return likeMap;
    }

    private Map<String, Object> generateNoEqualCondition(QueryTypeEnum queryTypeEnum) {
        Map<String, Object> noEqualMap = new HashMap<>();
        if (queryTypeEnum == QueryTypeEnum.QRY_ALREADYDONE_LIST) {
            noEqualMap.put("currentType", 1);
        }
        return noEqualMap;
    }

    private Map<String, String> generateSortField(QueryTypeEnum queryTypeEnum) {
        Map<String, String> sortMap = new HashMap<>();
        switch (queryTypeEnum) {
            case QRY_NEW_LIST:
            case QRY_ALL_LIST:
                sortMap.put("processCreateTime", "desc");
                break;
            case QRY_TODO_LIST:
                sortMap.put("currentTaskCreateTime", "desc");
                break;
            case QRY_ALREADYDONE_LIST:
                sortMap.put("taskList.processTime", "desc");
                break;
            default:
                break;
        }
        return sortMap;
    }

    private Map<String, EsQueryParam.NestedSortConfig> generateNestedSortConfig(TaskMgmtVO vo, QueryTypeEnum queryTypeEnum) {
        Map<String, EsQueryParam.NestedSortConfig> configMap = new HashMap<>();
        if (queryTypeEnum == QueryTypeEnum.QRY_ALREADYDONE_LIST) {
            configMap.put("taskList.processTime",
                    new EsQueryParam.NestedSortConfig("taskList", "taskList.userId", vo.getApplyUser()));
        }
        return configMap;
    }

    private Map<String, List> generateTermsCondition(TaskMgmtVO vo) {
        if (vo == null) {
            return new HashMap<>();
        }
        Map<String, List> termsMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(vo.getProcessKeyList())) {
            termsMap.put("processKey", vo.getProcessKeyList());
        } else if (!ObjectUtils.isEmpty(vo.getVersionProcessKeys())) {
            termsMap.put("processKey", vo.getVersionProcessKeys());
        }
        return termsMap;
    }

    private Map<String, Object> generateOrSearchCondition(TaskMgmtVO vo) {
        if (vo == null) {
            return new HashMap<>();
        }
        Map<String, Object> orEqMap = new HashMap<>();
        orEqMap.put("startUserId", vo.getApplyUser());
        orEqMap.put("currentProcessingUsers", vo.getApplyUser());
        orEqMap.put("alreadyProcessedUsers", vo.getApplyUser());
        return orEqMap;
    }

    private List<ProcessDataESDto> buildProcessDataDtos(EsQueryResult result) {
        if (result.getJsonResults() == null || result.getJsonResults().isEmpty()) {
            return Collections.emptyList();
        }
        return result.getJsonResults().stream()
                .map(json -> JSON.parseObject(json, ProcessDataESDto.class))
                .collect(Collectors.toList());
    }

    private TaskMgmtVO processDataDtoToTaskMgmtVo(ProcessDataESDto dto, TaskMgmtVO fvo, QueryTypeEnum queryTypeEnum) {
        if (dto == null) {
            return new TaskMgmtVO();
        }
        TaskMgmtVO vo = new TaskMgmtVO();
        vo.setApplyUser(dto.getStartUserName());
        vo.setApplyUserName(dto.getStartUserName());
        vo.setDescription(dto.getDescription());
        vo.setTaskName(dto.getCurrentNodeName());
        vo.setTaskIds(dto.getCurrentTaskIds());
        vo.setTaskStype(dto.getProcessState());
        vo.setProcessState(dto.getProcessState());
        vo.setProcessKey(dto.getProcessKey());
        vo.setProcessCode(dto.getProcessCode());
        vo.setProcessDigest(dto.getProcessDigest());
        vo.setProcessNumber(dto.getProcessCode());

        Date createTime = null;
        try {
            if (StringUtils.isNotBlank(dto.getProcessCreateTime())) {
                createTime = DateUtil.SDF_DATETIME_PATTERN.parse(dto.getProcessCreateTime());
            }
        } catch (Exception e) {
            log.warn("datetime parse error", e);
        }
        vo.setCreateTime(createTime);

        switch (queryTypeEnum) {
            case QRY_NEW_LIST:
                vo.setStartTime(dto.getProcessCreateTime());
                if (StringUtils.isNotBlank(dto.getProcessCreateTime())) {
                    vo.setRunTime(parseDate(dto.getProcessCreateTime()));
                }
                break;
            case QRY_TODO_LIST:
                vo.setStartTime(dto.getCurrentTaskCreateTime());
                if (StringUtils.isNotBlank(dto.getCurrentTaskCreateTime())) {
                    vo.setRunTime(parseDate(dto.getCurrentTaskCreateTime()));
                }
                List<String> currentTaskIds = dto.getCurrentTaskIds();
                if (currentTaskIds != null && !currentTaskIds.isEmpty()) {
                    if (currentTaskIds.size() == 1) {
                        vo.setTaskId(currentTaskIds.get(0));
                    } else {
                        List<String> currentProcessingUsers = dto.getCurrentProcessingUsers();
                        if (currentProcessingUsers != null) {
                            int i = currentProcessingUsers.indexOf(fvo.getApplyUser());
                            if (i >= 0 && i < currentTaskIds.size()) {
                                vo.setTaskId(currentTaskIds.get(i));
                            }
                        }
                    }
                }
                break;
            case QRY_ALREADYDONE_LIST:
                List<HisTaskVo> taskList = dto.getTaskList();
                if (taskList != null && !taskList.isEmpty()) {
                    List<HisTaskVo> currentUserTasks = taskList.stream()
                            .filter(a -> Objects.equals(a.getUserId(), fvo.getApplyUser()))
                            .collect(Collectors.toList());
                    if (!currentUserTasks.isEmpty()) {
                        HisTaskVo hisTaskVo = currentUserTasks.get(currentUserTasks.size() - 1);
                        vo.setStartTime(hisTaskVo.getStartTime());
                        if (StringUtils.isNotBlank(hisTaskVo.getProcessTime())) {
                            vo.setRunTime(parseDate(hisTaskVo.getProcessTime()));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return vo;
    }

    private Date parseDate(String dateStr) {
        try {
            return DateUtil.SDF_DATETIME_PATTERN.parse(dateStr);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", dateStr, e);
            return null;
        }
    }

    enum QueryTypeEnum {
        QRY_COMMON(0, "通用"),
        QRY_TODO_LIST(1, "查询待办列表"),
        QRY_ALREADYDONE_LIST(2, "查询已办列表"),
        QRY_NEW_LIST(3, "查询我的新建列表"),
        QRY_ALL_LIST(4, "APP全部查询列表");

        @Getter
        private final Integer code;
        @Getter
        private final String desc;

        QueryTypeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
