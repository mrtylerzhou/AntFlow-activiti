package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeRoleOutsideEmpConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeRoleOutsideEmpConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeRoleOutsideEmpConfService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class BpmnNodeRoleOutsideEmpConfServiceImpl extends ServiceImpl<BpmnNodeRoleOutsideEmpConfMapper, BpmnNodeRoleOutsideEmpConf> implements BpmnNodeRoleOutsideEmpConfService {
}
