package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.confentity.BpmTaskconfig;
import org.openoa.engine.bpmnconf.mapper.BpmTaskconfigMapper;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Service
public class BpmTaskconfigServiceImpl extends ServiceImpl<BpmTaskconfigMapper, BpmTaskconfig> {


    @Autowired
    private BpmTaskconfigMapper mapper;
    @Autowired
    private TaskService taskService;

    /**
     * save task config
     *
     * @param procDefId  process definition id
     * @param taskDefKey task definition key
     */
    public void addBpmTaskconfig(String procDefId, String taskDefKey, Long userId, Integer number) {
        BpmTaskconfig con = new BpmTaskconfig();
        con.setProcDefId(procDefId);
        con.setTaskDefKey(taskDefKey);
        con.setUserId(userId);
        con.setNumber(number);
        mapper.insert(con);
    }

    public void addBpmTaskconfig(BpmTaskconfig bpmTaskconfig) {
        mapper.insert(bpmTaskconfig);
    }

    public void addBpmTaskconfig(String procDefId, String taskDefKey, Long userId) {
        BpmTaskconfig con = new BpmTaskconfig();
        con.setProcDefId(procDefId);
        con.setTaskDefKey(taskDefKey);
        con.setUserId(userId);
        mapper.insert(con);
    }

    /**

     * get reject node by task id
     *
     * @param taskId
     * @return
     */
    public String findTargeNode(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (ObjectUtils.isEmpty(task)) {
            throw new JiMuBizException("当前任务id:[" + taskId + "]" + "不存在");
        }
        String processKey = task.getProcessDefinitionId().split(":")[0];
        String nodeKey = mapper.findTaskRollBack(processKey, task.getTaskDefinitionKey());
        return nodeKey == null ? "" : nodeKey;
    }



    /**
     * 查询资源数据
     */
    public List<TaskMgmtVO> findTaskCode(String procDefId, String taskDefKey) {
        return mapper.findTaskCode(procDefId, taskDefKey);
    }

    /**
     * 根据节点获取数据
     */
    public Map<String, Object> findTaskNodeType(String taskDefKey) {
        return mapper.findTaskNodeType(taskDefKey);
    }

    /**
     * 获取app路由
     */
    public Map<String, Object> findByAppRoute(@Param("processKey") String processKey, @Param("taskKey") String taskKey, @Param("routeType") String routeType) {
        return mapper.findByAppRoute(processKey, taskKey, routeType);
    }

    /**
     * 根据流程实例获取指定节点处理人
     *
     * @param procInstId 流程实例id
     * @param taskDefKey 节点key
     * @return
     */
    public Map<String, Object> findHiTaskHandle(@Param("procInstId") String procInstId, @Param("taskDefKey") String taskDefKey) {
        return mapper.findHiTaskHandle(procInstId, taskDefKey);
    }

    /**
     * 根据当前流程实例获取当前流程传阅人
     *
     * @param procInstId
     * @param applyUser
     * @return
     */
    public Map<String, Object> findEntrust(String procInstId, Integer applyUser) {

        return mapper.findEntrust(procInstId, applyUser);
    }

    /**
     * 根据HRBP指派人员数据判断是否有薪酬权限
     */
    public List<BpmTaskconfig> findBpmTaskconfig(BpmTaskconfig bpmTaskconfig) {
        return mapper.findBpmTaskconfig(bpmTaskconfig);
    }

    /**
     * 根据任务实例id删除任务配置数据
     *
     * @param procDefId 流程实例id
     * @param taskKey   节点Key
     * @return
     */
    public Integer deleteByTask(String procDefId, String taskKey) {
        return mapper.deleteByTask(procDefId, taskKey);
    }

    /**
     * 查询当前节点不同意类型
     *
     * @param nodeKey    节点Key
     * @param processKey 流程key
     * @return
     */
    public Integer disagreeType(String nodeKey, String processKey) {
        return mapper.disagreeType(nodeKey, processKey);
    }

    public String getProcessKey(String deploymentId) {
        return mapper.getProcessKey(deploymentId);
    }
}
