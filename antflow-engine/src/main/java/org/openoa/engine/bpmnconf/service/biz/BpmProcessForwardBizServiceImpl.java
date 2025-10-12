package org.openoa.engine.bpmnconf.service.biz;

import com.google.common.base.Strings;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmProcessForward;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessForwardBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BpmProcessForwardBizServiceImpl implements BpmProcessForwardBizService {
    private static Map<String, BpmProcessForward> processForwardMap = new ConcurrentHashMap<>();

    private static Map<String, Task> taskMap = new ConcurrentHashMap<>();


    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProviderService;

    /**
     * put data in cache and load if necessary
     */
    @Override
    public void loadProcessForward(String userId) {
        Map<String, BpmProcessForward> map = new HashMap<>();
        List<BpmProcessForward> list = this.getService().allBpmProcessForward(userId);
        if (list == null) {
            return;
        }
        for (BpmProcessForward next : list) {
            if(!StringUtils.isEmpty(next.getProcessInstanceId())){
                map.put(next.getProcessInstanceId(), next);
            }
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


    @Override
    public void loadTask(String userId) {
        Map<String, Task> map = new HashMap<>();
        List<Task> list = this.findTaskByEmpId(userId);
        if (list == null) {
            return;
        }
        for (Task next : list) {
            if(!StringUtils.isEmpty(next.getProcessInstanceId())){
                map.put(next.getProcessInstanceId(), next);
            }
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
    @Override
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
    @Override
    public void addProcessForwardBatch(String procInstId,String processNumber,List<String> forwardUserIds){
        if(CollectionUtils.isEmpty(forwardUserIds)){
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
        this.getService().saveBatch(processForwardList);
    }
}
