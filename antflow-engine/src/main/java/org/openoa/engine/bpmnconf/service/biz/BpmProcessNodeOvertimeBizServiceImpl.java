package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.enums.ProcessEnum;
import org.openoa.base.constant.enums.ProcessKeyEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmProcessNodeOvertime;
import org.openoa.base.entity.BpmProcessNodeRecord;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.DateUtil;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNodeRecordServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNodeOvertimeBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class BpmProcessNodeOvertimeBizServiceImpl implements BpmProcessNodeOvertimeBizService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmProcessNodeRecordServiceImpl processNodeRecordService;
    @Autowired
    private ProcessBusinessContans processBusinessContans;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmBusinessProcessMapper bpmBusinessProcessService;

    /**
     * check whether current node is overtime
     */
    @Override
    public Boolean nodeOvertime(String processKey, String taskId) {

        //get node overtime
        //get the time difference between  current time and the current node creation time
        //compare the time difference and the overtime time
        Boolean isOvertime = false;
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        QueryWrapper<BpmProcessNodeOvertime> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        wrapper.eq("node_key", task.getTaskDefinitionKey());
        List<BpmProcessNodeOvertime> list = getMapper().selectList(wrapper);
        if (!ObjectUtils.isEmpty(list)) {
            BpmProcessNodeOvertime processNodeOvertime = list.get(0);
            Long hours = DateUtil.dateDiff(new Date(), task.getCreateTime(), 2);
            if (!ObjectUtils.isEmpty(processNodeOvertime.getNoticeTime())) {
                if (processNodeOvertime.getNoticeTime() <= hours.intValue()) {
                    isOvertime = true;
                } else {
                    isOvertime = false;
                }
            } else {
                isOvertime = false;
            }

        }
        return isOvertime;
    }

    /**
     * get task overtime
     */
    @Override
    public void checkTaskOvertime() throws AFBizException {
        List<Task> list = taskService.createTaskQuery().list();
        for (Task task : list) {
            try {
                //获取流程key
                String processKey = task.getProcessDefinitionId().split("\\:")[0].toString();
                if (!ObjectUtils.isEmpty(ProcessKeyEnum.getCodeByDesc(processKey))) {
                    BpmProcessNodeRecord processNodeRecord = processNodeRecordService.getBpmProcessNodeRecord(BpmProcessNodeRecord.builder()
                            .processInstanceId(task.getProcessInstanceId())
                            .taskId(task.getId())
                            .build());
                    if (ObjectUtils.isEmpty(processNodeRecord)) {
                        if (!task.getAssignee().equals(ProcessEnum.PROC_MAN.getDesc()) && !ObjectUtils.isEmpty(task.getAssignee())) {
                            if (this.nodeOvertime(processKey, task.getId())) {
                                String businessKey = taskMgmtMapper.findByProcinstIdTask(task.getProcessInstanceId());
                                BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.findBpmBusinessProcess(BpmBusinessProcess.builder().entryId(businessKey).build());
                                //todo send message
                                processNodeRecordService.addBpmProcessNodeRecord(BpmProcessNodeRecord.builder()
                                        .taskId(task.getId())
                                        .processInstanceId(task.getProcessInstanceId())
                                        .build());
                            }
                        }
                    }
                }
            } catch (AFBizException e) {
                e.printStackTrace();
            }
        }
    }
}
