package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.activiti.engine.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.entity.jsonconf.VariableConfigJson;
import org.openoa.base.entity.jsonconf.VariableConfigJson.*;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpPersonnelBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.openoa.common.constant.enus.ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL;

@Service
public class BpmVariableSignUpPersonnelBizServiceImpl implements BpmVariableSignUpPersonnelBizService {
    @Autowired
    private BpmVariableServiceImpl bpmVariableService;

    /**
     * insert signup personnel and modify task variable (read-modify-write on JSON)
     */
    @Override
    public void insertSignUpPersonnel(TaskService taskService, String taskId, String processNumber, String nodeId, String assignee, List<BaseIdTranStruVo> signUpUsers) {

        if (ObjectUtils.isEmpty(signUpUsers)) {
            return;
        }

        // query variable main table's info by process number
        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>()
                .eq("process_num", processNumber)
                .eq("is_del", 0));

        if (ObjectUtils.isEmpty(bpmVariable) || ObjectUtils.isEmpty(bpmVariable.getVariableConfigJson())) {
            return;
        }

        // parse variable config JSON
        VariableConfigJson config = JSON.parseObject(bpmVariable.getVariableConfigJson(), VariableConfigJson.class);
        if (config == null || ObjectUtils.isEmpty(config.getSignUps())) {
            return;
        }

        // find matching signUp by elementId == nodeId
        SignUpItem signUp = config.getSignUps().stream()
                .filter(s -> nodeId.equals(s.getElementId()))
                .findFirst()
                .orElse(null);

        if (signUp == null || ObjectUtils.isEmpty(signUp.getSubElements())) {
            return;
        }

        // deserialize sub elements
        List<BpmnConfCommonElementVo> subElementVos = JSON.parseArray(signUp.getSubElements(), BpmnConfCommonElementVo.class);

        // get sign up node (non-back-sign-up)
        BpmnConfCommonElementVo signUpElement = subElementVos.stream()
                .filter(o -> o.getIsBackSignUp() == 0)
                .findFirst()
                .orElse(new BpmnConfCommonElementVo());

        List<String> signeeUpAssignees = signUpUsers.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        // set sign up node parameter
        taskService.setVariable(taskId, signUpElement.getCollectionName(), signeeUpAssignees);

        // if it is serial sign up, also set loop size param
        if (!ObjectUtils.isEmpty(signUpElement.getElementProperty()) && signUpElement.getElementProperty().intValue() == ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode()) {
            taskService.setVariable(taskId, StringUtils.join(signUpElement.getElementId(), "size"), signUpUsers.size());
        }

        // build personnel for signUp element
        List<PersonnelItem> signUpPersonnel = signUpUsers.stream()
                .map(o -> PersonnelItem.builder()
                        .assignee(o.getId())
                        .assigneeName(o.getName())
                        .build())
                .collect(Collectors.toList());

        // replace personnel for this sub-element (delete old + insert new)
        signUp.getPersonnelByElement().put(signUpElement.getElementId(), signUpPersonnel);

        BpmnConfCommonElementVo backSignUpElement = null;
        if (signUp.getAfterSignUpWay() != null && signUp.getAfterSignUpWay() == 1) {
            // back to sign-up user
            backSignUpElement = subElementVos.stream()
                    .filter(o -> o.getIsBackSignUp() == 1)
                    .findFirst()
                    .orElse(new BpmnConfCommonElementVo());

            // set variables
            List<String> assignees = Lists.newArrayList(assignee);
            taskService.setVariable(taskId, backSignUpElement.getCollectionName(), assignees);
            taskService.setVariable(taskId, StringUtils.join(backSignUpElement.getElementId(), "size"), assignees.size());

            // add back-sign-up personnel
            signUp.getPersonnelByElement().put(backSignUpElement.getElementId(),
                    Arrays.asList(PersonnelItem.builder()
                            .assignee(assignee)
                            .assigneeName(SecurityUtils.getLogInEmpName())
                            .build()));
        }

        // serialize and write back
        bpmVariable.setVariableConfigJson(JSON.toJSONString(config));
        bpmVariableService.getBaseMapper().updateById(bpmVariable);
    }
}
