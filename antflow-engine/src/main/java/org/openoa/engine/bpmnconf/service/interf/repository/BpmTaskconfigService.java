package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmTaskconfig;
import org.openoa.base.vo.TaskMgmtVO;

import java.util.List;
import java.util.Map;

public interface BpmTaskconfigService extends IService<BpmTaskconfig> {
    void addBpmTaskconfig(String procDefId, String taskDefKey, Long userId, Integer number);

    void addBpmTaskconfig(BpmTaskconfig bpmTaskconfig);

    List<TaskMgmtVO> findTaskCode(String procDefId, String taskDefKey);

    Map<String, Object> findByAppRoute(@Param("processKey") String processKey, @Param("taskKey") String taskKey, @Param("routeType") String routeType);

    Map<String, Object> findHiTaskHandle(@Param("procInstId") String procInstId, @Param("taskDefKey") String taskDefKey);

    Map<String, Object> findEntrust(String procInstId, Integer applyUser);

    List<BpmTaskconfig> findBpmTaskconfig(BpmTaskconfig bpmTaskconfig);

    Integer deleteByTask(String procDefId, String taskKey);

    Integer disagreeType(String nodeKey, String processKey);

    String getProcessKey(String deploymentId);
}
