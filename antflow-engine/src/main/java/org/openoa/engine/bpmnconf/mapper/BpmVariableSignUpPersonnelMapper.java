package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUpPersonnel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BpmVariableSignUpPersonnelMapper extends BaseMapper<BpmVariableSignUpPersonnel> {
    List<BaseIdTranStruVo> getByVariableIdAndElementId(@Param("variableId") Long variableId, @Param("elementId") String elementId);
    List<BaseIdTranStruVo> getByProcessNumAndElementId(@Param("processNumber") String processNumber, @Param("elementId") String elementId);
}
