package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeConditionsConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnNodeConditionsConfServiceImpl extends ServiceImpl<BpmnNodeConditionsConfMapper, BpmnNodeConditionsConf> implements BpmnNodeConditionsConfService {

}
