package org.openoa.engine.bpmnconf.adp.formatter;

import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.util.SpringBeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author: AntFlow
 * @since 0.5
 */
@Service
public class BpmnStartFormatFactory {
   public void formatBpmnConf(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions){
        Collection<BpmnStartFormat> startFormats = SpringBeanUtils.getBeans(BpmnStartFormat.class);
        for (BpmnStartFormat startFormat : startFormats) {
            startFormat.formatBpmnConf(bpmnConfVo,bpmnStartConditions);
        }
    }
}
