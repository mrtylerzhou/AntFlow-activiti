package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import com.google.common.collect.Lists;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmEmbedNodeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * this is a demo ordered sign node service,not contain task logic,actual business according to the specific business to find the approvers
 * @author: AntFlow
 * @since 0.5
 */
@Service
public class TestOrderedSignNodeAdp extends AbstractOrderedSignNodeAdp {
    @Override
    public List<BaseIdTranStruVo> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {

        ArrayList<BaseIdTranStruVo> baseIdTranStruVos = Lists.newArrayList(
                new BaseIdTranStruVo("1", "张三"),
                new BaseIdTranStruVo("2", "李四")
                );

        return baseIdTranStruVos;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.TEST_ORDERED_SIGN);
    }
}
