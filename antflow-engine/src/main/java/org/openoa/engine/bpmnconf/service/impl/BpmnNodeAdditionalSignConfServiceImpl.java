package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeAdditionalSignConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeAdditionalSignConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeAdditionalSignConfService;
import org.springframework.stereotype.Repository;

@Repository
public class BpmnNodeAdditionalSignConfServiceImpl extends ServiceImpl<BpmnNodeAdditionalSignConfMapper, BpmnNodeAdditionalSignConf> implements BpmnNodeAdditionalSignConfService {

}
