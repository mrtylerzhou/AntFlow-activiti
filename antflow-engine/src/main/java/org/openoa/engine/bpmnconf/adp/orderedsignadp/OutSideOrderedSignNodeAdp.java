package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import com.google.common.collect.Lists;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.OutSideBpmAccessEmbedNodeVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/15 22:52
 * @Version 0.5
 */
@Service
public class OutSideOrderedSignNodeAdp extends AbstractOrderedSignNodeAdp {
    @Override
    public List<String> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        String nodeMark = nodeVo.getProperty().getNodeMark();
        //outside embed node
        List<OutSideBpmAccessEmbedNodeVo> embedNodes = bpmnStartConditions.getEmbedNodes();
        if(ObjectUtils.isEmpty(nodeMark)|| CollectionUtils.isEmpty(embedNodes)){
            return Lists.newArrayList("0");
        }
        OutSideBpmAccessEmbedNodeVo embedNodeVo = embedNodes
                .stream()
                .filter(o -> o.getNodeMark().equals(nodeMark))
                .findFirst()
                .orElse(null);
        if(embedNodeVo==null){
            return Lists.newArrayList("0");
        }
        List<String> assigneeIdList = embedNodeVo.getAssigneeIdList();
        if(CollectionUtils.isEmpty(assigneeIdList)){
            return Lists.newArrayList("0");
        }

        return Lists.newArrayList(assigneeIdList);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.OUT_SIDE_NODE);
    }
}
