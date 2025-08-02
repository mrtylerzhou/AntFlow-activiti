package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeCustomizeConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeCustomizeConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeCustomizeConfService;
import org.springframework.stereotype.Service;

@Service
public class BpmnNodeCustomizeConfServiceImpl extends ServiceImpl<BpmnNodeCustomizeConfMapper, BpmnNodeCustomizeConf> implements BpmnNodeCustomizeConfService {
}
