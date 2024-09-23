package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmManualNotify;
import org.springframework.stereotype.Repository;

@Mapper
public interface BpmManualNotifyMapper extends BaseMapper<BpmManualNotify> {

}