package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.LFFieldControlVO;
import org.openoa.base.entity.BpmnNodeLfFormdataFieldControl;

import java.util.List;

@Mapper
public interface BpmnNodeLfFormdataFieldControlMapper extends BaseMapper<BpmnNodeLfFormdataFieldControl> {
    List<LFFieldControlVO> getFieldControlByProcessNumberAndElementId(@Param("processNum") String processNum,
                                                                      @Param("elementId") String elementId);

    /**
     * 根据审批节点nodeId 获取当前节点的表单权限设置
     * @param nodeId t_bpmn_node.id
     * @return List<LFFieldControlVO>
     */
    List<LFFieldControlVO> getFieldControlByNodeId(@Param("nodeId") Long nodeId);
}
