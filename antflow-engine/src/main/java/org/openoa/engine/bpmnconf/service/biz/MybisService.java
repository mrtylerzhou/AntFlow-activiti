package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname MybisService
 * @Date 2021-10-31 16:04
 * @Created by AntOffice
 */
@Service
public class MybisService extends BizServiceImpl<BpmnConfServiceImpl> {
    public void getit() {
        List<BpmnConf> list = getService().list();
    }
}
