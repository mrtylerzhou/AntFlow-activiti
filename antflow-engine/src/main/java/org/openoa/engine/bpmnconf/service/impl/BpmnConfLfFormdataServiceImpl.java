package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfLfFormdataService;
import org.springframework.stereotype.Repository;

@Repository
public class BpmnConfLfFormdataServiceImpl extends ServiceImpl<BpmnConfLfFormdataMapper, BpmnConfLfFormdata> implements BpmnConfLfFormdataService {
}
