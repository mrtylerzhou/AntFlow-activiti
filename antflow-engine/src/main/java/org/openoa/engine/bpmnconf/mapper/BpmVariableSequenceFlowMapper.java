package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSequenceFlow;
import org.springframework.stereotype.Repository;

/**
 * @Classname BpmVariableSequenceFlowMapper
 * @Description variable sequence flow mapper
 * @since 0.5
 * @Created by AntOffice
 */
@Mapper
public interface BpmVariableSequenceFlowMapper extends BaseMapper<BpmVariableSequenceFlow> {
}
