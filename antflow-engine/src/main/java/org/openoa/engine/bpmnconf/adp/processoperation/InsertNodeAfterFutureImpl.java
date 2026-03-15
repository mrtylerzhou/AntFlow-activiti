package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.dto.NodeElementDto;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.AbstractAddOrRemoveFutureAssigneeSerivceImpl;
import org.openoa.engine.bpmnconf.service.flowcontrol.BpmnModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InsertNodeAfterFutureImpl extends AbstractAddOrRemoveFutureAssigneeSerivceImpl implements ProcessOperationAdaptor {
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;

    @Override
    public void doProcessButton(BusinessDataVo vo){
        String processNumber = vo.getProcessNumber();
        String taskDefKey=vo.getTaskDefKey();
        //要添加的人会裂变成节点
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        if(StringUtils.isEmpty(processNumber)){
            throw new AFBizException("流程编号不能为空");
        }
        if (CollectionUtils.isEmpty(userInfos)){
            throw new AFBizException("要添加的节点人员不能为空");
        }
        if(StringUtils.isEmpty(taskDefKey)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr(),"taskDefKey不能为空");
        }
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(null==bpmBusinessProcess){
            throw new AFBizException("未能根据流程编号找到流程信息:"+processNumber);
        }
        String procInstId = bpmBusinessProcess.getProcInstId();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(CollectionUtils.isEmpty(tasks)){
            log.error("流程实例未找到任务信息:{}",procInstId);
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"流程实例未找到任务信息:"+procInstId);
        }
        String procDefId = tasks.get(0).getProcessDefinitionId();
        List<ActivityImpl> activitiList = additionalInfoService.getActivitiList(procDefId);
        if(CollectionUtils.isEmpty(activitiList)){
            throw new RuntimeException("未能根据流程定义id:"+procDefId+"找到流程图");
        }
        List<ActivityImpl> currentActivities = activitiList.stream().filter(a -> a.getId().equals(taskDefKey)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(currentActivities)){
            throw new RuntimeException("未能根据流程节点key:"+taskDefKey+"找到流程图对应节点信息");
        }
        ActivityImpl currentActiviti = currentActivities.get(0);
        ActivityBehavior activityBehavior = currentActiviti.getActivityBehavior();
        if(!(activityBehavior instanceof MultiInstanceActivityBehavior)){
            throw new AFBizException("当前节点不是多实例节点，不支持插入节点");
        }
        String nodeIdByElementId = bpmVariableMultiplayerMapper.getNodeIdByElementId(processNumber, taskDefKey);
        NodeElementDto nodeElementDto = bpmVariableBizService.queryElementIdByNodeIdDetail(processNumber, nodeIdByElementId);
        if(nodeElementDto==null){
            throw new AFBizException("未能根据节点id获取元素Id"+nodeIdByElementId);
        }
        if (activityBehavior instanceof SequentialMultiInstanceBehavior){
            super.modifyFutureAssigneesByProcessInstance(bpmBusinessProcess,nodeElementDto,vo.getUserInfos(),1);
            return;
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        byte[] bytes = converter.convertToXML(bpmnModel);
        String orgXml=new String(bytes);
        try {
            String desXml = BpmnModifier.changeSequential(orgXml, taskDefKey, true);
            taskMgmtMapper.updateResourceBytes(procDefId,desXml.getBytes());
        }catch (Exception e){
            log.error("流程xml转换失败:{}",e.getMessage());
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"流程xml转换失败:"+e.getMessage());
        }
        ProcessEngineConfigurationImpl config =
                (ProcessEngineConfigurationImpl)
                        ProcessEngines.getDefaultProcessEngine()
                                .getProcessEngineConfiguration();

        DeploymentCache deploymentCache = config.getProcessDefinitionCache();
        deploymentCache.remove(procDefId);

        super.modifyFutureAssigneesByProcessInstance(bpmBusinessProcess,nodeElementDto,vo.getUserInfos(),1);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_INSERT_AFTER_FUTURE_NODE);
    }
}
