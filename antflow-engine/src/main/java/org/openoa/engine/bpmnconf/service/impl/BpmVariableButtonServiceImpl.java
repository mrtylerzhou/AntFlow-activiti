package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmVariableButton;
import org.openoa.engine.bpmnconf.mapper.BpmVariableButtonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname BpmVariableButtonServiceImpl
 * @Date 2021-11-27 15:29
 * @Created by AntOffice
 */
@Service
public class BpmVariableButtonServiceImpl extends ServiceImpl<BpmVariableButtonMapper, BpmVariableButton> {


    public List<BpmVariableButton> getButtonsByProcessNumber(String processNum, String elementId) {

        return this.getBaseMapper().getButtonsByProcessNumber(processNum, elementId);
    }
}
