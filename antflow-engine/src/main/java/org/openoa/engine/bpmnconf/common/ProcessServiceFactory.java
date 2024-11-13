package org.openoa.engine.bpmnconf.common;

import org.activiti.engine.*;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.bpmnconf.mapper.BpmTaskconfigMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.NotifyServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.TemplateMgmtServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * process service factory
 * @author ：AntFlow
 * @since 0.5
 */
@Component
public class ProcessServiceFactory {
    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected FormService formService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected BpmFlowruninfoServiceImpl bpmFlowruninfoMapper;

    @Autowired
    protected BpmVerifyInfoServiceImpl verifyInfoService;

    @Autowired
    protected BpmTaskconfigMapper bpmTaskconfigMapper;

    @Autowired
    protected ProcessConstants processConstants;

    @Autowired
    protected NotifyServiceImpl notifyService;

    @Autowired
    protected BpmBusinessProcessMapper bpmBusinessProcessMapper;

    @Autowired
    protected BpmTaskconfigServiceImpl bpmTaskconfigService;


    @Autowired
    protected BpmFlowrunEntrustServiceImpl entrustService;

    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    protected TemplateMgmtServiceImpl templateMgmtService;


    @Autowired
    protected TaskMgmtMapper taskMgmtMapper;

    @Autowired
    protected ProcessEngine processEngine;


    @Autowired
    protected TaskMgmtServiceImpl taskMgmtService;
    @Autowired
    protected UserMessageServiceImpl userMessageService;
}