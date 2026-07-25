package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BpmEmbedNodeVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author TylerZhou
 * @Date 2024/7/15 22:52
 * @Version 0.5
 */
@Service
public class OutSideOrderedSignNodeAdp extends AbstractOrderedSignNodeAdp {
    @Override
    public List<List<BaseIdTranStruVo>> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        String nodeMark = nodeVo.getProperty().getNodeMark();
        //outside embed node
        Map<String,BpmEmbedNodeVo> embedNodes = bpmnStartConditions.getEmbedNodes();
        if(ObjectUtils.isEmpty(nodeMark)|| CollectionUtils.isEmpty(embedNodes)){
            return Lists.newArrayList(Collections.singleton(Lists.newArrayList(AFSpecialAssigneeEnum.buildToBeRemoved())));
        }
        BpmEmbedNodeVo embedNodeVo = embedNodes.get(nodeMark);
        if(embedNodeVo==null){
            return Lists.newArrayList(Collections.singleton(Lists.newArrayList()));
        }
        List<List<BaseIdTranStruVo>> assigneeList = embedNodeVo.getAssigneeList();
        if(CollectionUtils.isEmpty(assigneeList)){
            return Lists.newArrayList(Collections.singleton(Lists.newArrayList(AFSpecialAssigneeEnum.buildToBeRemoved())));
        }

        //包法 X:每个元素独立成一层(每层 1 人),保持链式语义
        List<List<BaseIdTranStruVo>> result = new ArrayList<>();
        for (List<BaseIdTranStruVo> layer : assigneeList) {
            if (CollectionUtils.isEmpty(layer)) {
                continue;
            }
            for (BaseIdTranStruVo vo : layer) {
                List<BaseIdTranStruVo> singleLayer = new ArrayList<>();
                singleLayer.add(vo);
                result.add(singleLayer);
            }
        }
        return result;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.OUT_SIDE_NODE);
    }
}
