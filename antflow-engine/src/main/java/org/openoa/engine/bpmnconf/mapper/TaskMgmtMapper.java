package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.service.cmd.DeleteRunningTaskCmd;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * //todo 分页插件
 *
 * @Classname TaskMgmtMapper
 * @Description task management mapper,for generic info
 * @Date 2021-11-08 22:48
 * @Created by AntOffice
 */
@Mapper
public interface TaskMgmtMapper extends BaseMapper<TaskMgmtVO> {

    /**
     * get my to do list
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    public List<TaskMgmtVO> findAgencyList(Page page, TaskMgmtVO taskMgmtVO);

    /**
     * get my process
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    public List<TaskMgmtVO> findActHiProcinst(Page page, TaskMgmtVO taskMgmtVO);

    /**
     * query process that a specified user involved in
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    public List<TaskMgmtVO> findAllHiTaskinst(Page page, @Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query process that entrusted by me or passed to me
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    public List<TaskMgmtVO> findAllEntrustTask(Page page, @Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query current task's assignee by task id
     *
     * @param taskId
     * @return
     */
    public TaskMgmtVO findTask(@Param("taskId") String taskId);

    /**
     * 根据任务id修改当前任务节点处理人
     *
     * @param taskMgmtVO
     * @return
     */
    public Integer updateaActinst(@Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * modify current node's history assignee
     *
     * @param taskMgmtVO
     * @return
     */
    public Integer updateaTaskinst(@Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * modify current assignee
     *
     * @param taskMgmtVO
     * @return
     */
    public Integer updateTask(@Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * get login user task count
     *
     * @param loginUserId
     * @return
     */
    public Integer findTaskCount(@Param("loginUserId") Integer loginUserId);

    /**
     * get forward to current user's process  count
     *
     * @param loginUserId
     * @return
     */
    public Integer findCirculateCount(@Param("loginUserId") Integer loginUserId);

    /**
     * get to do task by taskId and process instance id
     *
     * @param taskId task id
     * @param type  get same node undertake task
     * @return
     */
    public List<TaskMgmtVO> getAgencyList(@Param("taskId") String taskId, @Param("type") Integer type, @Param("processInstanceId") String processInstanceIds);

    /**
     * delete current task
     *
     * @param taskId task id
     * @return
     */
    public Integer deletTask(@Param("taskId") String taskId);

    /**
     * query whether current task is finished by business id
     *
     * @param businessId
     * @return
     */
    public TaskMgmtVO findHiProcinst(@Param("businessId") String businessId);

    /**
     * get all process's names
     */
    public List<TaskMgmtVO> findAllProcess();

    /**
     * 根据业务id和流程Code获取节点id和taskID
     *
     * @return
     */
    public TaskMgmtVO findByTask(@Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query business key by process instance id
     *
     * @param procinstId
     * @return
     */
    public String findByProcinstIdTask(@Param("procinstId") String procinstId);

    /**
     * query whether show revoke button by  business key and start user id
     *
     * @return
     */
    public Integer showRevoke(@Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query process instance id by business key
     *
     * @param businessId
     * @return
     */
    public String findByBusinessId(@Param("businessId") String businessId);

    /**
     * query process history instance,if endtime is null ,then we can judge that the process is finished
     *
     * @param processNumber
     * @return
     */
    public TaskMgmtVO findByHiProcinst(@Param("processNumber") String processNumber);

    /**
     * get process info by entryId
     *
     * @param entryId
     * @return
     */
    public TaskMgmtVO findByEntryId(@Param("entryId") String entryId);

    /**
     * get revoke node data
     *
     * @return
     */
    public TaskMgmtVO findByTaskinst(@Param("entryId") String entryId);

    /**
     * get all approvers by entryId
     *
     * @param entryId
     * @return
     */
    public List<String> getAssigneesByEntryId(@Param("entryId") String entryId);

    /**
     * query current assignee by entryId
     *
     * @param entryId
     * @return
     */
    public List<TaskMgmtVO> getCurrentAssignee(@Param("entryId") String entryId);

    /**
     * 根据BUSINESS_NUMBER获取entryId
     *
     * @param code
     * @param businessId
     * @return
     */
    public String getEntryId(@Param("code") String code, @Param("businessId") Long businessId);

    /**
     * query current assignee by provided task_id
     *
     * @param taskId
     * @return
     */
    public String getAssigneeByTaskId(@Param("taskId") String taskId);

    /**
     * @param id
     * @return
     */
    public String getProcessName(@Param("id") String id);

    /**
     * delete process instance by entryId that started by a specified user
     */
    public Integer deleteProcess(@Param("entryId") String entryId);

    /**
     * 删除历史节点数据
     */
    public Integer deleteTaskinst(@Param("processInstanceId") String processInstanceId);

    /**
     * delete a process historic instance id
     */
    public Integer deleteActinst(@Param("processInstanceId") String processInstanceId);

    /**
     * query whether current process is exist
     *
     * @param
     * @return
     */
    public Integer findProcessModel(@Param("fileName") String fileName);

    /**
     * query current task's id by process instance id
     *
     * @param processInstanceId
     * @return
     */
    public String getTaskId(@Param("processInstanceId") String processInstanceId);

    /**
     * get to do task notify info
     *
     * @param procInstId
     * @param taskDefId
     * @return
     */
    public TaskMgmtVO findNotifyInfo(@Param("procInstId") String procInstId, @Param("taskDefId") String taskDefId, @Param("assignee") String assignee);

    /**
     * get start user id by entryId
     *
     * @param entryId
     * @return
     */
    public String getStartuserByEntryId(@Param("entryId") String entryId);

    /**
     * query current process type by process key
     *
     * @param procDefKey
     * @return
     */
    public Integer getProcType(@Param("procDefKey") String procDefKey);

    /**
     * query whether current process has parallel gateway
     */
    public List<TaskMgmtVO> findTaskCout(@Param("entryId") String entryId);

    /**
     * query task by task id
     *
     * @param taskId
     * @return
     */
    public Map<String, Object> findTaskById(@Param("taskId") String taskId);
    public String findProcDefIdByInstId(String procInstId);

    void deleteExecutionsByProcinstIdAndTaskDefKeys(@Param("procInstId")String procInstId,@Param("taskDefKeys") List<String> taskdefkeys);
}
