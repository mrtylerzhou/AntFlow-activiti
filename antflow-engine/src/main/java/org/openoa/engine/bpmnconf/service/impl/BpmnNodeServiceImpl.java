package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNode;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnNodeServiceImpl extends ServiceImpl<BpmnNodeMapper, BpmnNode> implements BpmnNodeService {

    @Autowired
    private BpmnNodeToServiceImpl bpmnNodeToService;
    @Autowired
    private BpmnNodeMapper mapper;

}
