package org.openoa.engine.bpmnconf.common;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Service
public class TaskMgmtServiceImpl extends ServiceImpl<TaskMgmtMapper, TaskMgmtVO> {
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessServiceImpl processService;
    @Autowired
    protected BpmBusinessProcessMapper bpmBusinessProcessMapper;
    @Autowired
    private RuntimeService runtimeService;


    /**
     * find task by its id
     *
     * @param taskId
     * @return
     * @throws JiMuBizException
     */
    public TaskMgmtVO findTask(String taskId) throws JiMuBizException {
        return taskMgmtMapper.findTask(taskId);
    }

    /**
     * get task vo by its id
     *
     * @param taskId
     * @return
     * @throws JiMuBizException
     */
    public TaskMgmtVO getAgencyList(String taskId) throws JiMuBizException {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        TaskMgmtVO mgmtVO = new TaskMgmtVO();
        if (!ObjectUtils.isEmpty(task)) {
            mgmtVO.setTaskStype(2);
        } else {
            mgmtVO.setTaskStype(1);
        }
        return mgmtVO;
    }

    /**
     * change task assignee by taskid
     */
    public void updateTask(TaskMgmtVO taskMgmtVO) {
        if (ObjectUtils.isEmpty(taskMgmtVO.getTaskIds())) {
            throw new JiMuBizException("please select the task ids to modify ！！");
        }
        if (!ObjectUtils.isEmpty(taskMgmtVO.getTaskIds())) {
            taskMgmtVO.getTaskIds().forEach(o -> {
                taskMgmtMapper.updateaActinst(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .taskId(o)
                        .build());
                taskMgmtMapper.updateaTaskinst(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .taskId(o)
                        .build());
                taskMgmtMapper.updateTask(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .taskId(o)
                        .build());
            });

        }
    }


    /**
     * get current node and taskId by businessId and processCode
     *
     * @param taskMgmtVO
     * @return
     */
    public TaskMgmtVO findByTask(TaskMgmtVO taskMgmtVO) {
        try {

            taskMgmtVO.setApplyUser( SecurityUtils.getLogInEmpIdSafe().toString());
            BpmBusinessProcess bpmBusinessProcess = processService.findBpmBusinessProcess(taskMgmtVO.getBusinessId(), taskMgmtVO.getCode());
            if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
                taskMgmtVO.setEntryId(bpmBusinessProcess.getEntryId());
                TaskMgmtVO mgmtVO = taskMgmtMapper.findByTask(taskMgmtVO);
                return mgmtVO;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new JiMuBizException("根据业务ID:[" + taskMgmtVO.getBusinessId() + "]无法查询代办数据");
        }
    }

    public void  changeFutureAssignees(String executionId, String variableName, List<String> assignees){
        Map<String,Object> assigneeMap=new HashMap<>();
        assigneeMap.put(variableName,assignees);
        runtimeService.setVariables(executionId,assigneeMap);
    }
}