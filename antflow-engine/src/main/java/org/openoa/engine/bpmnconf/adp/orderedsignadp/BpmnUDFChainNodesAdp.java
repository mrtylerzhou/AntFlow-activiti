package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BpmEmbedNodeVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
public class BpmnUDFChainNodesAdp  extends AbstractOrderedSignNodeAdp {
    @Override
    public List<BaseIdTranStruVo> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        Map<String, BpmEmbedNodeVo> embedNodes = bpmnStartConditions.getEmbedNodes();
        if(CollectionUtils.isEmpty(embedNodes)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL,"current node is a chain sign node,but does not provide required parameters embedNodes");
        }
        BpmEmbedNodeVo bpmEmbedNodeVo = embedNodes.get(nodeVo.getId().toString());
        if(bpmEmbedNodeVo==null){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL,"current node is a chain sign node,but can not get required parameters from map");
        }
        List<BaseIdTranStruVo> assigneeList = bpmEmbedNodeVo.getAssigneeList();
        if(CollectionUtils.isEmpty(assigneeList)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL,"current node is a chain sign node,but can not get required parameters assigneeList");
        }

        return assigneeList;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.UDF_CHAIN_NODES);
    }
}
