package org.openoa.engine.bpmnconf.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.activiti.engine.impl.variable.*;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.entity.AFExecutionEntity;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseNumIdStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.DIYProcessInfoDTO;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.entity.BpmProcessNotice;
import org.openoa.base.entity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNoticeServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.base.interf.LFFormOperationAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Service
public class TaskMgmtServiceImpl extends ServiceImpl<TaskMgmtMapper, TaskMgmtVO> {
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessServiceImpl processService;
    @Autowired
    protected BpmBusinessProcessMapper bpmBusinessProcessMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired(required = false)
    private Map<String, FormOperationAdaptor> formOperationAdaptorMap;
    @Autowired
    @Lazy
    private BpmnConfBizService bpmnConfBizService;
    @Autowired
    private BpmProcessNoticeServiceImpl bpmProcessNoticeService;
    @Autowired
    private HistoryService historyService;



    /**
     * find task by its id
     *
     * @param taskId
     * @return
     * @throws AFBizException
     */
    public TaskMgmtVO findTask(String taskId) throws AFBizException {
        return taskMgmtMapper.findTask(taskId);
    }

    /**
     * get task vo by its id
     *
     * @param taskId
     * @return
     * @throws AFBizException
     */
    public TaskMgmtVO getAgencyList(String taskId) throws AFBizException {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        TaskMgmtVO mgmtVO = new TaskMgmtVO();
        if (!ObjectUtils.isEmpty(task)) {
            mgmtVO.setTaskStype(2);
        } else {
            mgmtVO.setTaskStype(1);
        }
        return mgmtVO;
    }

    /**
     * change task assignee by taskid
     */
    public void updateTask(TaskMgmtVO taskMgmtVO) {
        if (ObjectUtils.isEmpty(taskMgmtVO.getTaskIds())) {
            throw new AFBizException("please select the task ids to modify ！！");
        }
        if (!ObjectUtils.isEmpty(taskMgmtVO.getTaskIds())) {
            taskMgmtVO.getTaskIds().forEach(o -> {
                taskMgmtMapper.updateaActinst(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .applyUserName(taskMgmtVO.getApplyUserName())
                        .taskId(o)
                        .build());
                taskMgmtMapper.updateaTaskinst(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .applyUserName(taskMgmtVO.getApplyUserName())
                        .taskId(o)
                        .build());
                taskMgmtMapper.updateTask(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .applyUserName(taskMgmtVO.getApplyUserName())
                        .taskId(o)
                        .build());
            });

        }
    }


    /**
     * get current node and taskId by businessId and processCode
     *
     * @param taskMgmtVO
     * @return
     */
    public TaskMgmtVO findByTask(TaskMgmtVO taskMgmtVO) {
        try {

            taskMgmtVO.setApplyUser( SecurityUtils.getLogInEmpIdSafe().toString());
            BpmBusinessProcess bpmBusinessProcess = processService.findBpmBusinessProcess(taskMgmtVO.getBusinessId(), taskMgmtVO.getCode());
            if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
                taskMgmtVO.setEntryId(bpmBusinessProcess.getEntryId());
                TaskMgmtVO mgmtVO = taskMgmtMapper.findByTask(taskMgmtVO);
                return mgmtVO;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new AFBizException("根据业务ID:[" + taskMgmtVO.getBusinessId() + "]无法查询代办数据");
        }
    }

    public void  changeFutureAssignees(String executionId, String variableName, List<String> assignees){
        Map<String,Object> assigneeMap=new HashMap<>();
        assigneeMap.put(variableName,assignees);
        runtimeService.setVariables(executionId,assigneeMap);
    }

