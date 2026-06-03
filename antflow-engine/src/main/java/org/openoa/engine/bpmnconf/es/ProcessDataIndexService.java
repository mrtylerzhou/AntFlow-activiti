package org.openoa.engine.bpmnconf.es;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.MsgProcessEventEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.entity.Department;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.EsClientAdaptor;
import org.openoa.base.util.DateUtil;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.base.vo.HisTaskVo;
import org.openoa.base.vo.MqProcessEventVo;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ProcessDataESDto;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.DepartmentServiceImpl;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generic process data indexing service.
 * Indexes process event data into Elasticsearch whenever a workflow button operation occurs.
 *
 * @Author tylerzhou
 */
@Service
@Slf4j
public class ProcessDataIndexService {

    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private DepartmentServiceImpl departmentService;
    @Autowired(required = false)
    private EsClientAdaptor esClientAdaptor;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Value("${antflow.es.index.name:antflow-process-data}")
    private String indexName;

    /**
     * Process an MQ process event and index the data to ES.
     */
    public void process(MqProcessEventVo entity) {
        if (esClientAdaptor == null) {
            log.debug("ES client not configured, skipping process data indexing");
            return;
        }
        if (!checkParam(entity)) {
            return;
        }

        String processCode = entity.getProcessCode();
        ProcessDataESDto dto = new ProcessDataESDto();

        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processCode);
        if (bpmBusinessProcess == null) {
            log.warn("Process not found by code: {}", processCode);
            return;
        }

        // Populate basic process fields
        dto.of(bpmBusinessProcess);

        Integer buttonOperationType = entity.getButtonOperationType();
        MsgProcessEventEnum eventEnum = MsgProcessEventEnum.getEnumByCode(buttonOperationType);

        // Handle process finish state override
        Integer processState = bpmBusinessProcess.getProcessState();
        if (Objects.equals(buttonOperationType, MsgProcessEventEnum.PROCESS_FINISH.getCode())) {
            processState = ProcessStateEnum.HANDLED_STATE.getCode();
            dto.setProcessState(processState);
        }

        dto.setCurrentType(buttonOperationType);
        dto.setCurrentTypeDesc(eventEnum != null ? eventEnum.getDesc() : null);

        // Use formCode from message if available
        if (StringUtils.isNotBlank(entity.getFormCode())) {
            dto.setFormCode(entity.getFormCode());
        }

        // Set startUserName from BpmBusinessProcess
        dto.setStartUserName(bpmBusinessProcess.getUserName());

        // Set applicant = process initiator
        String applyEmployeeId = bpmBusinessProcess.getCreateUser();
        dto.setApplyEmployeeId(applyEmployeeId);
        dto.setApplyEmployeeName(bpmBusinessProcess.getUserName());

        // Handle resubmit / back-to-modify states
        if (Objects.equals(buttonOperationType, MsgProcessEventEnum.PROCESS_RESUBMIT.getCode())) {
            dto.setBackToModifyRelatedUser(entity.getOperationUserId());
        } else if (Objects.equals(buttonOperationType, MsgProcessEventEnum.BUTTON_BACK_TO_MODIFY.getCode())) {
            dto.setBackToModifyRelatedUser(entity.getOperationUserId());
        }

        String procInstId = bpmBusinessProcess.getProcInstId();

        // Get already processed users
        List<String> assignees = taskMgmtMapper.getAssigneesByEntryId(procInstId);
        if (assignees != null && !assignees.isEmpty()) {
            dto.setAlreadyProcessedUsers(assignees);
        }

        // Get current tasks
        List<TaskMgmtVO> currentTasks = taskMgmtMapper.getCurrentAssignee(procInstId);
        if (currentTasks != null && !currentTasks.isEmpty()) {
            dto.setCurrentProcessingUsers(currentTasks.stream()
                    .map(TaskMgmtVO::getOriginalName)
                    .collect(Collectors.toList()));
        }

        // Get task history
        List<HisTaskVo> hisTaskList = taskMgmtMapper.findHisTaskList(procInstId);
        dto.setTaskList(hisTaskList);

