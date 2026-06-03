package org.openoa.engine.bpmnconf.service.biz;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.common.ProcessConstants;
import org.openoa.engine.bpmnconf.mapper.*;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmTaskconfigService;
import org.openoa.engine.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Slf4j
@Service
@Transactional
public class NotifyServiceImpl {


    @Autowired
    private TaskMgmtMapper taskMgmtMapper;

    @Autowired
    private ProcessConstants processConstants;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AfUserService employeeService;

    @Autowired
    private BpmFlowrunEntrustMapper bpmFlowrunEntrustMapper;


    @Autowired
    private BpmTaskconfigMapper bpmTaskconfigMapper;

    @Autowired
    private BpmBusinessProcessMapper bpmBusinessProcessMapper;
    @Autowired
    private BpmTaskconfigService bpmTaskconfigService;
    //todo list detail page
    private static final String PROC_TO_DO = "/main/tab?tabKey=examine&menuCode=approval&subMenuCode=pending";

    /**
     * process end notice
     *
     * @param entryId
     */
    public void endNotify(String entryId) {
        try {
            TaskMgmtVO taskMgmtVO = taskMgmtMapper.findByEntryId(entryId);
            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());
            String procDefId = taskMgmtVO.getProcessName().split(":")[0];
            //流程名称
            String processName = taskMgmtMapper.getProcessName(procDefId);
            String code = taskMgmtVO.getProcessNumber().split("_")[0];
            String title = "您的" + processName + bpmBusinessProcess.getBusinessNumber() + "已完成。";
            //String content="您的"+processName+bpmBusinessProcess.getBusinessNumber()+bpmBusinessProcess.getDescription()+" 已完成。";
            UrlParams urlParams = new UrlParams();
            urlParams.setBusinessId(entryId.split(":")[1]);
            urlParams.setCode(code);
            Map<String, Object> map = bpmTaskconfigMapper.findByAppRoute(procDefId, null, "test");
            String appUrl = StringUtils.join(processConstants.getMapValue(map, "routeUrl"), entryId.split(":")[1]);
            SendParam sendParam = SendParam.builder()
                    .appUrl(appUrl)
                    .userId(taskMgmtVO.getApplyUser())
                    .urlParams(urlParams)
                    .params("check")
                    .title(title)
                    .content(title)
                    .build();
            MessageUtils.sendMessage(sendParam);
        } catch (Exception e) {
            log.error("process finish notice error", e);
        }
    }



    /**
     * process canceled notice
     *
     * @param entryId
     */
    public void cancelNotify(String entryId) {
        try {
            TaskMgmtVO taskMgmtVO = taskMgmtMapper.findByEntryId(entryId);

            BaseIdTranStruVo startUser = employeeService.getById(taskMgmtVO.getApplyUser());
            //get process def id
            String procDefId = taskMgmtVO.getProcessName().split(":")[0];
            //process's name
            String processName = taskMgmtMapper.getProcessName(procDefId);
            //code
            String code = taskMgmtVO.getProcessNumber().split("_")[0];
            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());
            String title = "您参与的" + processName + bpmBusinessProcess.getBusinessNumber() + "已被" + startUser.getName() + "撤销。";
            //String content="您参与的"+processName+bpmBusinessProcess.getBusinessNumber()+bpmBusinessProcess.getDescription()+"已被"+startUser.getName() +"撤销。";
            UrlParams urlParams = new UrlParams();
            urlParams.setBusinessId(entryId.split(":")[1]);
            urlParams.setCode(code);
            Map<String, Object> map = bpmTaskconfigMapper.findByAppRoute(procDefId, null, "test");
            String appUrl = StringUtils.join(processConstants.getMapValue(map, "routeUrl"), entryId.split(":")[1]);
            //经办人列表
            List<String> assignees = taskMgmtMapper.getAssigneesByProcInstId(bpmBusinessProcess.getProcInstId());
            if (!ObjectUtils.isEmpty(assignees)) {
                for (String o : assignees) {
                    if (!Strings.isNullOrEmpty(o) && !o.equals(ProcessEnum.PROC_MAN.getDesc())) {
                        SendParam sendParam = SendParam.builder()
                                .params("check")
                                .appUrl(appUrl)
                                .urlParams(urlParams)
                                .userId(o)
                                .title(title)
                                .content(title)
                                .build();
                        MessageUtils.sendMessage(sendParam);
                    }
                }
            }
        } catch (Exception e) {
            log.error("撤销通知出错", e);
        }
    }

    /**
     * 待处理添加代办URl地址
     * add url to do list
     *
     * @param bpmBusinessProcess
     * @return
     */
