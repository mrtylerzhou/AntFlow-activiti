package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeRoleConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeRoleConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeRoleConfService;
import org.springframework.stereotype.Service;

@Service
public class BpmnNodeRoleConfServiceImpl extends ServiceImpl<BpmnNodeRoleConfMapper, BpmnNodeRoleConf> implements BpmnNodeRoleConfService {

}
