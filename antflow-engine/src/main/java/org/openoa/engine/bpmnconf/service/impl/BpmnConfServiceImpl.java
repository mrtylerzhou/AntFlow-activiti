package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.stereotype.Repository;


/**
 * @Classname BpmnConfServiceImpl
 * @Description bpmn conf service
 * @Date 2021-10-31 10:29
 * @Created by AntOffice
 */
@Repository
public class BpmnConfServiceImpl extends ServiceImpl<BpmnConfMapper, BpmnConf> implements BpmnConfService {


}
