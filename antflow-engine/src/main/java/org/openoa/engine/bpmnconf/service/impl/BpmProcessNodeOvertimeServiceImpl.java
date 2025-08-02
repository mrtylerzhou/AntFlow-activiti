package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.enums.ProcessKeyEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.util.DateUtil;
import org.openoa.base.entity.BpmProcessNodeOvertime;
import org.openoa.base.entity.BpmProcessNodeRecord;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeOvertimeMapper;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.BpmProcessNodeOvertimeVo;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.constant.enums.ProcessEnum;

import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeOvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class BpmProcessNodeOvertimeServiceImpl extends ServiceImpl<BpmProcessNodeOvertimeMapper, BpmProcessNodeOvertime> implements BpmProcessNodeOvertimeService {

    @Autowired
    private BpmProcessNodeOvertimeMapper bpmProcessNodeOvertimeMapper;
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
     * save overtime remind info
     *
     * @return
     */
    public boolean saveNodeOvertime(BpmProcessDeptVo vo) {
        QueryWrapper<BpmProcessNodeOvertime> wrapper = new QueryWrapper<BpmProcessNodeOvertime>();
        wrapper.eq("process_key", vo.getProcessKey());
        bpmProcessNodeOvertimeMapper.delete(wrapper);
        if (!ObjectUtils.isEmpty(vo.getRemindTypeIds())) {
            vo.getRemindTypeIds().forEach(o -> {
                if (!ObjectUtils.isEmpty(vo.getNodeIds())) {
                    vo.getNodeIds().forEach(node -> {
                        bpmProcessNodeOvertimeMapper.insert(BpmProcessNodeOvertime.builder()
                                .noticeType(o)
                                .processKey(vo.getProcessKey())
                                .nodeKey(node)
                                //.nodeName(node.getNodeName())
                                .noticeTime(vo.getNoticeTime())
                                .build());
                    });
                }
            });
        }
        return true;
    }

    /**
     * get a list of node overtime info
     *
     * @param processKey
     * @return
     */
    public List<BpmProcessNodeOvertime> nodeOvertimeList(String processKey) {
        QueryWrapper<BpmProcessNodeOvertime> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        List<BpmProcessNodeOvertime> list = bpmProcessNodeOvertimeMapper.selectList(wrapper);
        return list;
    }

    /**
     * get notice type
     *
     * @param processKey
     * @return
     */
    public List<Integer> selectNoticeType(String processKey) {
        return bpmProcessNodeOvertimeMapper.selectNoticeType(processKey);
    }

    /***

     * query a list of node overtime info
     * @param processKey
     * @return
     */
    public List<BpmProcessNodeOvertimeVo> selectNoticeNodeName(String processKey) {
        return bpmProcessNodeOvertimeMapper.selectNoticeNodeName(processKey);
    }

    /**
     * check whether current node is overtime
     */
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
        List<BpmProcessNodeOvertime> list = bpmProcessNodeOvertimeMapper.selectList(wrapper);
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
    public void checkTaskOvertime() throws JiMuBizException {
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
            } catch (JiMuBizException e) {
                e.printStackTrace();
            }
        }
    }
}
