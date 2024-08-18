package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmnNodeMapper extends BaseMapper<BpmnNode> {

    List<BpmnNode> customNode(String processKey);

}