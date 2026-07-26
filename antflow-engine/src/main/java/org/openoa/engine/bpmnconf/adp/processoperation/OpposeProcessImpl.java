package org.openoa.engine.bpmnconf.adp.processoperation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnNodeBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * arbitration sign oppose handler.
 * when an approver opposes in an arbitration-sign node, delete the approver's task and record verify info.
 * if the oppose count reaches the threshold M = ceil(n * (100 - ratio) / 100), terminate the process.
 */
@Slf4j
@Component
public class OpposeProcessImpl implements ProcessOperationAdaptor {
    private static final int OPPOSE_VERIFY_STATUS = 7;
    private static final int DEFAULT_ARBITRATION_RATIO = 100;

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmVerifyInfoMapper bpmVerifyInfoMapper;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private EndProcessImpl endProcessImpl;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;
    @Autowired
    private BpmnNodeBizService bpmnNodeBizService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        if (ObjectUtils.isEmpty(vo.getTaskId())) {
            throw new AFBizException("当前流程节点等于空！");
        }
        Task task = taskService.createTaskQuery()
                .taskId(vo.getTaskId())
                .singleResult();
        if (ObjectUtils.isEmpty(task)) {
            throw new AFBizException("当前流程节点不存在或已被处理！");
        }
        String taskDefKey = task.getTaskDefinitionKey();
        String taskName = task.getName();
        String processInstanceId = task.getProcessInstanceId();

        //1. delete current approver's task
        taskMgmtMapper.deletTask(vo.getTaskId());

        //2. record verify info (verify_status=7, oppose)
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        String verifyUserName = StringUtils.EMPTY;
        String verifyUserId = StringUtils.EMPTY;
        if (Boolean.TRUE.equals(vo.getIsOutSideAccessProc())) {
            Map<String, Object> objectMap = vo.getObjectMap();
            if (!CollectionUtils.isEmpty(objectMap)) {
                verifyUserName = Optional.ofNullable(objectMap.get("employeeName")).map(String::valueOf).orElse(StringUtils.EMPTY);
                verifyUserId = Optional.ofNullable(objectMap.get("employeeId")).map(Object::toString).orElse(StringUtils.EMPTY);
            }
        } else {
            verifyUserName = SecurityUtils.getLogInEmpName();
            verifyUserId = SecurityUtils.getLogInEmpIdStr();
        }
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .businessId(bpmBusinessProcess.getBusinessId())
                .verifyUserId(verifyUserId)
                .verifyUserName(verifyUserName)
                .verifyStatus(OPPOSE_VERIFY_STATUS)
                .verifyDate(new Date())
                .processCode(vo.getProcessNumber())
                .verifyDesc(vo.getApprovalComment())
                .taskName(taskName)
                .taskId(vo.getTaskId())
                .taskDefKey(taskDefKey)
                .runInfoId(processInstanceId)
                .build());

        //3. count oppose records for current node
        LambdaQueryWrapper<BpmVerifyInfo> opposeWrapper = new LambdaQueryWrapper<BpmVerifyInfo>()
                .eq(BpmVerifyInfo::getProcessCode, vo.getProcessNumber())
                .eq(BpmVerifyInfo::getTaskDefKey, taskDefKey)
                .eq(BpmVerifyInfo::getVerifyStatus, OPPOSE_VERIFY_STATUS);
        Long opposeCount = bpmVerifyInfoMapper.selectCount(opposeWrapper);

        //4. get arbitration ratio from node config
        Integer ratio = DEFAULT_ARBITRATION_RATIO;
        try {
            BpmnNodeVo bpmnNodeVo = bpmnNodeBizService.getBpmnNodeVoByTaskDefKey(vo.getProcessNumber(), taskDefKey);
            BpmnNodePropertysVo property = bpmnNodeVo == null ? null : bpmnNodeVo.getProperty();
            if (property != null && property.getArbitrationRatio() != null) {
                ratio = property.getArbitrationRatio();
            }
        } catch (Exception e) {
            log.warn("failed to get arbitration ratio for processNumber={}, taskDefKey={}", vo.getProcessNumber(), taskDefKey, e);
        }

        //5. get total personnel count n
        List<BpmVariableMultiplayer> multiplayerList = bpmVariableMultiplayerMapper.isMoreNode(vo.getProcessNumber(), taskDefKey);
        long n = 0L;
        if (!CollectionUtils.isEmpty(multiplayerList)) {
            Long variableMultiplayerId = multiplayerList.get(0).getId();
            LambdaQueryWrapper<BpmVariableMultiplayerPersonnel> personnelWrapper = new LambdaQueryWrapper<BpmVariableMultiplayerPersonnel>()
                    .eq(BpmVariableMultiplayerPersonnel::getVariableMultiplayerId, variableMultiplayerId)
                    .eq(BpmVariableMultiplayerPersonnel::getIsDel, 0);
            n = bpmVariableMultiplayerPersonnelService.count(personnelWrapper);
        }

        //6. calculate oppose threshold M = ceil(n * (100 - ratio) / 100)
        int m = (int) Math.ceil(n * (100 - ratio) / 100.0);

        log.info("arbitration oppose: processNumber={}, taskDefKey={}, opposeCount={}, n={}, ratio={}, M={}",
                vo.getProcessNumber(), taskDefKey, opposeCount, n, ratio, m);

        //7. if opposeCount >= M, terminate the process
        if (opposeCount != null && opposeCount >= m) {
            vo.setFlag(false);
            endProcessImpl.endProcessWithoutVerify(vo);
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_OPPOSE);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_OPPOSE);
    }
}
