package org.openoa.engine.bpmnconf.service.biz;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.service.ProcessModelServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class BpmnCreateBpmnAndStartImpl implements BpmnCreateBpmnAndStart {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private BpmProcessForwardServiceImpl processForwardService;
    @Autowired
    private ProcessModelServiceImpl processModelService;;

    @Override
    public void createBpmnAndStart(BpmnConfCommonVo bpmnConfCommonVo, BpmnStartConditionsVo bpmnStartConditions) {

        //start param map
        Map<String, Object> startParamMap = Maps.newHashMap();


        //set entry id
        startParamMap.put("entryId", bpmnStartConditions.getEntryId());


        //set business id
        startParamMap.put("businessId", bpmnStartConditions.getBusinessId());


        //set bpmncode
        startParamMap.put("bpmnCode", bpmnConfCommonVo.getBpmnCode());


        //set form code
        startParamMap.put("formCode", bpmnConfCommonVo.getFormCode());


        //set process number
        startParamMap.put("processNumber", bpmnConfCommonVo.getProcessNum());

        // Setup the user
        Authentication.setAuthenticatedUserId(bpmnStartConditions.getStartUserId());

        // 1. Build up the model from scratch
        BpmnModel model = new BpmnModel();
        processModelService.execute(bpmnConfCommonVo, bpmnStartConditions, startParamMap, model);

        // 3. Deploy the process to the engine
        repositoryService.createDeployment()
                .addBpmnModel(StringUtils.join(bpmnConfCommonVo.getProcessNum(), ".bpmn"), model)
                .name(StringUtils.join(bpmnConfCommonVo.getProcessNum(), " deployment"))
                .deploy();

        // 4. Start a process instance
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmnConfCommonVo.getProcessNum(), bpmnStartConditions.getEntryId(),
                startParamMap);


        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBaseMapper().selectOne(
                new QueryWrapper<BpmBusinessProcess>()
                        .eq("ENTRY_ID", bpmnStartConditions.getEntryId()));
        if (!ObjectUtils.isEmpty(bpmBusinessProcess) && bpmBusinessProcess.getId()!=null) {
            bpmBusinessProcessService.updateById(BpmBusinessProcess
                    .builder()
                    .id(bpmBusinessProcess.getId())
                    .procInstId(processInstance.getId())
                    .build());

            String procInstId=processInstance.getId();
            String processNumber=bpmBusinessProcess.getBusinessNumber();
            List<String> empToForwardList = bpmnStartConditions.getEmpToForwardList();
            bpmBusinessProcessService.updateById(BpmBusinessProcess
                    .builder()
                    .id(bpmBusinessProcess.getId())
                    .procInstId(procInstId)
                    .build());
            processForwardService.addProcessForwardBatch(procInstId,processNumber,empToForwardList);
        }


        //get the first task and complete it
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        if (!ObjectUtils.isEmpty(tasks)) {
            Task task = tasks.get(0);
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, SecurityUtils.getLogInEmpName());
            taskService.complete(task.getId(),varMap);
        }

    }
}
