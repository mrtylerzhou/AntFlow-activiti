package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsParamConf;
import org.springframework.stereotype.Repository;

@Mapper
public interface BpmnNodeConditionsParamConfMapper extends BaseMapper<BpmnNodeConditionsParamConf> {
    void insertParams(@Param("conf") BpmnNodeConditionsParamConf conf);
}