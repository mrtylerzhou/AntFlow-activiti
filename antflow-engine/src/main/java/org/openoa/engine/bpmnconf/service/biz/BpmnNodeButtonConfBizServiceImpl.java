package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.entity.BpmnNodeButtonConf;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnNodeButtonConfBizService;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmnNodeButtonConfBizServiceImpl implements BpmnNodeButtonConfBizService {
    public List<BpmnNodeButtonConf> listByProcessNumber(String bpmnCode){
        List<BpmnNodeButtonConf> bpmnNodeButtonConfs = this.getMapper().queryConfByBpmnConde(bpmnCode);
        return bpmnNodeButtonConfs;
    }
}
