package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsParamConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeConditionsParamConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsParamConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnNodeConditionsParamConfServiceImpl extends ServiceImpl<BpmnNodeConditionsParamConfMapper, BpmnNodeConditionsParamConf> implements BpmnNodeConditionsParamConfService {

}
