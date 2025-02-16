package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BpmVariableMapper extends BaseMapper<BpmVariable> {
    //one node can have multiple elements,but all of them share the same elementId,so if the results contains multiple results,they are all the same
    List<String> getElementIsdByNodeId(@Param("processNum") String processNum,
                                                  @Param("nodeId") String nodeId);
}