    public List<DIYProcessInfoDTO> viewProcessInfo(String desc){
        List<DIYProcessInfoDTO> diyProcessInfoDTOS = baseFormInfo(desc);
        if(CollectionUtils.isEmpty(diyProcessInfoDTOS)){
            return diyProcessInfoDTOS;
        }
        List<String> formCodes = diyProcessInfoDTOS.stream().map(DIYProcessInfoDTO::getKey).collect(Collectors.toList());
        LambdaQueryWrapper<BpmnConf> queryWrapper = Wrappers.<BpmnConf>lambdaQuery()
                .select(BpmnConf::getFormCode, BpmnConf::getExtraFlags)
                .in(BpmnConf::getFormCode, formCodes)
                .eq(BpmnConf::getEffectiveStatus, 1);
        List<BpmnConf> bpmnConfs = bpmnConfBizService.getService().list(queryWrapper);
        if(!CollectionUtils.isEmpty(bpmnConfs)){
            Map<String, Integer> formCode2Flags = bpmnConfs
                    .stream()
                    .filter(a->a.getExtraFlags()!=null)
                    .collect(Collectors.toMap(BpmnConf::getFormCode, BpmnConf::getExtraFlags, (v1, v2) -> v1));
            Map<String, List<BpmProcessNotice>> processNoticeMap = bpmProcessNoticeService.processNoticeMap(formCodes);
            for (DIYProcessInfoDTO diyProcessInfoDTO : diyProcessInfoDTOS) {
                String formCode = diyProcessInfoDTO.getKey();
                Integer flags = formCode2Flags.get(formCode);
                if(flags!=null){
                    boolean hasStartUserChooseModules = BpmnConfFlagsEnum.HAS_STARTUSER_CHOOSE_MODULES.flagsContainsCurrent(flags);
                    diyProcessInfoDTO.setHasStarUserChooseModule(hasStartUserChooseModules);
                }
                List<BpmProcessNotice> bpmProcessNotices = processNoticeMap.get(diyProcessInfoDTO.getKey());
                if(!CollectionUtils.isEmpty(bpmProcessNotices)){
                    List<BaseNumIdStruVo> processNotices=new ArrayList<>();

                    for (ProcessNoticeEnum value : ProcessNoticeEnum.values()) {
                        Integer type = value.getCode();
                        String descByCode = value.getDesc();
                        BaseNumIdStruVo struVo=new BaseNumIdStruVo();
                        struVo.setId(type.longValue());
                        struVo.setName(descByCode);
                        for (BpmProcessNotice bpmProcessNotice : bpmProcessNotices) {
                           if(Objects.equals(value.getCode(),bpmProcessNotice.getType())){
                               struVo.setActive(true);
                           }
                        }
                        processNotices.add(struVo);
                    }
                    diyProcessInfoDTO.setProcessNotices(processNotices);
                }
                BpmnConfVo confVo=new BpmnConfVo();
                confVo.setFormCode(formCode);
                bpmnConfBizService.setBpmnTemplateVos(confVo);
                diyProcessInfoDTO.setTemplateVos(confVo.getTemplateVos());
            }
        }
        return diyProcessInfoDTOS;
    }
    /**私有方法 */
    private List<DIYProcessInfoDTO> baseFormInfo(String desc){
        List<DIYProcessInfoDTO> results=new ArrayList<>();
        for (Map.Entry<String, FormOperationAdaptor> stringFormOperationAdaptorEntry : formOperationAdaptorMap.entrySet()) {
            String key=stringFormOperationAdaptorEntry.getKey();
            FormOperationAdaptor formOperationAdaptor = stringFormOperationAdaptorEntry.getValue();
            if(formOperationAdaptor instanceof LFFormOperationAdaptor){
                continue;
            }
            ActivitiServiceAnno annotation = ClassUtils.getUserClass(formOperationAdaptor).getAnnotation(ActivitiServiceAnno.class);
            if (StringUtils.isEmpty(annotation.desc())){
                continue;
            }
            if(!StringUtils.isEmpty(desc)){
                if(annotation.desc().contains(desc)){
                    results.add(
                            DIYProcessInfoDTO
                                    .builder()
                                    .key(key)
                                    .value(annotation.desc())
                                    .type("DIY")
                                    .build()
                    );
                }
            }
            else{
                results.add(
                        DIYProcessInfoDTO
                                .builder()
                                .key(key)
                                .value(annotation.desc())
                                .type("DIY")
                                .build()
                );
            }
        }
        return results;
    }
    public void   insertExecution(IdGenerator idGenerator, List<HistoricTaskInstance> hisInstances, BpmBusinessProcess bpmBusinessProcess){

        String nextId = idGenerator.getNextId();
        AFExecutionEntity executionInstEntity = new AFExecutionEntity();
        executionInstEntity.setId(nextId);
        executionInstEntity.setRevision(1);
        executionInstEntity.setProcessInstanceId(bpmBusinessProcess.getProcInstId());

        executionInstEntity.setBusinessKey(bpmBusinessProcess.getProcessinessKey());
        executionInstEntity.setProcessDefinitionId(hisInstances.get(0).getProcessDefinitionId());
        executionInstEntity.setActivityId(null);
        executionInstEntity.setIsActive(false);
        executionInstEntity.setIsConcurrent(false);

        executionInstEntity.setIsScope(true);
        executionInstEntity.setParentId(null);
        executionInstEntity.setSuperExecutionId(null);
        executionInstEntity.setSuspensionState(1);
        executionInstEntity.setCachedEntityState(2);
        executionInstEntity.setTenantId(null);
        executionInstEntity.setName(null);

        taskMgmtMapper.insertExecution(executionInstEntity);

        String newId=idGenerator.getNextId();
        AFExecutionEntity afExecution = executionInstEntity.clone();
        afExecution.setId(newId);
        afExecution.setParentId(nextId);
        afExecution.setActivityId(hisInstances.get(0).getTaskDefinitionKey());
        taskMgmtMapper.insertExecution(afExecution);
        for (HistoricTaskInstance hisInstance : hisInstances) {

            String activeId = idGenerator.getNextId();
            AFExecutionEntity afExecution1 = afExecution.clone();
            afExecution1.setId(activeId);
            afExecution1.setParentId(newId);
            afExecution1.setIsScope(false);
            afExecution1.setIsActive(true);
            afExecution1.setIsConcurrent(true);
            taskMgmtMapper.insertExecution(afExecution1);

            TaskEntity newTask = new TaskEntity();
            newTask.setId(idGenerator.getNextId());
            newTask.setExecutionId(activeId);
            newTask.setProcessInstanceId(bpmBusinessProcess.getProcInstId());
            newTask.setProcessDefinitionId(hisInstance.getProcessDefinitionId());
            newTask.setName(hisInstance.getName());
            newTask.setTaskDefinitionKey(hisInstance.getTaskDefinitionKey());
            newTask.setAssignee(hisInstance.getAssignee());
            newTask.setPriority(hisInstance.getPriority());
            newTask.setCreateTime(hisInstance.getCreateTime());
            newTask.setSuspensionState(1);
            taskMgmtMapper.insertTask(newTask);

            final List<HistoricVariableInstance> hstVariables = historyService
                    .createHistoricVariableInstanceQuery()
                    .processInstanceId(bpmBusinessProcess.getProcInstId())
                    .executionId(hisInstance.getExecutionId())
                    .list();

            List<VariableInstanceEntity> variables = new ArrayList<>();
            for (HistoricVariableInstance hstVariable : hstVariables) {
                VariableType type;
                if (hstVariable.getVariableTypeName().equals("boolean")) {
                    type = new BooleanType();
                } else if (hstVariable.getVariableTypeName().equals("integer")) {
                    type = new IntegerType();
                } else if (hstVariable.getVariableTypeName().equals("short")) {
                    type = new ShortType();
                } else if (hstVariable.getVariableTypeName().equals("long")) {
                    type = new LongType();
                } else if (hstVariable.getVariableTypeName().equals("double")) {
                    type = new DoubleType();
                } else {
                    type = new StringType(100);
                }

                VariableInstanceEntity variableInstance = VariableInstanceEntity.create(hstVariable.getVariableName(), type, hstVariable.getValue());
                variableInstance.setId(idGenerator.getNextId());
                variableInstance.setTypeName(hstVariable.getVariableTypeName());
                variableInstance.setName(hstVariable.getVariableName());
                variableInstance.setExecutionId(newId);
                variableInstance.setProcessInstanceId(hstVariable.getProcessInstanceId());
                variableInstance.setTaskId(newTask.getId());
                variableInstance.setValue(hstVariable.getValue());

                variables.add(variableInstance);
            }

            taskMgmtMapper.bulkInsertVariableInstance(variables);


            HistoricTaskInstanceEntity hstTaskUpdate = new HistoricTaskInstanceEntity();
            hstTaskUpdate.setId(hisInstance.getId());
            hstTaskUpdate.setProcessDefinitionId(hisInstance.getProcessDefinitionId());
            hstTaskUpdate.setExecutionId(hisInstance.getExecutionId());
            hstTaskUpdate.setName(hisInstance.getName());
            hstTaskUpdate.setProcessInstanceId(hisInstance.getProcessInstanceId());
            hstTaskUpdate.setOwner(hisInstance.getOwner());
            hstTaskUpdate.setAssignee(hisInstance.getAssignee());
            hstTaskUpdate.setClaimTime(hisInstance.getClaimTime());
            hstTaskUpdate.setEndTime(null);
            hstTaskUpdate.setDurationInMillis(null);
            hstTaskUpdate.setDeleteReason(null);
            hstTaskUpdate.setTaskDefinitionKey(hisInstance.getTaskDefinitionKey());
            hstTaskUpdate.setPriority(hisInstance.getPriority());
            hstTaskUpdate.setDueDate(hisInstance.getDueDate());
            hstTaskUpdate.setCategory(hisInstance.getCategory());


            taskMgmtMapper.updateHistoricTaskInstance(hstTaskUpdate);

        }
        int hisSize = hisInstances.size();
        if(hisInstances.size()>1){
            VariableInstanceEntity variableInstance = VariableInstanceEntity.create("nrOfInstances", new IntegerType(), hisSize);
            variableInstance.setId(idGenerator.getNextId());
            variableInstance.setExecutionId(newId);
            variableInstance.setProcessInstanceId(bpmBusinessProcess.getProcInstId());
            variableInstance.setTaskId(null);

            VariableInstanceEntity variableInstance1 = VariableInstanceEntity.create("nrOfCompletedInstances", new IntegerType(), 0);
            variableInstance1.setId(idGenerator.getNextId());
            variableInstance1.setExecutionId(newId);
            variableInstance1.setProcessInstanceId(bpmBusinessProcess.getProcInstId());
            variableInstance1.setTaskId(null);

            VariableInstanceEntity variableInstance2 = VariableInstanceEntity.create("nrOfActiveInstances", new IntegerType(), hisSize);
            variableInstance2.setId(idGenerator.getNextId());
            variableInstance2.setExecutionId(newId);
            variableInstance2.setProcessInstanceId(bpmBusinessProcess.getProcInstId());
            variableInstance2.setTaskId(null);

            taskMgmtMapper.bulkInsertVariableInstance(Lists.newArrayList(variableInstance,variableInstance1,variableInstance2));
        }
    }
}