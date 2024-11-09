package org.openoa.engine.bpmnconf.adp.formatter;

import com.google.common.collect.Lists;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.formatter.AbstractBpmnRemoveFormat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author AntFlow
 * @since 0.5
 */
@Component
public class BpmnRemoveFormatImpl extends AbstractBpmnRemoveFormat {

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void removeBpmnConf(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions) {
        super.removeBpmnConf(bpmnConfVo,bpmnStartConditions);
    }

    @Override
    public List<Supplier<Boolean>> trueSuppliers(BpmnNodeVo vo, BpmnStartConditionsVo bpmnStartConditionsVo) {
       Supplier<Boolean> supplier1= ()->vo.getParams().getParamType().equals(1) &&
                vo.getParams().getAssignee().getAssignee().equals("0");
       Supplier<Boolean> supplier2=()->vo.getParams().getParamType().equals(2) &&
               vo.getParams().getAssigneeList().get(0).getAssignee().equals("0");
       return Lists.newArrayList(supplier1,supplier2);
    }

}
