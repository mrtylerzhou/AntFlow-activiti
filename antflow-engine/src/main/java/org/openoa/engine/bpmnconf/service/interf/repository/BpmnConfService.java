package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.interf.IAFService;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;

public interface BpmnConfService extends IAFService<BpmnConfMapper,BpmnConf> {
}
