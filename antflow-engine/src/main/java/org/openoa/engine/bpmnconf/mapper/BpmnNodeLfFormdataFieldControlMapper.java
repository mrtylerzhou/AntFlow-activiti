package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.LFFieldControlVO;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeLfFormdataFieldControl;

import java.util.List;

@Mapper
public interface BpmnNodeLfFormdataFieldControlMapper extends BaseMapper<BpmnNodeLfFormdataFieldControl> {
    List<LFFieldControlVO> getFieldControlByProcessNumberAndElementId(@Param("processNum") String processNum,
                                                                      @Param("elementId") String elementId);
}
