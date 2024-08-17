package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.confentity.BpmTaskconfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BpmTaskconfigMapper extends BaseMapper<BpmTaskconfig> {
    /**
     * query config by processKey and taskKey
     *
     * @param procDefId
     * @param taskDefKey
     * @return
     */
    public List<Integer> findByProcessIdBpmTaskconfig(@Param("procDefId") String procDefId, @Param("taskDefKey") String taskDefKey, @Param("number") Integer number);

    /**
     * delete taskconfig by processKey
     *
     * @param procDefId
     * @return
     */
    public Integer deleteTaskconfig(@Param("procDefId") String procDefId);

    /**
     * delete a taskconfig by process instance id and task key
     *
     * @param procDefId
     * @return
     */
    public Integer deleteByTask(@Param("procDefId") String procDefId, @Param("taskKey") String taskKey);

    /**
     * query resource permission by process instance id and process key
     *
     * @param procDefId  流程id
     * @param taskDefKey 节点key
     * @return
     */
    public List<TaskMgmtVO> findTaskCode(@Param("procDefId") String procDefId, @Param("taskDefKey") String taskDefKey);

    /**
     * query resource code by taskKey and nodeType
     *
     * @param taskDefKey
     * @return
     */
    public Map<String, Object> findTaskNodeType(@Param("taskDefKey") String taskDefKey);



    /**
     * delete taskconfig by processKey,taskDefId and userId
     *
     * @param procDefId
     * @param userId
     * @return
     */
    public Integer deleteUnundertake(@Param("procDefId") String procDefId, @Param("userId") Integer userId, @Param("taskDefId") String taskDefId);

    /**
     * query task's rollback node by processId and taskKey
     *
     * @param processDefId
     * @param taskKey
     * @return
     */
    public String findTaskRollBack(@Param("processDefId") String processDefId, @Param("taskKey") String taskKey);

    /**
     * get app route
     */
    public Map<String, Object> findByAppRoute(@Param("processKey") String processKey, @Param("taskKey") String taskKey, @Param("routeType") String routeType);

    /**
     * get a a task's assignee by processKey and taskKey
     *
     * @param procInstId 流程实例id
     * @param taskDefKey 节点key
     * @return
     */
    public Map<String, Object> findHiTaskHandle(@Param("procInstId") String procInstId, @Param("taskDefKey") String taskDefKey);

    /**
     * get current entrust by processKey and apply User id
     *
     * @param procInstId
     * @param applyUser
     * @return
     */
    public Map<String, Object> findEntrust(@Param("procInstId") String procInstId, @Param("applyUser") Integer applyUser);

    /**
     * query taskconfig by processKey and userId
     */
    public List<BpmTaskconfig> findBpmTaskconfig(BpmTaskconfig bpmTaskconfig);

    /**

     * query current node disagree type
     *
     * @param nodeKey    node key
     * @param processKey process key
     * @return
     */
    public Integer disagreeType(@Param("nodeKey") String nodeKey, @Param("processKey") String processKey);

    /**
     * get process key
     */
    public String getProcessKey(@Param("deploymentId") String deploymentId);
}
