package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnNodeServiceImpl extends ServiceImpl<BpmnNodeMapper, BpmnNode> {

    @Autowired
    private BpmnNodeToServiceImpl bpmnNodeToService;
    @Autowired
    private BpmnNodeMapper mapper;

}
