package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.*;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.stereotype.Service;

import static org.openoa.base.constant.NumberConstants.BPMN_FLOW_TYPE_OUTSIDE;
import static org.openoa.base.constant.enums.NodeTypeEnum.NODE_TYPE_APPROVER;


/**
 * @Classname BpmnConfServiceImpl
 * @Description bpmn conf service
 * @Date 2021-10-31 10:29
 * @Created by AntOffice
 */
@Service
public class BpmnConfServiceImpl extends ServiceImpl<BpmnConfMapper, BpmnConf> implements BpmnConfService {


}
