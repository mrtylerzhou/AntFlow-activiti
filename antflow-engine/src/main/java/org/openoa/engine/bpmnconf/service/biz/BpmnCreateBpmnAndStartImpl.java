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
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.service.ProcessModelServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnBizCustomService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnCreateBpmnAndStart;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessForwardService;
import org.openoa.base.util.AFWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
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
    private BpmBusinessProcessService bpmBusinessProcessService;

    @Autowired
    private BpmProcessForwardService processForwardService;
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
                .tenantId(MultiTenantUtil.getCurrentTenantId())
                .addBpmnModel(StringUtils.join(bpmnConfCommonVo.getProcessNum(), ".bpmn"), model)
                .name(StringUtils.join(bpmnConfCommonVo.getProcessNum(), " deployment"))
                .deploy();

        // 4. Start a process instance
        ProcessInstance processInstance =runtimeService
                .startProcessInstanceByKeyAndTenantId(bpmnConfCommonVo.getProcessNum(),bpmnStartConditions.getEntryId(),startParamMap, MultiTenantUtil.getCurrentTenantId());
        runtimeService.setVariable(processInstance.getId(), StringConstants.ActVarKeys.PROCINSTID,processInstance.getId());
        Map<String,Object> extraRuntimeVariables=new HashMap<>();
        extraRuntimeVariables.put(StringConstants.ActVarKeys.Is_OUTSIDEPROC,bpmnStartConditions.getIsOutSideAccessProc());
        extraRuntimeVariables.put(StringConstants.ActVarKeys.PROCINSTID,processInstance.getId());
        extraRuntimeVariables.put(StringConstants.ActVarKeys.BPMN_NAME,bpmnConfCommonVo.getBpmnName());
        runtimeService.setVariables(processInstance.getId(),extraRuntimeVariables);

        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBaseMapper().selectOne(
                new QueryWrapper<BpmBusinessProcess>()
                        .eq("ENTRY_ID", bpmnStartConditions.getEntryId()));
        if(Boolean.TRUE.equals(bpmnStartConditions.getIsMigration())){
             bpmBusinessProcess = bpmBusinessProcessService.getBaseMapper().selectOne(
                   AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                            .eq(BpmBusinessProcess::getBusinessNumber, bpmnStartConditions.getProcessNum()));
            String procInstId = bpmBusinessProcess.getProcInstId();
            runtimeService.deleteProcessInstance(procInstId,"migration");
        }
        if (!ObjectUtils.isEmpty(bpmBusinessProcess) && bpmBusinessProcess.getId()!=null) {
            bpmBusinessProcessService.updateById(BpmBusinessProcess
                    .builder()
                    .id(bpmBusinessProcess.getId())
                    .procInstId(processInstance.getId())
                    .build());

            String procInstId=processInstance.getId();
            String processNumber=bpmBusinessProcess.getBusinessNumber();


        }


        //get the first task and complete it
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskTenantId(MultiTenantUtil.getCurrentTenantId()).list();
        if (!ObjectUtils.isEmpty(tasks)) {
            Task task = tasks.get(0);
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, SecurityUtils.getLogInEmpName());
            taskService.complete(task.getId(),varMap);

            //执行自定义业务逻辑
            Collection<BpmnBizCustomService> beans = SpringBeanUtils.getBeans(BpmnBizCustomService.class);
            if (!ObjectUtils.isEmpty(beans)) {
                for (BpmnBizCustomService bean : beans) {
                    bean.execute(task);
                }
            }

        }

    }
}
