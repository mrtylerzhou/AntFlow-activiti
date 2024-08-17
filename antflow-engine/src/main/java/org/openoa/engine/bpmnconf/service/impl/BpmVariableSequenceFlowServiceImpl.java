package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSequenceFlow;
import org.openoa.engine.bpmnconf.mapper.BpmVariableSequenceFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname BpmVariableSequenceFlowServiceImpl
 * @Description TODO
 * @Date 2021-11-27 15:39
 * @Created by AntOffice
 */
@Service
public class BpmVariableSequenceFlowServiceImpl extends ServiceImpl<BpmVariableSequenceFlowMapper, BpmVariableSequenceFlow> {
    @Autowired
    private BpmVariableSequenceFlowMapper mapper;
}