//todo
    public String getUrl(BpmBusinessProcess bpmBusinessProcess, DelegateTask task) {
        String processCode = bpmBusinessProcess.getBusinessNumber().split("_")[0];
        String url = "";
        if (ObjectUtils.isEmpty(task)) {
            url = "/user/workflow/Upcoming/apply/" + processCode + "/" + bpmBusinessProcess.getBusinessId();
        } else {
            Map<String, Object> map = bpmTaskconfigService.findTaskNodeType(task.getTaskDefinitionKey());
            url = "/user/workflow/Upcoming/check/" + processCode + "/" + bpmBusinessProcess.getBusinessId() + "/" + processConstants.getMapValue(map, "nodeType") + "/" + task.getId();
        }
        /*if(!processCode.equals(Contants.ENTRY_NUMBER)){
        }*/
        log.info("**************组装连接：" + url + "*********************");
        return url;
    }

    /**
     * to do list notification
     * notify current approver
     *
     * @param entryId
     */
    public void notifyCurrent(String assignee, String entryId, DelegateTask task) {

        try {
            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());
            String procDefId = bpmBusinessProcess.getProcessinessKey();
            //process number
            String processNumber = bpmBusinessProcess.getBusinessNumber();
            //process name
            String processName = taskMgmtMapper.getProcessName(procDefId);
            //Thread.sleep(1000*30);
            SendParam sendParam = new SendParam();
            UrlParams urlParams = new UrlParams();
            if (Strings.isNullOrEmpty(assignee) || assignee.equals(ProcessEnum.PROC_MAN.getDesc())) {
                //set process admin
                Map<Integer, List<Integer>> map = null;// todo get process admin
                Integer procType = Optional.ofNullable(taskMgmtMapper.getProcType(procDefId)).orElse(-1);
                List<Integer> assignees = map.get(procType);
                if (ObjectUtils.isEmpty(assignees)) {
                    log.error("current has no admin！");
                } else {
                    List<SendParam> sendParamList = new ArrayList<>();
                    assignees.forEach(o -> {
                        SendParam send = new SendParam();
                        //current node has no approver
                        String title = processName + processNumber + "无人处理，请至流程管理中进行干预。";
                        send.setTitle("您有1个" + processName + processNumber + "无人处理，请至流程管理中进行干预。");
                        send.setContent(title);
                        send.setParams("workflowList");
                        send.setUrlParams(urlParams);
                        send.setUrl("/manage/setting/work-process/process-manage");
                        send.setUserId(o.toString());
                        send.setNode(task.getId());
                        sendParamList.add(send);
                    });
                    MessageUtils.sendMessageBatch(sendParamList);
                }
            } else {
                String url = this.getUrl(bpmBusinessProcess, task);
                //has approver's condition
                sendParam.setUserId(assignee);
                sendParam.setTitle("您有1个" + bpmBusinessProcess.getDescription() + "需要处理。");
                sendParam.setContent("您有1个" + processName + processNumber + bpmBusinessProcess.getDescription() + "需要处理。");
                sendParam.setUrl(url);
                //current task id
                if (!ObjectUtils.isEmpty(task)) {
                    sendParam.setNode(task.getId());
                }

                //to do list
                sendParam.setAppUrl(PROC_TO_DO);
                sendParam.setParams("myTask");
                sendParam.setUrlParams(urlParams);
                MessageUtils.sendMessage(sendParam);

            }
        } catch (Exception e) {
            log.error("sent notification error", e);
        }

    }

    /**
     * process flow notification
     *
     * @param procInstId
     */
    public void passNotify(String procInstId, String entryId) {
        try {
            //get forwarded user list
            List<Integer> bpmFlowrunEntrusts = bpmFlowrunEntrustMapper.getCirculated(procInstId);

            if (ObjectUtils.isEmpty(bpmFlowrunEntrusts)) {
                return;
            }

            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());

            String procDefId = bpmBusinessProcess.getProcessinessKey();
            //process number
            String processNumber = bpmBusinessProcess.getBusinessNumber();
            //process name
            String processName = taskMgmtMapper.getProcessName(procDefId);
            String code = processNumber.split("_")[0];
            String title = "您关注的" + processName + processNumber + "已有新的进展。";

            UrlParams urlParams = new UrlParams();
            urlParams.setBusinessId(entryId.split(":")[1]);
            urlParams.setCode(code);

            Map<String, Object> map = bpmTaskconfigMapper.findByAppRoute(procDefId, null, "test");
            String appUrl = StringUtils.join(processConstants.getMapValue(map, "routeUrl"), entryId.split(":")[0]);

            bpmFlowrunEntrusts.forEach(o -> {
                SendParam sendParam = SendParam.builder()
                        .userId(o.toString())
                        .params("check")
                        .appUrl(appUrl)
                        .urlParams(urlParams)
                        .title(title)
                        .content(title)
                        .build();
                MessageUtils.sendMessage(sendParam);
            });
        } catch (Exception e) {
            log.error("流转通知出错", e);
        }
    }


}
