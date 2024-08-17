package org.openoa.engine.bpmnconf.adp.formatter;

import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.formatter.BpmnRemoveFormat;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author AntFlow
 * @since 0.5
 */
@Service
public class BpmnRemoveConfFormatFactory {

    public void removeBpmnConf (BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions){
        List<BpmnRemoveFormat> removeCopyFormat = SpringBeanUtils.getOrderedBeans(BpmnRemoveFormat.class);
        for (BpmnRemoveFormat bpmnRemoveFormat : removeCopyFormat) {
            bpmnRemoveFormat.removeBpmnConf(bpmnConfVo,bpmnStartConditions);
        }
    }
}
