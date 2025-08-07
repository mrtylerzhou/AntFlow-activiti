package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.constant.enums.ViewPageTypeEnum;
import org.openoa.base.entity.*;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.BpmnInsertVariableSubs;
import org.openoa.common.constant.enus.ElementPropertyEnum;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableMessageBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnInsertVariables;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableButtonService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableSequenceFlowService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableSignUpService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableViewPageButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BpmnInsertVariablesImpl implements BpmnInsertVariables {

    @Autowired
    private BpmVariableService bpmVariableService;

    @Autowired
    private BpmVariableViewPageButtonService bpmVariableViewPageButtonService;

    @Autowired
    private BpmVariableButtonService bpmVariableButtonService;

    @Autowired
    private BpmVariableSequenceFlowService bpmVariableSequenceFlowService;

    @Autowired
    private BpmVariableSignUpService bpmVariableSignUpService;

    @Autowired
    private BpmVariableMessageBizService bpmVariableMessageBizService;


    public void insertVariables(BpmnConfCommonVo bpmnConfCommonVo, BpmnStartConditionsVo bpmnStartConditions) {
        BpmVariable bpmVariable = BpmVariable
                .builder()
                .bpmnCode(bpmnConfCommonVo.getBpmnCode())
                .processNum(bpmnConfCommonVo.getProcessNum())
                .processName(bpmnConfCommonVo.getProcessName())
                .processDesc(bpmnConfCommonVo.getProcessDesc())
                .processStartConditions(JSON.toJSONString(bpmnStartConditions))//process start condition
                .createUser(SecurityUtils.getLogInEmpIdSafe())
                .tenantId(MultiTenantUtil.getCurrentTenantId())
                .createTime(new Date())
                .build();
        bpmVariableService.getBaseMapper().insert(bpmVariable);

        Long variableId = Optional.ofNullable(bpmVariable.getId()).orElse(0L);


        // insert viewPageButton
        insertViewPageButton(bpmnConfCommonVo, variableId);

        //create a multiMap for signUp node
        Multimap<String, BpmnConfCommonElementVo> signUpMultimap = ArrayListMultimap.create();


        //get elementList
        List<BpmnConfCommonElementVo> elementList = bpmnConfCommonVo.getElementList();

        for (BpmnConfCommonElementVo elementVo : elementList) {

            Integer elementType = elementVo.getElementType();

            Integer elementProperty = elementVo.getElementProperty();

            if (elementType.equals(ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode())) {//UserTask
                Class<? extends BpmnInsertVariableSubs> bpmnInsertVariableSubs = ElementPropertyEnum.getVariableSubClsByCode(elementProperty);
                if (!ObjectUtils.isEmpty(bpmnInsertVariableSubs)) {
                    BpmnInsertVariableSubs bean = SpringBeanUtils.getBean(bpmnInsertVariableSubs);
                    if (!ObjectUtils.isEmpty(bean)) {
                        bean.insertVariableSubs(elementVo, variableId);
                    }
                }

                //设置加批节点数据
                //set nodesignup data
                if (elementVo.getIsSignUp() == 1) {
                    signUpMultimap.put(elementVo.getElementId(), new BpmnConfCommonElementVo());
                }


                if (elementVo.getIsSignUpSubElement() == 1) {
                    signUpMultimap.put(elementVo.getSignUpElementId(), elementVo);
                }

                //insert elementButton
                insertElementButton(variableId, elementVo, elementVo.getElementId());

            } else if (elementType.equals(ElementTypeEnum.ELEMENT_TYPE_SEQUENCE_FLOW.getCode())) {//SequenceFlow
                bpmVariableSequenceFlowService.getBaseMapper().insert(BpmVariableSequenceFlow
                        .builder()
                        .variableId(variableId)
                        .elementId(elementVo.getElementId())
                        .elementName(elementVo.getElementName())
                        .elementFromId(elementVo.getFlowFrom())
                        .elementToId(elementVo.getFlowTo())
                        .sequenceFlowType(1)//此版本默认无参连线
                        .tenantId(MultiTenantUtil.getCurrentTenantId())
                        .build());
            }
        }


        //insert signUp data
        insertSignUp(variableId, signUpMultimap, elementList);

        // insert message data
        bpmVariableMessageBizService.insertVariableMessage(variableId, bpmnConfCommonVo);

    }

    /**
     * insert signUp data
     *
     * @param variableId
     * @param signUpMultimap
     * @param elementList
     */
    private void insertSignUp(Long variableId, Multimap<String, BpmnConfCommonElementVo> signUpMultimap, List<BpmnConfCommonElementVo> elementList) {
        if (!signUpMultimap.isEmpty()) {
            List<BpmVariableSignUp> bpmVariableSignUps = Lists.newArrayList();
            for (String key : signUpMultimap.keySet()) {

                //筛选获得加批节点元素对象
                BpmnConfCommonElementVo elementVo = elementList
                        .stream()
                        .filter(o -> o.getElementId().equals(key))
                        .findFirst()
                        .orElse(new BpmnConfCommonElementVo());


                List<BpmnConfCommonElementVo> subElements = signUpMultimap.get(key)
                        .stream()
                        .filter(o -> !ObjectUtils.isEmpty(o) && !ObjectUtils.isEmpty(o.getElementId()))
                        .collect(Collectors.toList());


                bpmVariableSignUps.add(BpmVariableSignUp
                        .builder()
                        .variableId(variableId)
                        .afterSignUpWay(elementVo.getAfterSignUpWay())
                        .elementId(key)
                        .nodeId(elementVo.getNodeId())
                        .subElements(JSON.toJSONString(subElements))
                        .tenantId(MultiTenantUtil.getCurrentTenantId())
                        .build());
            }

            //save signup data in batch
            bpmVariableSignUpService.saveBatch(bpmVariableSignUps);
        }
    }

    /**
     * insert element button
     *
     * @param variableId
     * @param elementVo
     * @param elementId
     */
    private void insertElementButton(Long variableId, BpmnConfCommonElementVo elementVo, String elementId) {
        //node button on start up page
        if (!ObjectUtils.isEmpty(elementVo.getButtons().getStartPage())) {
            bpmVariableButtonService.saveBatch(elementVo.getButtons().getStartPage()
                    .stream()
                    .map(o -> {
                        return BpmVariableButton
                                .builder()
                                .variableId(variableId)
                                .elementId(elementId)
                                .buttonPageType(ButtonPageTypeEnum.INITIATE.getCode())
                                .buttonType(o.getButtonType())
                                .buttonName(o.getButtonName())
                                .build();
                    })
                    .collect(Collectors.toList()));
        }


        //node button on approval page
        if (!ObjectUtils.isEmpty(elementVo.getButtons().getApprovalPage())) {
            bpmVariableButtonService.saveBatch(elementVo.getButtons().getApprovalPage()
                    .stream()
                    .map(o -> {
                        return BpmVariableButton
                                .builder()
                                .variableId(variableId)
                                .elementId(elementId)
                                .buttonPageType(ButtonPageTypeEnum.AUDIT.getCode())
                                .buttonType(o.getButtonType())
                                .buttonName(o.getButtonName())
                                .build();
                    })
                    .collect(Collectors.toList()));
        }

    }

    /**
     * insert view page button
     *
     * @param bpmnConfCommonVo
     * @param variableId
     */
    private void insertViewPageButton(BpmnConfCommonVo bpmnConfCommonVo, Long variableId) {

        //get start user's view page
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getViewPageButtons().getViewPageStart())) {
            bpmVariableViewPageButtonService.saveBatch(bpmnConfCommonVo.getViewPageButtons().getViewPageStart()
                    .stream()
                    .map(o -> {
                        return BpmVariableViewPageButton
                                .builder()
                                .variableId(variableId)
                                .viewType(ViewPageTypeEnum.VIEW_PAGE_TYPE_START.getCode())
                                .buttonType(o.getButtonType())
                                .buttonName(o.getButtonName())
                                .tenantId(MultiTenantUtil.getCurrentTenantId())
                                .build();
                    })
                    .collect(Collectors.toList()));
        }

        //get other user's view page
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getViewPageButtons().getViewPageOther())) {
            bpmVariableViewPageButtonService.saveBatch(bpmnConfCommonVo.getViewPageButtons().getViewPageOther()
                    .stream()
                    .map(o -> {
                        return BpmVariableViewPageButton
                                .builder()
                                .variableId(variableId)
                                .viewType(ViewPageTypeEnum.VIEW_PAGE_TYPE_OTHER.getCode())
                                .buttonType(o.getButtonType())
                                .buttonName(o.getButtonName())
                                .tenantId(MultiTenantUtil.getCurrentTenantId())
                                .build();
                    })
                    .collect(Collectors.toList()));
        }
    }
}
