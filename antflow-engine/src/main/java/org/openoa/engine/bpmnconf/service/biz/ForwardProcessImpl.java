package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.confentity.BpmProcessForward;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 流程转发
 */
@Component
public class ForwardProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmProcessForwardServiceImpl processForwardService;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        if (bpmBusinessProcess != null) {

            vo.getUserInfos().stream().forEach(o -> {
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
