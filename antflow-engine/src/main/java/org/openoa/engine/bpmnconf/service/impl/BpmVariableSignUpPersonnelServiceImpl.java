package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.activiti.engine.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.service.BpmVariableSignUpPersonnelService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUp;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUpPersonnel;
import org.openoa.engine.bpmnconf.mapper.BpmVariableSignUpPersonnelMapper;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openoa.common.constant.enus.ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL;

@Service
public class BpmVariableSignUpPersonnelServiceImpl extends ServiceImpl<BpmVariableSignUpPersonnelMapper, BpmVariableSignUpPersonnel> implements BpmVariableSignUpPersonnelService {


    @Autowired
    @Lazy
    private BpmVariableServiceImpl bpmVariableService;

    @Autowired
    private BpmVariableSignUpServiceImpl bpmVariableSignUpService;

    /**
     * insert signup personnel and modify task variable
     *
     * @param taskService
     * @param taskId
     * @param processNumber
     * @param nodeId
     * @param assignee
     * @param signUpUsers
     */
    public void insertSignUpPersonnel(TaskService taskService, String taskId, String processNumber, String nodeId, String assignee, List<BaseIdTranStruVo> signUpUsers) {

        if (ObjectUtils.isEmpty(signUpUsers)) {
            return;
        }


        //query variable main table's info by process number
        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>()
                .eq("process_num", processNumber)
                .eq("is_del", 0));


        //to check whether bpm variable is empty,if empty return
        if (ObjectUtils.isEmpty(bpmVariable)) {
            return;
        }


        //query to check whether bpm signup variable sign up is empty,if empty return
        BpmVariableSignUp bpmVariableSignUp = bpmVariableSignUpService.getBaseMapper().selectOne(new QueryWrapper<BpmVariableSignUp>()
                .eq("variable_id", bpmVariable.getId())
                .eq("element_id", nodeId));


        //if bpm signup variable sign up is empty,return
        if (ObjectUtils.isEmpty(bpmVariableSignUp) || ObjectUtils.isEmpty(bpmVariableSignUp.getSubElements())) {
            return;
        }



        //deserialize sub element
        List<BpmnConfCommonElementVo> subElementVos = JSON.parseArray(bpmVariableSignUp.getSubElements(), BpmnConfCommonElementVo.class);

        //get sign up node
        BpmnConfCommonElementVo signUpElement = subElementVos.stream().filter(o -> o.getIsBackSignUp() == 0).findFirst().orElse(new BpmnConfCommonElementVo());

        List<String> signeeUpAssignees = signUpUsers.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        //set sign up node parameter
        taskService.setVariable(taskId, signUpElement.getCollectionName(), signeeUpAssignees);


        //if it is serial sign up,we also need to set loop size param
        if (!ObjectUtils.isEmpty(signUpElement.getElementProperty()) && signUpElement.getElementProperty().intValue() == ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode()) {
            taskService.setVariable(taskId, StringUtils.join(signUpElement.getElementId(), "size"), signUpUsers.size());
        }
        BpmnConfCommonElementVo backSignUpElement=null;
        if (bpmVariableSignUp.getAfterSignUpWay() == 1) {//come back to the node who add the sign up node

            //back
           backSignUpElement= subElementVos.stream().filter(o -> o.getIsBackSignUp() == 1).findFirst().orElse(new BpmnConfCommonElementVo());

            //set variables
            List<String> assignees = Lists.newArrayList(assignee);
            taskService.setVariable(taskId, backSignUpElement.getCollectionName(), assignees);
            taskService.setVariable(taskId, StringUtils.join(backSignUpElement.getElementId(), "size"), assignees.size());
        }

        //set corresponding node sign up personnel info(delete old one and then insert)
        this.getBaseMapper().delete(new QueryWrapper<BpmVariableSignUpPersonnel>()
                .eq("variable_id", bpmVariable.getId())
                .eq("element_id", signUpElement.getElementId()));
        this.saveBatch(signUpUsers
                .stream()
                .map(o -> BpmVariableSignUpPersonnel
                        .builder()
                        .variableId(bpmVariable.getId())
                        .assignee(o.getId())
                        .assigneeName(o.getName())
                        .elementId(signUpElement.getElementId())
                        .build())
                .collect(Collectors.toList()));
        if(backSignUpElement!=null){
            this.save(BpmVariableSignUpPersonnel.builder()
                    .variableId(bpmVariable.getId())
                    .assignee(assignee)
                    .assigneeName(SecurityUtils.getLogInEmpName())
                    .elementId(backSignUpElement.getElementId())
                    .build());
        }
    }
    public List<BaseIdTranStruVo> getSignUpInfoByVariableAndElementId(Long variableId,String elementId){
        List<BaseIdTranStruVo> byVariableIdAndElementId = this.getBaseMapper().getByVariableIdAndElementId(variableId, elementId);
        return byVariableIdAndElementId;
    }

    @Override
    public Map<String, String> getByProcessNumAndElementId(String processNumber, String elementId) {
        List<BaseIdTranStruVo> assigneeList = this.getBaseMapper().getByProcessNumAndElementId(processNumber, elementId);
        Map<String, String> assigneeMap = assigneeList.stream().collect(Collectors.toMap(BaseIdTranStruVo::getId, BaseIdTranStruVo::getName, (k1, k2) -> k1));
        return assigneeMap;
    }
}
