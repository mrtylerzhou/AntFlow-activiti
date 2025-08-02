package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmVariableViewPageButton;
import org.openoa.engine.bpmnconf.mapper.BpmVariableViewPageButtonMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableViewPageButtonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmVariableViewPageButtonServiceImpl extends ServiceImpl<BpmVariableViewPageButtonMapper, BpmVariableViewPageButton> implements BpmVariableViewPageButtonService {


    /**
     * get process view page button
     *
     * @param processNum
     * @return
     */
    public List<BpmVariableViewPageButton> getButtonsByProcessNumber(String processNum) {

        return this.getBaseMapper().getButtonsByProcessNumber(processNum);
    }
}
