package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeUDRConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeUDRConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeUDRConfService;
import org.springframework.stereotype.Repository;

@Repository
public class BpmnNodeUDRConfServiceImpl extends ServiceImpl<BpmnNodeUDRConfMapper,BpmnNodeUDRConf> implements BpmnNodeUDRConfService {
}
