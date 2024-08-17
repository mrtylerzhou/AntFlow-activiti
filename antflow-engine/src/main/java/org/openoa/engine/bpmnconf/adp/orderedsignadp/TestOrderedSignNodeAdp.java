package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import com.google.common.collect.Lists;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * this is a demo ordered sign node service,not contain task logic,actual business according to the specific business to find the approvers
 * @author: AntFlow
 * @since 0.5
 */
@Service
public class TestOrderedSignNodeAdp extends AbstractOrderedSignNodeAdp {
    @Override
    public List<String> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        return Lists.newArrayList("1","21","23","42");
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.TEST_ORDERED_SIGN);
    }
}
