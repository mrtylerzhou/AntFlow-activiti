package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.springframework.stereotype.Repository;

@Mapper
public interface BpmVariableMapper extends BaseMapper<BpmVariable> {

}
