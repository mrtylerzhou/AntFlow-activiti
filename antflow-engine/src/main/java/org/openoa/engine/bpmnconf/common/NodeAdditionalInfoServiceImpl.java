package org.openoa.engine.bpmnconf.common;

import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.entity.BpmnNodeTo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeToServiceImpl;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.base.util.AFWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.NodeTypeEnum.NODE_TYPE_APPROVER;

/**
 * designed to get addition node info
 *
 * @Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-05 11:18
 * @Param
 * @return
 * @Version 0.5
 */
@Service
public class NodeAdditionalInfoServiceImpl {
    @Autowired
    private BpmnNodeToServiceImpl bpmnNodeToService;
    @Autowired
    private IAdaptorFactory adaptorFactory;

    public Map<Long, List<String>> getBpmnNodeToMap(List<Long> idList) {
        return bpmnNodeToService.getBaseMapper().selectList(
                        AFWrappers.<BpmnNodeTo>lambdaTenantQuery()
                                .in(BpmnNodeTo::getBpmnNodeId, idList))
                .stream()
                .collect(Collectors.toMap(
                        BpmnNodeTo::getBpmnNodeId,
                        v -> Lists.newArrayList(Collections.singletonList(v.getNodeTo())),
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }));
    }
    public BpmnNodeAdaptor getBpmnNodeAdaptor(BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum) {
        if(bpmnNodeAdpConfEnum==null){
            return null;
        }
        return adaptorFactory.getBpmnNodeAdaptor(bpmnNodeAdpConfEnum);
    }
    public static BpmnNodeAdpConfEnum getBpmnNodeAdpConfEnum(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum;


        NodeTypeEnum nodeTypeEnumByCode = NodeTypeEnum.getNodeTypeEnumByCode(bpmnNodeVo.getNodeType());

        if (!ObjectUtils.isEmpty(nodeTypeEnumByCode)) {
            if (NODE_TYPE_APPROVER.equals(nodeTypeEnumByCode)) {
                NodePropertyEnum nodePropertyEnum = NodePropertyEnum.getNodePropertyEnumByCode(bpmnNodeVo.getNodeProperty());
                bpmnNodeAdpConfEnum = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(nodePropertyEnum);
            } else {
                bpmnNodeAdpConfEnum = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(nodeTypeEnumByCode);
            }
        } else {

            NodePropertyEnum nodePropertyEnum = NodePropertyEnum.getNodePropertyEnumByCode(bpmnNodeVo.getNodeProperty());
            bpmnNodeAdpConfEnum = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(nodePropertyEnum);
        }
        return bpmnNodeAdpConfEnum;
    }
}
