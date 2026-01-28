package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmVariableButton;
import org.openoa.engine.bpmnconf.mapper.BpmVariableButtonMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableButtonService;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @Classname BpmVariableButtonServiceImpl
 * @Date 2021-11-27 15:29
 * @Created by AntOffice
 */
@Repository
public class BpmVariableButtonServiceImpl extends ServiceImpl<BpmVariableButtonMapper, BpmVariableButton>  implements BpmVariableButtonService {


    public List<BpmVariableButton> getButtonsByProcessNumber(String processNum, Collection<String> elementIds) {

        return this.getBaseMapper().getButtonsByProcessNumber(processNum, elementIds);
    }
}
