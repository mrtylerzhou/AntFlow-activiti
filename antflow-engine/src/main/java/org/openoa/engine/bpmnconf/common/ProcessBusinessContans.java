package org.openoa.engine.bpmnconf.common;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ProcessRecordInfoVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessForward;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class ProcessBusinessContans extends ProcessServiceFactory {
    @Autowired
    private BpmProcessForwardServiceImpl processForwardService;


    @Autowired
    private HistoryService historyService;






    /**
     * query process record info
     */
    public ProcessRecordInfoVo processInfo(BpmBusinessProcess bpmBusinessProcess) {
        ProcessRecordInfoVo processInfoVo = new ProcessRecordInfoVo();
        if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
            //数据权限
            if (!this.showProcessData(bpmBusinessProcess.getBusinessNumber())) {
                throw new JiMuBizException("00", "current user has no access right！");
            }
            //set task state
            processInfoVo.setTaskState(ProcessStateEnum.getDescByCode(bpmBusinessProcess.getProcessState()));
            //process's verify info
            processInfoVo.setVerifyInfoList(verifyInfoService.verifyInfoList(bpmBusinessProcess));
            //发起人信息
            processInfoVo.setProcessTitle(bpmBusinessProcess.getDescription());

            //todo info set emp
            processInfoVo.setCreateTime(bpmBusinessProcess.getCreateTime());
            //set start userId
            processInfoVo.setStartUserId(bpmBusinessProcess.getCreateUser());
            //set process Number
            processInfoVo.setProcessNumber(bpmBusinessProcess.getBusinessNumber());
            String processInstanceId = taskMgmtMapper.findByBusinessId(bpmBusinessProcess.getEntryId());
            //modify forward data
            processForwardService.updateProcessForward(BpmProcessForward.builder()
                    .processInstanceId(processInstanceId)
                    .forwardUserId(SecurityUtils.getLogInEmpIdStr())
                    .build());
            //modify notice
            //todo
            //userMessageService.readNode(processInstanceId);
            List<Task> list = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).taskAssignee(SecurityUtils.getLogInEmpId().toString()).list();
            // Task task = taskService.createTaskQuery().processInstanceBusinessKey(bpmBusinessProcess.getEntryId()).taskAssignee(OASecurityUtils.getLoginedEmployee().getUserId().toString()).singleResult();
            if (!ObjectUtils.isEmpty(list)) {
                processInfoVo.setTaskId(list.get(0).getId());
                processInfoVo.setNodeId(list.get(0).getTaskDefinitionKey());
            }
        }
        return processInfoVo;
    }


    /**
     * delete process
     */
    public void deleteProcessInstance(String processInstanceId) {
        runtimeService.deleteProcessInstance(processInstanceId, "process ending");
    }



    /**
     * verify current logged in user's data right
     *
     * @param processCode that is process number
     * @return
     */
    public boolean showProcessData(String processCode) {

        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processCode);
        //monitor,view,process's admin,super admin,historical approvers and forward user
        if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
            List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
            List<String> list = taskInstanceList.stream().filter(s -> !ObjectUtils.isEmpty(s)).map(HistoricTaskInstance::getAssignee).collect(Collectors.toList());
            if (list.contains(SecurityUtils.getLogInEmpIdStr())) {
                return true;
            }
            //todo redesign
            return true;
        }
        return true;
    }

    /**
     * app route url build
     *
     * @param processKey process key
     * @param fromCode   form code
     * @return
     * @throws UnsupportedEncodingException
     */
    public String applyRoute(String processKey, String fromCode, boolean isOutSide) {
        String roure = "";
        try {
            String inParameter = URLEncoder.encode("{\"processKey\":\"" + processKey + "\",\"formCode\":\"" + fromCode + "\"}", "UTF-8");
            roure = "oaapp://oa.app/page?param={\"type\":\"H5\",\"pageName\":\"{申请类型}\"," +
                    "\"pageURL\":\"/{申请类型}/{路由}/{工作流入参}\"}";
            if (isOutSide) {
                fromCode = "OUTSIDE_WMA";
            }
            String routePath = "apply";


            roure = StringUtils.replaceEach(roure,
                    new String[]{"{申请类型}", "{路由}", "{工作流入参}"},
                    new String[]{fromCode, routePath, inParameter});
            roure = URLEncoder.encode(roure, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return roure;
    }

    /**
     * check current app version true for current version false for old version,not implemented yet at the moment
     * @return
     */
    public boolean checkAppVersionByCurrentUser() {
        return false;
    }
}
