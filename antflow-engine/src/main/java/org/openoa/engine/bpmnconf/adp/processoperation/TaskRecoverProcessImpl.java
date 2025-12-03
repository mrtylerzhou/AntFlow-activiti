package org.openoa.engine.bpmnconf.adp.processoperation;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.variable.*;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.AFExecutionEntity;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class TaskRecoverProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private TaskMgmtServiceImpl taskMgmtService;


    @Override
    public void doProcessButton(BusinessDataVo vo)  {
        String processNumber=vo.getProcessNumber();
        String taskDefKey = vo.getTaskDefKey();
        if(!StringUtils.hasText(processNumber)){
            throw new AFBizException("请输入流程编号");
        }
        if(!StringUtils.hasText(taskDefKey)){
            throw new AFBizException("请输入流程taskDefKey");
        }
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(bpmBusinessProcess==null){
            throw  new AFBizException(Strings.lenientFormat("未能根据流程编号:%s找到流程信息",processNumber));
        }
        List<HistoricTaskInstance> hisInstances = processEngine
                .getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(bpmBusinessProcess.getProcInstId())
                .taskDefinitionKey(taskDefKey)
                .list();

        if (hisInstances == null) {
            throw new AFBizException("指定流程实例不存在");
        }


        IdGenerator idGenerator = ((ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration()).getIdGenerator();
        taskMgmtService.insertExecution(idGenerator, hisInstances, bpmBusinessProcess);


        List<HistoricIdentityLink> hstIdentityLinks = historyService.getHistoricIdentityLinksForProcessInstance(bpmBusinessProcess.getProcInstId());
        List<IdentityLinkEntity> identityLinkEntities = new ArrayList<>();
        for (HistoricIdentityLink historicIdentityLink : hstIdentityLinks) {
            IdentityLinkEntity identityLink = new IdentityLinkEntity();
            identityLink.setId(idGenerator.getNextId());
            identityLink.setType(historicIdentityLink.getType());
            identityLink.setUserId(historicIdentityLink.getUserId());
            identityLink.setTaskId(historicIdentityLink.getTaskId());
            identityLink.setProcessInstanceId(historicIdentityLink.getProcessInstanceId());
            identityLinkEntities.add(identityLink);
        }


        taskMgmtMapper.bulkInsertIdentityLink(identityLinkEntities);

        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(bpmBusinessProcess.getProcInstId())
                .activityType("endEvent")
                .list();

        taskMgmtMapper.deleteHisActInst(historicActivityInstanceList.get(0));

    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_RECOVER_TO_HIS);
    }
}

