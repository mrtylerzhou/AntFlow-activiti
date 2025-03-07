package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

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
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoServiceImpl bpmVerifyInfoService;

    public void migrateAndJumpToCurrent(String currentTaskDefKey, BpmBusinessProcess bpmBusinessProcess, BusinessDataVo vo, TripleConsumer<BusinessDataVo,Task,BpmBusinessProcess> tripleConsumer){

        BusinessDataVo submitVo= SerializationUtils.clone(vo);
        submitVo.setIsMigration(true);
        submitVo.setStartUserId(bpmBusinessProcess.getCreateUser());
        submitVo.setBpmnCode(bpmBusinessProcess.getVersion());
        processApprovalService.buttonsOperation(JSON.toJSONString(submitVo),submitVo.getFormCode());
        bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        String procDefIdByInstId = taskMgmtMapper.findProcDefIdByInstId(bpmBusinessProcess.getProcInstId());
        if(StringUtils.isBlank(procDefIdByInstId)){
            throw new JiMuBizException("未能根据流程实例id查找到流程定义id,请检查逻辑!");
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
                    .taskDefinitionKey(activity.getId()).list();
            if (!CollectionUtils.isEmpty(tsks)) {
                Map<String, BpmVerifyInfo> verifyInfoMap =verifyInfoMap=bpmVerifyInfoService.getByProcInstIdAndTaskDefKey(bpmBusinessProcess.getBusinessNumber(), id);
                for (Task tsk : tsks) {
                    if(!CollectionUtils.isEmpty(verifyInfoMap)){
                        BpmVerifyInfo bpmVerifyInfo = verifyInfoMap.get(tsk.getTaskDefinitionKey() + tsk.getAssignee());
                        vo.setStartUserId(tsk.getAssignee());
                        if(bpmVerifyInfo!=null){
                            if(!StringUtils.isEmpty(tsk.getAssigneeName())){
                                vo.setStartUserName(tsk.getAssigneeName());
                            }else{
                                vo.setStartUserName(bpmVerifyInfo.getVerifyUserName());
                            }
                            vo.setApprovalComment(bpmVerifyInfo.getVerifyDesc());
                        }else{
                            if(!StringUtils.isEmpty(tsk.getAssigneeName())){
                                vo.setStartUserName(tsk.getAssigneeName());
                            }
                        }
                    }
                    tripleConsumer.accept(vo,tsk,bpmBusinessProcess);
                }
            }
            if (currentTaskDefKey.equals(id)) {
                currentExecuted = true;
            }
        }
    }
}
