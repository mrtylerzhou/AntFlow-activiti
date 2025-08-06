package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeLoopConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeLoopConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeLoopConfService;
import org.springframework.stereotype.Repository;

@Repository
public class BpmnNodeLoopConfServiceImpl extends ServiceImpl<BpmnNodeLoopConfMapper, BpmnNodeLoopConf> implements BpmnNodeLoopConfService {

}
