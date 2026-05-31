package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.base.constant.enums.ViewPageTypeEnum;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.entity.jsonconf.VariableConfigJson;
import org.openoa.base.entity.jsonconf.VariableConfigJson.*;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.*;
import org.openoa.common.adaptor.BpmnInsertVariableSubs;
import org.openoa.common.constant.enus.ElementPropertyEnum;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnInsertVariables;
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


    public void insertVariables(BpmnConfCommonVo bpmnConfCommonVo, BpmnStartConditionsVo bpmnStartConditions) {
        // 1. Insert variable first to get the ID (needed by multiplayer adaptors)
        BpmVariable bpmVariable = BpmVariable
                .builder()
                .bpmnCode(bpmnConfCommonVo.getBpmnCode())
                .processNum(bpmnConfCommonVo.getProcessNum())
                .processName(bpmnConfCommonVo.getProcessName())
                .processDesc(bpmnConfCommonVo.getProcessDesc())
                .processStartConditions(JSON.toJSONString(bpmnStartConditions, JSONWriter.Feature.WriteNonStringKeyAsString))
                .createUser(SecurityUtils.getLogInEmpIdSafe())
                .tenantId(MultiTenantUtil.getCurrentTenantId())
                .createTime(new Date())
                .build();
        bpmVariableService.getBaseMapper().insert(bpmVariable);
        Long variableId = Optional.ofNullable(bpmVariable.getId()).orElse(0L);

        // 2. Build variable config JSON
        VariableConfigJson config = new VariableConfigJson();

        // build view page buttons
        buildViewPageButtons(bpmnConfCommonVo, config);

        // create a multiMap for signUp node
        Multimap<String, BpmnConfCommonElementVo> signUpMultimap = ArrayListMultimap.create();

        // get elementList
        List<BpmnConfCommonElementVo> elementList = bpmnConfCommonVo.getElementList();

        for (BpmnConfCommonElementVo elementVo : elementList) {
            Integer elementType = elementVo.getElementType();
            Integer elementProperty = elementVo.getElementProperty();

            if (elementType.equals(ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode())) {
                // write to t_bpm_variable_multiplayer tables (kept, not part of JSON)
                Class<? extends BpmnInsertVariableSubs> bpmnInsertVariableSubs = ElementPropertyEnum.getVariableSubClsByCode(elementProperty);
                if (!ObjectUtils.isEmpty(bpmnInsertVariableSubs)) {
                    BpmnInsertVariableSubs bean = SpringBeanUtils.getBean(bpmnInsertVariableSubs);
                    if (!ObjectUtils.isEmpty(bean)) {
                        bean.insertVariableSubs(elementVo, variableId);
                    }
                }

                // set nodesignup data
                if (elementVo.getIsSignUp() == 1) {
                    signUpMultimap.put(elementVo.getElementId(), new BpmnConfCommonElementVo());
                }

                if (elementVo.getIsSignUpSubElement() == 1) {
                    signUpMultimap.put(elementVo.getSignUpElementId(), elementVo);
                }

                // build element buttons
                buildElementButtons(config, elementVo, elementVo.getElementId());
            }
        }

        // build signUp data
        buildSignUps(config, signUpMultimap, elementList);

        // build message and approveRemind data
        buildMessagesAndApproveReminds(config, bpmnConfCommonVo);

        // 3. Update variable with config JSON
        bpmVariable.setVariableConfigJson(JSON.toJSONString(config));
        bpmVariableService.getBaseMapper().updateById(bpmVariable);
    }

    private void buildSignUps(VariableConfigJson config, Multimap<String, BpmnConfCommonElementVo> signUpMultimap, List<BpmnConfCommonElementVo> elementList) {
        if (signUpMultimap.isEmpty()) {
            return;
        }
        for (String key : signUpMultimap.keySet()) {
            BpmnConfCommonElementVo elementVo = elementList
                    .stream()
                    .filter(o -> o.getElementId().equals(key))
                    .findFirst()
                    .orElse(new BpmnConfCommonElementVo());

            List<BpmnConfCommonElementVo> subElements = signUpMultimap.get(key)
                    .stream()
                    .filter(o -> !ObjectUtils.isEmpty(o) && !ObjectUtils.isEmpty(o.getElementId()))
                    .collect(Collectors.toList());

            config.getSignUps().add(SignUpItem.builder()
                    .elementId(key)
                    .nodeId(elementVo.getNodeId())
                    .afterSignUpWay(elementVo.getAfterSignUpWay())
                    .subElements(JSON.toJSONString(subElements))
                    .build());
        }
    }

    private void buildElementButtons(VariableConfigJson config, BpmnConfCommonElementVo elementVo, String elementId) {
        if (!ObjectUtils.isEmpty(elementVo.getButtons().getStartPage())) {
            elementVo.getButtons().getStartPage().forEach(o ->
                    config.getButtons().add(ButtonItem.builder()
                            .elementId(elementId)
                            .buttonPageType(ButtonPageTypeEnum.INITIATE.getCode())
                            .buttonType(o.getButtonType())
                            .buttonName(o.getButtonName())
                            .build()));
        }

        if (!ObjectUtils.isEmpty(elementVo.getButtons().getApprovalPage())) {
            elementVo.getButtons().getApprovalPage().forEach(o ->
                    config.getButtons().add(ButtonItem.builder()
                            .elementId(elementId)
                            .buttonPageType(ButtonPageTypeEnum.AUDIT.getCode())
                            .buttonType(o.getButtonType())
                            .buttonName(o.getButtonName())
                            .build()));
        }

        if (!ObjectUtils.isEmpty(elementVo.getButtons().getViewPage())) {
            elementVo.getButtons().getViewPage().forEach(o ->
                    config.getButtons().add(ButtonItem.builder()
                            .elementId(elementId)
                            .buttonPageType(ButtonPageTypeEnum.TO_VIEW.getCode())
                            .buttonType(o.getButtonType())
                            .buttonName(o.getButtonName())
                            .build()));
        }
    }

    private void buildViewPageButtons(BpmnConfCommonVo bpmnConfCommonVo, VariableConfigJson config) {
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getViewPageButtons().getViewPageStart())) {
            bpmnConfCommonVo.getViewPageButtons().getViewPageStart().forEach(o ->
                    config.getButtons().add(ButtonItem.builder()
                            .buttonPageType(3)
                            .viewType(ViewPageTypeEnum.VIEW_PAGE_TYPE_START.getCode())
                            .buttonType(o.getButtonType())
                            .buttonName(o.getButtonName())
                            .build()));
        }

        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getViewPageButtons().getViewPageOther())) {
            bpmnConfCommonVo.getViewPageButtons().getViewPageOther().forEach(o ->
                    config.getButtons().add(ButtonItem.builder()
                            .buttonPageType(3)
                            .viewType(ViewPageTypeEnum.VIEW_PAGE_TYPE_OTHER.getCode())
                            .buttonType(o.getButtonType())
                            .buttonName(o.getButtonName())
                            .build()));
        }
    }

    private void buildMessagesAndApproveReminds(VariableConfigJson config, BpmnConfCommonVo bpmnConfCommonVo) {
        // out-of-node messages
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getTemplateVos())) {
            buildMessages(config, bpmnConfCommonVo.getTemplateVos(), "", 1);
        }

        // in-node messages and approve reminds
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getElementList())) {
            for (BpmnConfCommonElementVo elementVo : bpmnConfCommonVo.getElementList()) {
                if (ObjectUtils.isEmpty(elementVo.getTemplateVos())) {
                    continue;
                }
                buildMessages(config, elementVo.getTemplateVos(), elementVo.getElementId(), 2);

                if (!ObjectUtils.isEmpty(elementVo.getApproveRemindVo()) &&
                        !ObjectUtils.isEmpty(elementVo.getApproveRemindVo().getDays())) {
                    config.getApproveReminds().add(ApproveRemindItem.builder()
                            .elementId(elementVo.getElementId())
                            .content(JSON.toJSONString(elementVo.getApproveRemindVo()))
                            .build());
                }
            }
        }
    }

    private void buildMessages(VariableConfigJson config, List<BpmnTemplateVo> templateVos, String elementId, Integer messageType) {
        templateVos.forEach(o -> {
            Integer eventType = o.getEvent();
            Integer resolvedMessageType = getMessageSendType(eventType, messageType);
            config.getMessages().add(MessageItem.builder()
                    .elementId(elementId)
                    .messageType(resolvedMessageType)
                    .eventType(eventType)
                    .content(JSON.toJSONString(o))
                    .build());
        });
    }

    private Integer getMessageSendType(Integer event, Integer defaultMessageSendType) {
        if (event == null) {
            return defaultMessageSendType;
        }
        EventTypeEnum eventTypeEnum = EventTypeEnum.getByCode(event);
        if (eventTypeEnum == null) {
            return defaultMessageSendType;
        }
        return eventTypeEnum.getIsInNode() ? 2 : 1;
    }
}
