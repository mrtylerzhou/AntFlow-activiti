package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.MissingAssigneeProcessStragtegyEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnProcessAdminProvider;
import org.openoa.base.interf.MissAssigneeProcessing;
import org.openoa.base.util.FilterUtil;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.MissingAssigneeProcessStragtegyEnum.SKIP;

public abstract class AbstractMissingAssignNodeAssigneeVoProvider  extends AbstractNodeAssigneeVoProvider implements MissAssigneeProcessing {
    @Autowired
    private BpmnProcessAdminProvider bpmnProcessAdminProvider;

    @Override
    protected List<BpmnNodeParamsAssigneeVo> provideAssigneeList(BpmnNodeVo nodeVo, Collection<BaseIdTranStruVo> emplList) {
        Integer missingAssigneeDealWay = nodeVo.getNoHeaderAction();
        emplList=emplList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(nodeVo.getProperty()!=null){
            BpmnNodePropertysVo property = nodeVo.getProperty();
            List<ExtraSignInfoVo> additionalSignInfoList = property.getAdditionalSignInfoList();
            List<BaseIdTranStruVo> additionalAssigneeListToAdd=null;
            List<BaseIdTranStruVo> additionalAssigneeListToDel=null;
            if(!CollectionUtils.isEmpty(additionalSignInfoList)){
                for (ExtraSignInfoVo signInfoVo : additionalSignInfoList) {
                    Integer nodeProperty = signInfoVo.getNodeProperty();
                    Integer propertyType = signInfoVo.getPropertyType();
                    NodePropertyEnum nodePropertyEnum = NodePropertyEnum.getByCode(nodeProperty);
                    switch (nodePropertyEnum){
                        case NODE_PROPERTY_ROLE:
                            List<BaseIdTranStruVo> roleList = signInfoVo.getOtherSignInfos();
                            if(propertyType==1){//add
                                if(additionalAssigneeListToAdd==null){
                                    additionalAssigneeListToAdd=new ArrayList<>();
                                }
                                additionalAssigneeListToAdd.addAll(roleList);
                            }else if (propertyType==2){//del
                                if(additionalAssigneeListToDel==null){
                                    additionalAssigneeListToDel=new ArrayList<>();
                                }
                                additionalAssigneeListToDel.addAll(roleList);
                            }
                            break;
                            case NODE_PROPERTY_PERSONNEL:
                                List<BaseIdTranStruVo> personnelList = signInfoVo.getSignInfos();
                                if(propertyType==1) {//add
                                    if(additionalAssigneeListToAdd==null){
                                        additionalAssigneeListToAdd=new ArrayList<>();
                                    }
                                    additionalAssigneeListToAdd.addAll(personnelList);
                                }else if (propertyType==2){//del
                                    if(additionalAssigneeListToDel==null){
                                        additionalAssigneeListToDel=new ArrayList<>();
                                    }
                                    additionalAssigneeListToDel.addAll(personnelList);
                                }
                                break;
                    }
                }
            }
            if(!CollectionUtils.isEmpty(additionalAssigneeListToAdd)){
                emplList.addAll(additionalAssigneeListToAdd);
            }
            if(!CollectionUtils.isEmpty(additionalAssigneeListToDel)){
                for (BaseIdTranStruVo toDel : additionalAssigneeListToDel) {
                    List<BaseIdTranStruVo> toDelList = emplList.stream().filter(a -> a.getId().equals(toDel.getId())).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(toDelList)){
                        emplList.removeAll(toDelList);
                    }
                }
            }
        }
        //多种规则往里面添加,最终可能会有重复的
        emplList= emplList.stream().filter(FilterUtil.distinctByKeys(BaseIdTranStruVo::getId)).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(emplList)){
            return super.provideAssigneeList(nodeVo, emplList);
        }
        missingAssigneeDealWay=missingAssigneeDealWay!=null?missingAssigneeDealWay:MissingAssigneeProcessStragtegyEnum.NOT_ALLOWED.getCode();
        BaseIdTranStruVo baseIdTranStruVo = processMissAssignee(missingAssigneeDealWay);
        emplList.add(baseIdTranStruVo);
        if (MissingAssigneeProcessStragtegyEnum.getByCode(missingAssigneeDealWay)==SKIP) {
            nodeVo.getParams().setIsNodeDeduplication(1);
        }
        return super.provideAssigneeList(nodeVo, emplList);
    }
    @Override
    public BaseIdTranStruVo processMissAssignee(Integer processingWay){
        MissingAssigneeProcessStragtegyEnum processingStrategy = MissingAssigneeProcessStragtegyEnum.getByCode(processingWay);
        if(processingStrategy==null){
            return null;
        }
        switch (processingStrategy){
            case SKIP:
                return BaseIdTranStruVo.builder().id(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId()).name(AFSpecialAssigneeEnum.TO_BE_REMOVED.getDesc()).build();
            case TRANSFER_TO_ADMIN:
                BaseIdTranStruVo processAdminAndOutsideProcess = bpmnProcessAdminProvider.provideProcessAdminInfo();
                return processAdminAndOutsideProcess;
            case NOT_ALLOWED:
                throw new AFBizException("current not has no assignee!");
            default:
                return null;
        }
    }
}
