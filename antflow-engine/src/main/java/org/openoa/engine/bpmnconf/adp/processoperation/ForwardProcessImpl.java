package org.openoa.engine.bpmnconf.adp.processoperation;

import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.entity.BpmProcessForward;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 流程转发
 */
@Component
public class ForwardProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    protected BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmProcessForwardService processForwardService;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        if (bpmBusinessProcess != null) {

            vo.getUserInfos().forEach(o -> {
                processForwardService.addProcessForward(BpmProcessForward.builder()
                        .createTime(new Date())
                        .createUserId(SecurityUtils.getLogInEmpId())
                        .forwardUserId(o.getId())
                        .ForwardUserName(o.getName())
                        .processInstanceId(bpmBusinessProcess.getProcInstId())
                        .processNumber(vo.getProcessNumber())
                        .build());
            });
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_FORWARD);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),  ProcessOperationEnum.BUTTON_TYPE_FORWARD);
    }
}
