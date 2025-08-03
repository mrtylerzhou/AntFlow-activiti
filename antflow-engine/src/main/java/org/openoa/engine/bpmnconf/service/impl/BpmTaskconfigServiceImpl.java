package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.entity.BpmTaskconfig;
import org.openoa.engine.bpmnconf.mapper.BpmTaskconfigMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmTaskconfigService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BpmTaskconfigServiceImpl extends ServiceImpl<BpmTaskconfigMapper, BpmTaskconfig> implements BpmTaskconfigService {

    /**
     * save task config
     *
     * @param procDefId  process definition id
     * @param taskDefKey task definition key
     */
    @Override
    public void addBpmTaskconfig(String procDefId, String taskDefKey, Long userId, Integer number) {
        BpmTaskconfig con = new BpmTaskconfig();
        con.setProcDefId(procDefId);
        con.setTaskDefKey(taskDefKey);
        con.setUserId(userId);
        con.setNumber(number);
        getBaseMapper().insert(con);
    }

    @Override
    public void addBpmTaskconfig(BpmTaskconfig bpmTaskconfig) {
        getBaseMapper().insert(bpmTaskconfig);
    }

    public void addBpmTaskconfig(String procDefId, String taskDefKey, Long userId) {
        BpmTaskconfig con = new BpmTaskconfig();
        con.setProcDefId(procDefId);
        con.setTaskDefKey(taskDefKey);
        con.setUserId(userId);
        getBaseMapper().insert(con);
    }





    /**
     * 查询资源数据
     */
    @Override
    public List<TaskMgmtVO> findTaskCode(String procDefId, String taskDefKey) {
        return getBaseMapper().findTaskCode(procDefId, taskDefKey);
    }

    /**
     * 根据节点获取数据
     */
    public Map<String, Object> findTaskNodeType(String taskDefKey) {
        return getBaseMapper().findTaskNodeType(taskDefKey);
    }

    /**
     * 获取app路由
     */
    @Override
    public Map<String, Object> findByAppRoute(@Param("processKey") String processKey, @Param("taskKey") String taskKey, @Param("routeType") String routeType) {
        return getBaseMapper().findByAppRoute(processKey, taskKey, routeType);
    }

    /**
     * 根据流程实例获取指定节点处理人
     *
     * @param procInstId 流程实例id
     * @param taskDefKey 节点key
     * @return
     */
    @Override
    public Map<String, Object> findHiTaskHandle(@Param("procInstId") String procInstId, @Param("taskDefKey") String taskDefKey) {
        return getBaseMapper().findHiTaskHandle(procInstId, taskDefKey);
    }

    /**
     * 根据当前流程实例获取当前流程传阅人
     *
     * @param procInstId
     * @param applyUser
     * @return
     */
    @Override
    public Map<String, Object> findEntrust(String procInstId, Integer applyUser) {

        return getBaseMapper().findEntrust(procInstId, applyUser);
    }

    /**
     * 根据HRBP指派人员数据判断是否有薪酬权限
     */
    @Override
    public List<BpmTaskconfig> findBpmTaskconfig(BpmTaskconfig bpmTaskconfig) {
        return getBaseMapper().findBpmTaskconfig(bpmTaskconfig);
    }

    /**
     * 根据任务实例id删除任务配置数据
     *
     * @param procDefId 流程实例id
     * @param taskKey   节点Key
     * @return
     */
    @Override
    public Integer deleteByTask(String procDefId, String taskKey) {
        return getBaseMapper().deleteByTask(procDefId, taskKey);
    }

    /**
     * 查询当前节点不同意类型
     *
     * @param nodeKey    节点Key
     * @param processKey 流程key
     * @return
     */
    @Override
    public Integer disagreeType(String nodeKey, String processKey) {
        return getBaseMapper().disagreeType(nodeKey, processKey);
    }

    @Override
    public String getProcessKey(String deploymentId) {
        return getBaseMapper().getProcessKey(deploymentId);
    }
}
