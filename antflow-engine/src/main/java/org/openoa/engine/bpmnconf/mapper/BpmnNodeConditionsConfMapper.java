package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmnNodeConditionsConf;
import org.openoa.base.entity.BpmnNodeConditionsParamConf;

import java.util.List;

@Mapper
public interface BpmnNodeConditionsConfMapper extends BaseMapper<BpmnNodeConditionsConf> {
    List<String> queryConditionParamNameByProcessNumber(@Param("processNumber") String processNumber);
    List<String> queryConditionParamByFormCode(@Param("formCode") String formCode);
}
