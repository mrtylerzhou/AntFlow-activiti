package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmFlowruninfo;
import org.springframework.stereotype.Repository;
//todo has no mapper.xml
@Mapper
public interface BpmFlowruninfoMapper extends BaseMapper<BpmFlowruninfo> {
    /**
     * get runinfo by id
     *
     * @param runInfoId
     * @return
     */
    public BpmFlowruninfo getFlowruninfo(@Param("runInfoId") Long runInfoId);

    /**
     * get process definition
     *
     * @param processDefinitionId
     * @return
     */
    public ProcessDefinition getProcessDefinition(String processDefinitionId);
}
