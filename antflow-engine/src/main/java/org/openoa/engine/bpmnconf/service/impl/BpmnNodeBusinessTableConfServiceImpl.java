package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeBusinessTableConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeBusinessTableConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeBusinessTableConfService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class BpmnNodeBusinessTableConfServiceImpl extends ServiceImpl<BpmnNodeBusinessTableConfMapper, BpmnNodeBusinessTableConf>  implements BpmnNodeBusinessTableConfService {

}
