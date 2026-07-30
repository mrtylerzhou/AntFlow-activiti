package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BatchAgreeResultVo;
import org.openoa.base.vo.BatchAgreeVo;
import org.openoa.base.vo.BpmProcessVo;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNodeSubmitBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Batch approval service
 */
@Service
@Slf4j
public class BatchApprovalBizService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmProcessNodeSubmitBizService processNodeSubmitBizService;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private BpmnConfMapper bpmnConfMapper;

    /**
     * Batch agree entry point.
     * Loops through taskIds, executes each in its own transaction.
     * Returns partial success result.
     */
    public BatchAgreeResultVo batchAgree(BatchAgreeVo vo) {
        List<String> taskIds = vo.getTaskIds();
        String comment = StringUtils.hasText(vo.getBatchApprovalComment()) ? vo.getBatchApprovalComment() : "同意";
        String currentUserId = SecurityUtils.getLogInEmpIdStr();
        String currentUserName = SecurityUtils.getLogInEmpName();

        int successCount = 0;
        List<BatchAgreeResultVo.FailureItem> failures = new ArrayList<>();

        for (String taskId : taskIds) {
            String processNumber = null;
            String processName = null;
            try {
                Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (task == null) {
                    failures.add(BatchAgreeResultVo.FailureItem.builder()
                            .taskId(taskId)
                            .reason("任务不存在，请刷新页面")
                            .build());
                    continue;
                }
                // security check: only the assignee can approve
                if (!currentUserId.equals(task.getAssignee())) {
                    failures.add(BatchAgreeResultVo.FailureItem.builder()
                            .taskId(taskId)
                            .reason("无权操作此任务")
                            .build());
                    continue;
                }

                BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getOne(
                        new QueryWrapper<BpmBusinessProcess>().eq("PROC_INST_ID_", task.getProcessInstanceId()));
                if (bpmBusinessProcess == null) {
                    failures.add(BatchAgreeResultVo.FailureItem.builder()
                            .taskId(taskId)
                            .reason("未找到关联流程数据")
                            .build());
                    continue;
                }

                processNumber = bpmBusinessProcess.getBusinessNumber();
                processName = getProcessName(bpmBusinessProcess.getProcessinessKey());

                // execute single approval in transaction
                executeSingleApproval(task, bpmBusinessProcess, comment, currentUserId, currentUserName);
                successCount++;

            } catch (Exception e) {
                log.error("batch agree failed for taskId: {}", taskId, e);
                failures.add(BatchAgreeResultVo.FailureItem.builder()
                        .taskId(taskId)
                        .processNumber(processNumber)
                        .processName(processName)
                        .reason("审批执行失败: " + e.getMessage())
                        .build());
            }
        }

        return BatchAgreeResultVo.builder()
                .successCount(successCount)
                .failures(failures)
                .build();
    }

    /**
     * Execute single task approval within a transaction.
     * Includes: processComplete + verifyInfo + processDigest update
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeSingleApproval(Task task, BpmBusinessProcess bpmBusinessProcess,
                                      String comment, String userId, String userName) {
        // 1. complete task (with flow control)
        processNodeSubmitBizService.processComplete(task);

        // 2. write verify info
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo.builder()
                .verifyDate(new Date())
                .taskName(task.getName())
                .taskId(task.getId())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .verifyUserId(userId)
                .verifyUserName(userName)
                .taskDefKey(task.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(comment)
                .processCode(bpmBusinessProcess.getBusinessNumber())
                .build();
        bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);

        // 3. update process digest
        if (!ObjectUtils.isEmpty(bpmBusinessProcess.getProcessDigest())) {
            bpmBusinessProcessService.update(BpmBusinessProcess.builder()
                            .processDigest(bpmBusinessProcess.getProcessDigest())
                            .build(),
                    new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", bpmBusinessProcess.getBusinessNumber()));
        }
    }

    private String getProcessName(String formCode) {
        if (!StringUtils.hasText(formCode)) {
            return "";
        }
        try {
            BpmProcessVo bpmProcessVo = bpmnConfMapper.getBpmProcessVoByFormCode(formCode);
            return bpmProcessVo != null ? bpmProcessVo.getProcessName() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
