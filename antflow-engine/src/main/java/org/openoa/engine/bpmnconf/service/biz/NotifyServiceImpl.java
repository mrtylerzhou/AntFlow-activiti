package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.Employee;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.OperationResp;
import org.openoa.base.vo.SendParam;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.vo.UrlParams;
import org.openoa.engine.bpmnconf.common.ProcessContans;
import org.openoa.engine.bpmnconf.confentity.BpmManualNotify;
import org.openoa.engine.bpmnconf.mapper.*;
import org.openoa.engine.bpmnconf.service.impl.BpmTaskconfigServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.EmployeeServiceImpl;
import org.openoa.engine.bpmnconf.util.MessageUtils;
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
    private BpmManualNotifyMapper bpmManualNotifyMapper;

    @Autowired
    private TaskMgmtMapper taskMgmtMapper;

    @Autowired
    private ProcessContans processContans;

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private BpmFlowrunEntrustMapper bpmFlowrunEntrustMapper;


    @Autowired
    private BpmTaskconfigMapper bpmTaskconfigMapper;

    @Autowired
    private BpmBusinessProcessMapper bpmBusinessProcessMapper;
    @Autowired
    private BpmTaskconfigServiceImpl bpmTaskconfigService;
    //remind manually
    private static final long ALLOWED = 2 * 60 * 60;
    //代办详情页路由
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
            String appUrl = StringUtils.join(processContans.getMapValue(map, "routeUrl"), entryId.split(":")[1]);
            SendParam sendParam = SendParam.builder()
                    .appUrl(appUrl)
                    .userId(Long.parseLong(taskMgmtVO.getApplyUser()))
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

            Employee startUser = employeeService.qryLiteEmployeeInfoById(Long.parseLong(taskMgmtVO.getApplyUser()));
            //get process def id
            String procDefId = taskMgmtVO.getProcessName().split(":")[0];
            //process's name
            String processName = taskMgmtMapper.getProcessName(procDefId);
            //code
            String code = taskMgmtVO.getProcessNumber().split("_")[0];
            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());
            String title = "您参与的" + processName + bpmBusinessProcess.getBusinessNumber() + "已被" + startUser.getUsername() + "撤销。";
            //String content="您参与的"+processName+bpmBusinessProcess.getBusinessNumber()+bpmBusinessProcess.getDescription()+"已被"+startUser.getName() +"撤销。";
            UrlParams urlParams = new UrlParams();
            urlParams.setBusinessId(entryId.split(":")[1]);
            urlParams.setCode(code);
            Map<String, Object> map = bpmTaskconfigMapper.findByAppRoute(procDefId, null, "test");
            String appUrl = StringUtils.join(processContans.getMapValue(map, "routeUrl"), entryId.split(":")[1]);
            //经办人列表
            List<String> assignees = taskMgmtMapper.getAssigneesByEntryId(entryId);
            if (!ObjectUtils.isEmpty(assignees)) {
                for (String o : assignees) {
                    if (!Strings.isNullOrEmpty(o) && !o.equals(ProcessEnum.PROC_MAN.getDesc())) {
                        SendParam sendParam = SendParam.builder()
                                .params("check")
                                .appUrl(appUrl)
                                .urlParams(urlParams)
                                .userId(Long.parseLong(o))
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
     * notify manually
     *
     * @param code       CGSQ
     * @param businessId 18
     */
    public String notifyByHand(String code, Long businessId) {
        String rst = "提醒成功";
        BpmManualNotify bpmManualNotify = BpmManualNotify.builder()
                .code(code)
                .businessId(businessId)
                .build();
        List<BpmManualNotify> list = bpmManualNotifyMapper.selectList(new QueryWrapper<BpmManualNotify>()
                .eq("code", code)
                .eq("business_id", businessId));
        String entryId = taskMgmtMapper.getEntryId(code, businessId);

        if (ObjectUtils.isEmpty(list)) {
            //无提醒记录
            this.manualNotifyHelper(entryId);
            bpmManualNotifyMapper.insert(bpmManualNotify);
        } else {
            BpmManualNotify existedNotify = list.get(0);
            Date lastTime = existedNotify.getLastTime();
            Date now = new Date();
            long offset = (now.getTime() - lastTime.getTime()) / 1000;
            if (offset < ALLOWED) {
                //未到时间
                long deviation = ALLOWED - offset;
                long hour = deviation % (24 * 3600) / 3600;
                long minute = deviation % 3600 / 60;
                rst = "请于" + hour + "小时" + minute + "分钟后再提醒!";
                throw new JiMuBizException(OperationResp.FAILURE.getCode(), rst);
            }

            // time is overdue
            this.manualNotifyHelper(entryId);
            //更新通知表时间
            //update notice last time
            existedNotify.setLastTime(new Date());
            bpmManualNotifyMapper.updateById(existedNotify);
        }
        return rst;
    }

    /**
     * manually notice helper
     *
     * @param entryId
     */
    public void manualNotifyHelper(String entryId) {
        try {
            String startUser = SecurityUtils.getLogInEmpNameSafe();

            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());

            //process Number
            String processNumber = bpmBusinessProcess.getBusinessNumber();
            //process Name
            String procDefId = bpmBusinessProcess.getProcessinessKey();
            String processName = taskMgmtMapper.getProcessName(procDefId);
            //{发起人}提醒您尽快处理{流程名称}{流程编号}
            String title = startUser + "提醒您尽快处理" + processName + processNumber + "。";
            //String content=startUser+"提醒您尽快处理"+processName+processNumber+bpmBusinessProcess.getDescription()+"。";
            //当前处理人列表
            List<TaskMgmtVO> currentAssignees = taskMgmtMapper.getCurrentAssignee(entryId);
            if (ObjectUtils.isEmpty(currentAssignees)) {
                throw new JiMuBizException(OperationResp.FAILURE.getCode(), "当前流程节点无处理人！");
            }
            currentAssignees.forEach(o -> {
                if (Strings.isNullOrEmpty(o.getOriginalName()) || o.getOriginalName().equals(ProcessEnum.PROC_MAN.getDesc())) {
                    throw new JiMuBizException(OperationResp.FAILURE.getCode(), "当前流程节点无处理人！");
                }
                SendParam sendParam = SendParam.builder()
                        .userId(Long.parseLong(o.getOriginalName()))
                        .title(title)
                        .appUrl(PROC_TO_DO)
                        .content(title)
                        .params("myTask")
                        .urlParams(new UrlParams())
                        .build();
                MessageUtils.sendMessage(sendParam);
            });
        } catch (Exception e) {
            log.error("manual notice error", e);
        }
    }

    /**
     * 待处理添加代办URl地址
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
            url = "/user/workflow/Upcoming/check/" + processCode + "/" + bpmBusinessProcess.getBusinessId() + "/" + processContans.getMapValue(map, "nodeType") + "/" + task.getId();
        }
        /*if(!processCode.equals(Contants.ENTRY_NUMBER)){
        }*/
        log.info("**************组装连接：" + url + "*********************");
        return url;
    }

    /**
     * 工作流待办+通知
     * 流程流转，通知处理人
     *
     * @param entryId
     */
    public void notifyCurrent(String assignee, String entryId, DelegateTask task) {

        try {
            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());
            String procDefId = bpmBusinessProcess.getProcessinessKey();
            //流程编号
            String processNumber = bpmBusinessProcess.getBusinessNumber();
            //流程名称
            String processName = taskMgmtMapper.getProcessName(procDefId);
            //Thread.sleep(1000*30);
            SendParam sendParam = new SendParam();
            UrlParams urlParams = new UrlParams();
            if (Strings.isNullOrEmpty(assignee) || assignee.equals(ProcessEnum.PROC_MAN.getDesc())) {
                //设置处理人
                Map<Integer, List<Integer>> map = null;//todo获得管理员
                Integer procType = Optional.ofNullable(taskMgmtMapper.getProcType(procDefId)).orElse(-1);
                List<Integer> assignees = map.get(procType);
                if (ObjectUtils.isEmpty(assignees)) {
                    log.error("此流程无管理人员！");
                } else {
                    List<SendParam> sendParamList = new ArrayList<>();
                    assignees.forEach(o -> {
                        SendParam send = new SendParam();
                        //当前节点无处理人
                        String title = processName + processNumber + "无人处理，请至流程管理中进行干预。";
                        send.setTitle("您有1个" + processName + processNumber + "无人处理，请至流程管理中进行干预。");
                        send.setContent(title);
                        send.setParams("workflowList");
                        send.setUrlParams(urlParams);
                        send.setUrl("/manage/setting/work-process/process-manage");
                        send.setUserId(o.longValue());
                        send.setNode(task.getId());
                        sendParamList.add(send);
                    });
                    MessageUtils.sendMessageBatch(sendParamList);
                }
            } else {
                String url = this.getUrl(bpmBusinessProcess, task);
                //当前节点有处理人
                sendParam.setUserId(Long.parseLong(assignee));
                sendParam.setTitle("您有1个" + bpmBusinessProcess.getDescription() + "需要处理。");
                sendParam.setContent("您有1个" + processName + processNumber + bpmBusinessProcess.getDescription() + "需要处理。");
                sendParam.setUrl(url);
                //当前任务id
                if (!ObjectUtils.isEmpty(task)) {
                    sendParam.setNode(task.getId());
                }

                //代办列表
                sendParam.setAppUrl(PROC_TO_DO);
                sendParam.setParams("myTask");
                sendParam.setUrlParams(urlParams);
                MessageUtils.sendMessage(sendParam);

            }
        } catch (Exception e) {
            log.error("消息发送异常", e);
        }

    }

    /**
     * 工作流流转通知
     *
     * @param procInstId
     */
    public void passNotify(String procInstId, String entryId) {
        try {
            //获取通知人列表
            List<Integer> bpmFlowrunEntrusts = bpmFlowrunEntrustMapper.getCirculated(procInstId);

            if (ObjectUtils.isEmpty(bpmFlowrunEntrusts)) {
                return;
            }

            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(entryId).build());

            String procDefId = bpmBusinessProcess.getProcessinessKey();
            //流程编号
            String processNumber = bpmBusinessProcess.getBusinessNumber();
            //流程名称
            String processName = taskMgmtMapper.getProcessName(procDefId);
            String code = processNumber.split("_")[0];
            String title = "您关注的" + processName + processNumber + "已有新的进展。";

            UrlParams urlParams = new UrlParams();
            urlParams.setBusinessId(entryId.split(":")[1]);
            urlParams.setCode(code);

            Map<String, Object> map = bpmTaskconfigMapper.findByAppRoute(procDefId, null, "test");
            String appUrl = StringUtils.join(processContans.getMapValue(map, "routeUrl"), entryId.split(":")[0]);

            bpmFlowrunEntrusts.forEach(o -> {
                SendParam sendParam = SendParam.builder()
                        .userId(o.longValue())
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
