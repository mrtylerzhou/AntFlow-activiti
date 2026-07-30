package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.TripleConsumer;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVerifyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.MsgProcessEventEnum.PROCESS_SUBMIT;

@Service
public class BpmnProcessMigrationServiceImpl {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessApprovalServiceImpl processApprovalService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoService bpmVerifyInfoService;
    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    public void migrateAndJumpToCurrent(Task currentTask, BpmBusinessProcess bpmBusinessProcess, BusinessDataVo vo, TripleConsumer<BusinessDataVo,Task,BpmBusinessProcess> tripleConsumer){
        migrateAndJumpToCurrent(currentTask, bpmBusinessProcess, vo, tripleConsumer, false);
    }

    /**
     * 迁移并跳转到当前节点
     * @param stopAtParallelFork 如果为true,表示动态条件并行场景:重启后自动完成到网关分叉点,让并行分支自然生成待办
     */
    public void migrateAndJumpToCurrent(Task currentTask, BpmBusinessProcess bpmBusinessProcess, BusinessDataVo vo, TripleConsumer<BusinessDataVo,Task,BpmBusinessProcess> tripleConsumer, boolean stopAtParallelFork){
        String  currentTaskDefKey = currentTask.getTaskDefinitionKey();
        String currentComment=vo.getApprovalComment();
        BusinessDataVo submitVo= JSON.to(BusinessDataVo.class,vo);
        submitVo.setIsMigration(true);
        submitVo.setStartUserId(bpmBusinessProcess.getCreateUser());
        submitVo.setBpmnCode(bpmBusinessProcess.getVersion());
        submitVo.setOperationType(PROCESS_SUBMIT.getCode());
        processApprovalService.buttonsOperation(JSON.toJSONString(submitVo),submitVo.getFormCode());
        bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());

        if (stopAtParallelFork) {
            //动态条件并行:自动完成到网关分叉点,让并行分支自然生成
            autoCompleteToParallelFork(bpmBusinessProcess, vo, tripleConsumer);
            return;
        }

        String procDefIdByInstId = taskMgmtMapper.findProcDefIdByInstId(bpmBusinessProcess.getProcInstId());
        if(StringUtils.isBlank(procDefIdByInstId)){
            throw new AFBizException("未能根据流程实例id查找到流程定义id,请检查逻辑!");
        }
        List<ActivityImpl> activitiList = additionalInfoService.getActivitiList(procDefIdByInstId);
        boolean currentExecuted=false;
        for (ActivityImpl activity : activitiList) {
            if (currentExecuted) {
                break;
            }
            String id = activity.getId();

            // 查找当前流程实例的任务
            List<Task> tsks = taskService.createTaskQuery()
                    .processInstanceId(bpmBusinessProcess.getProcInstId())
                    .taskDefinitionKey(activity.getId())
                    .taskTenantId(MultiTenantUtil.getCurrentTenantId())
                    .list();
            Map<String, BpmVerifyInfo> verifyInfoMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(tsks)) {
                verifyInfoMap= bpmVerifyInfoService.getByProcInstIdAndTaskDefKey(bpmBusinessProcess.getBusinessNumber(), id);
                if (tsks.size() > 1) {
                    List<String> taskNames = tsks.stream().map(TaskInfo::getName).distinct().collect(Collectors.toList());
                    if (taskNames.size() == 1) {
                        List<BpmVariableMultiplayer> bpmVariableMultiplayerList = bpmVariableMultiplayerService.getBaseMapper().isMoreNode(bpmBusinessProcess.getBusinessNumber(), tsks.get(0).getTaskDefinitionKey());
                        if (!CollectionUtils.isEmpty(bpmVariableMultiplayerList) && bpmVariableMultiplayerList.get(0).getSignType() == 2) {
                            if (currentTaskDefKey.equals(id)) {
                                tsks = tsks.stream().filter(a -> a.getAssignee().equals(currentTask.getAssignee())).collect(Collectors.toList());
                            } else {
                                List<String> verifyUserIds = verifyInfoMap.values().stream().map(BpmVerifyInfo::getVerifyUserId).collect(Collectors.toList());
                                tsks = tsks.stream().filter(a -> verifyUserIds.contains(a.getAssignee())).collect(Collectors.toList());
                            }
                        }
                    }
                }

                for (Task tsk : tsks) {
                    if (!CollectionUtils.isEmpty(verifyInfoMap)) {
                        BpmVerifyInfo bpmVerifyInfo = verifyInfoMap.get(tsk.getTaskDefinitionKey() + tsk.getAssignee());
                        vo.setStartUserId(tsk.getAssignee());
                        if (bpmVerifyInfo != null) {
                            if (!StringUtils.isEmpty(tsk.getAssigneeName())) {
                                vo.setStartUserName(tsk.getAssigneeName());
                            } else {
                                vo.setStartUserName(bpmVerifyInfo.getVerifyUserName());
                            }
                            vo.setApprovalComment(StringConstants.CURRENT_USER_ALREADY_PROCESSED);
                        } else {
                            if (!StringUtils.isEmpty(tsk.getAssigneeName())) {
                                vo.setStartUserName(tsk.getAssigneeName());
                            }
                            if(currentTaskDefKey.equals(tsk.getTaskDefinitionKey())){
                                vo.setApprovalComment(currentComment);
                            }
                        }
                    } else {
                        vo.setStartUserName(tsk.getAssigneeName());
                        if(currentTaskDefKey.equals(tsk.getTaskDefinitionKey())){
                            vo.setApprovalComment(currentComment);
                        }
                    }
                    tripleConsumer.accept(vo, tsk, bpmBusinessProcess);
                }
            }
            if (currentTaskDefKey.equals(id)) {
                currentExecuted = true;
            }
        }
    }

    /**
     * 动态条件并行迁移:自动完成到网关分叉点,让并行分支自然生成待办
     * 逻辑:重启后如果已有多个任务(网关已分叉)则停止;否则逐个完成单任务直到网关评估产生多分支
     */
    private void autoCompleteToParallelFork(BpmBusinessProcess bpmBusinessProcess, BusinessDataVo vo, TripleConsumer<BusinessDataVo,Task,BpmBusinessProcess> tripleConsumer) {
        int maxIterations = 50;
        for (int i = 0; i < maxIterations; i++) {
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(bpmBusinessProcess.getProcInstId())
                    .taskTenantId(MultiTenantUtil.getCurrentTenantId())
                    .list();
            if (CollectionUtils.isEmpty(tasks)) {
                break;
            }
            //多个任务存在 → 网关已分叉,并行分支待办已生成,停止
            if (tasks.size() > 1) {
                break;
            }
            //单任务:自动完成它,然后检查网关是否分叉
            Task task = tasks.get(0);
            vo.setStartUserId(task.getAssignee());
            vo.setStartUserName(task.getAssigneeName());
            vo.setApprovalComment(StringConstants.CURRENT_USER_ALREADY_PROCESSED);
            tripleConsumer.accept(vo, task, bpmBusinessProcess);
        }
    }
}
