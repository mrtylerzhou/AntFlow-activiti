package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeConditionsConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeConditionsConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsConfService;
import org.springframework.stereotype.Repository;

@Repository
public class BpmnNodeConditionsConfServiceImpl extends ServiceImpl<BpmnNodeConditionsConfMapper, BpmnNodeConditionsConf> implements BpmnNodeConditionsConfService {

}
