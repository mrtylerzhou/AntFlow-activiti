package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmVariableMessage;
import org.springframework.stereotype.Repository;

/**
 * @Classname BpmVariableMessageMapper
 *@since 0.5
 * @Created by AntOffice
 */
@Mapper
public interface BpmVariableMessageMapper extends BaseMapper<BpmVariableMessage> {
}
