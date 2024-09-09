package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.confentity.BpmProcessForward;
import org.openoa.engine.bpmnconf.mapper.BpmProcessForwardMapper;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BpmProcessForwardServiceImpl extends ServiceImpl<BpmProcessForwardMapper, BpmProcessForward> {

    private static Map<String, BpmProcessForward> processForwardMap = new ConcurrentHashMap<>();

    private static Map<String, Task> taskMap = new ConcurrentHashMap<>();


    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProviderService;
    /**
     * add process forward data
     *
     * @param forward
     */
    public void addProcessForward(BpmProcessForward forward) {
        this.getBaseMapper().insert(forward);
    }

    /**
     * update forward info
     *
     * @param forward
     */
    public void updateProcessForward(BpmProcessForward forward) {
        QueryWrapper<BpmProcessForward> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", forward.getProcessInstanceId());
        wrapper.eq("forward_user_id", forward.getForwardUserId());
        this.getBaseMapper().selectList(wrapper).forEach(o -> {
            o.setIsRead(1);
            this.getBaseMapper().updateById(o);
        });
    }


    /**
     * query process forward by user id
     */
    public List<BpmProcessForward> allBpmProcessForward(String userId) {
        return Optional.ofNullable(this.getBaseMapper().selectList(new QueryWrapper<BpmProcessForward>()
                .eq("is_del", 0)
                .eq("forward_user_id", userId)
        ))
                .orElse(Collections.emptyList());
    }

    /**
     * put data in cache and load if necessary
     */
    public void loadProcessForward(String userId) {
        Map<String, BpmProcessForward> map = new HashMap<>();
        List<BpmProcessForward> list = this.allBpmProcessForward(userId);
        if (list == null) {
            return;
        }
        for (BpmProcessForward next : list) {
            map.put(next.getProcessInstanceId(), next);
        }
        processForwardMap.putAll(map);
    }

    public BpmProcessForward getProcessForward(String processInstanceId) {
        if (Strings.isNullOrEmpty(processInstanceId)) {
            return null;
        }
        return processForwardMap.get(processInstanceId);
    }

    /**
     * query task by user id
     */
    public List<Task> findTaskByEmpId(String userId) {
        return Optional.ofNullable(taskService.createTaskQuery().taskAssignee(userId).list()).orElse(Arrays.asList());
    }


    public void loadTask(String userId) {
        Map<String, Task> map = new HashMap<>();
        List<Task> list = this.findTaskByEmpId(userId);
        if (list == null) {
            return;
        }
        for (Task next : list) {
            map.put(next.getProcessInstanceId(), next);
        }
        taskMap.putAll(map);
    }

    public Task getTask(String processInstanceId) {
        if (Strings.isNullOrEmpty(processInstanceId)) {
            return null;
        }
        return taskMap.get(processInstanceId);
    }

    /**
     * to check whether the process is forwarded
     *
     * @return
     */
    public boolean isForward(String processInstanceId) {
        if (ObjectUtils.isEmpty(this.getTask(processInstanceId))) {
            return false;
        } else if (!ObjectUtils.isEmpty(this.getProcessForward(processInstanceId))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * add process forward by process instance id and forward user ids in batch
     * @param procInstId
     * @param forwardUserIds
     */
    public void addProcessForwardBatch(String procInstId,String processNumber,List<String> forwardUserIds){
        if(ObjectUtils.isEmpty(forwardUserIds)){
            return;
        }
        if(Strings.isNullOrEmpty(procInstId)||Strings.isNullOrEmpty(processNumber)){
            return;
        }
        Map<String, String> employeeInfo = employeeInfoProviderService.provideEmployeeInfo(forwardUserIds);
        List<BpmProcessForward> processForwardList=new ArrayList<>();
        for (String userId :forwardUserIds) {
            BpmProcessForward processForward = BpmProcessForward.builder()
                    .createTime(new Date())
                    .createUserId(SecurityUtils.getLogInEmpIdStr())
                    .ForwardUserName(employeeInfo.get(userId))
                    .forwardUserId(userId)
                    .processInstanceId(procInstId)
                    .processNumber(processNumber)
                    .build();
            processForwardList.add(processForward);
        }
        this.saveBatch(processForwardList);
    }
}
