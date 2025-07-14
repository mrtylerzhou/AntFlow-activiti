package org.openoa.engine.bpmnconf.adp.formatter;


import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.formatter.AbstractBpmnRemoveFormat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * remove forward copy node
 * @author AntFlow
 * @since 0.5
 */
@Service
public class BpmnRemoveCopyFormatImpl extends AbstractBpmnRemoveFormat {
    @Override
    public int order() {
        return 2;
    }

    @Override
    public void removeBpmnConf(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions) {
        super.removeBpmnConf(bpmnConfVo,bpmnStartConditions);
    }

    @Override
    public List<Supplier<Boolean>> trueSuppliers(BpmnNodeVo vo, BpmnStartConditionsVo bpmnStartConditions) {
        Supplier<Boolean> supplier = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return vo.getNodeType().equals(NodeTypeEnum.NODE_TYPE_COPY.getCode());
            }
        };
        if(supplier.get()){
            if (bpmnStartConditions.getEmpToForwardList() == null) {
                bpmnStartConditions.setEmpToForwardList(new ArrayList<>());
            }
            vo.setEmpToForwardList(vo.getProperty().getEmplList());
            bpmnStartConditions.getEmpToForwardList().addAll(vo.getProperty().getEmplIds());
        }
        return Lists.newArrayList(supplier);
    }
}