        // Get department info for applicant
        if (StringUtils.isNotBlank(applyEmployeeId)) {
            try {
                Department department = departmentService.getDepartmentByEmployeeId(applyEmployeeId);
                if (department != null) {
                    dto.setApplyEmployeeDeptId(department.getId() != null ? department.getId().toString() : null);
                    dto.setApplyEmployeeDeptName(department.getName());
                }
            } catch (Exception e) {
                log.debug("Failed to get department info for employee {}: {}", applyEmployeeId, e.getMessage());
            }
        }

        // Get all related users (all approvers)
        List<BaseIdTranStruVo> allRelatedUsers = getAllVerifyUserIds(processCode);
        dto.setAllProcessRelatedUsers(allRelatedUsers);

        // Final state: clear current processing users
        if (ProcessStateEnum.HANDLED_STATE.getCode().equals(processState)
                || ProcessStateEnum.END_STATE.getCode().equals(processState)
                || ProcessStateEnum.REJECT_STATE.getCode().equals(processState)) {
            dto.setCurrentProcessingUsers(new ArrayList<>());
            dto.setCurrentProcessingUserNames(new ArrayList<>());
        }

        // Save to ES
        insertOrUpdateDataEs(dto);
    }

    private boolean checkParam(MqProcessEventVo entity) {
        if (entity == null) {
            return false;
        }
        if (entity.getButtonOperationType() == null) {
            log.error("Message missing buttonOperationType: {}", entity);
            return false;
        }
        if (StringUtils.isBlank(entity.getProcessCode())) {
            log.error("Message missing processCode: {}", entity);
            return false;
        }
        return true;
    }

    private void insertOrUpdateDataEs(ProcessDataESDto dto) {
        String now = DateUtil.SDF_DATETIME_PATTERN.format(new Date());
        dto.setUpdateTime(now);
        String processCode = dto.getProcessCode();
        Integer currentType = dto.getCurrentType();

        String json = com.alibaba.fastjson2.JSON.toJSONString(dto);
        if (Objects.equals(currentType, MsgProcessEventEnum.PROCESS_SUBMIT.getCode())) {
            dto.setCreateTime(now);
            json = com.alibaba.fastjson2.JSON.toJSONString(dto);
            esClientAdaptor.saveData(indexName, processCode, json);
        } else {
            esClientAdaptor.updateData(indexName, processCode, json);
        }
    }

    /**
     * Collect all verify user IDs and names from approval history.
     */
    private List<BaseIdTranStruVo> getAllVerifyUserIds(String processCode) {
        if (StringUtils.isBlank(processCode)) {
            return null;
        }
        List<BpmVerifyInfoVo> verifyInfoVos = bpmVerifyInfoBizService.getBpmVerifyInfoVos(processCode, false);
        if (verifyInfoVos == null || verifyInfoVos.isEmpty()) {
            return null;
        }
        List<BaseIdTranStruVo> allUsers = new ArrayList<>();
        List<String> batchLookupIds = new ArrayList<>();
        for (BpmVerifyInfoVo vo : verifyInfoVos) {
            if (StringUtils.isBlank(vo.getVerifyUserName())) {
                continue;
            }
            if (StringUtils.isNotBlank(vo.getVerifyUserId())) {
                allUsers.add(BaseIdTranStruVo.builder().id(vo.getVerifyUserId()).name(vo.getVerifyUserName()).build());
            }
            if (vo.getVerifyUserIds() != null) {
                batchLookupIds.addAll(vo.getVerifyUserIds());
            }
        }
        if (!batchLookupIds.isEmpty()) {
            Map<String, String> employeeInfoMap = bpmnEmployeeInfoProviderService.provideEmployeeInfo(batchLookupIds);
            for (String userId : batchLookupIds) {
                allUsers.add(BaseIdTranStruVo.builder().id(userId).name(employeeInfoMap.get(userId)).build());
            }
        }
        return allUsers;
    }
}
