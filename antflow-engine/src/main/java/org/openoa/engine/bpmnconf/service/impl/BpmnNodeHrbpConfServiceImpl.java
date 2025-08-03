package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeHrbpConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeHrbpConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeHrbpConfService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class BpmnNodeHrbpConfServiceImpl extends ServiceImpl<BpmnNodeHrbpConfMapper, BpmnNodeHrbpConf> implements BpmnNodeHrbpConfService {

}