package org.openoa.engine.bpmnconf.common;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.constant.enums.ConfigFlowButtonSortEnum;
import org.openoa.base.constant.enums.ProcessButtonEnum;
import org.openoa.base.dto.NodeXelementXvarXverifyInfo;
import org.openoa.base.entity.*;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.ProcessActionButtonVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableButtonServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableViewPageButtonServiceImpl;
import org.openoa.base.constant.enums.ProcessStateEnum;

import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnNodeButtonConfBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.common.constant.enus.ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_PARALLEL_OR;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-04 9:25
 * @Param
 * @return
 * @Version 0.5
 */
@Component
public class ConfigFlowButtonContans {
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmVariableButtonServiceImpl bpmVariableButtonService;
    @Autowired
    private BpmVariableViewPageButtonServiceImpl bpmVariableViewPageButtonService;
    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
    @Autowired
    private BpmVariableSignUpBizService bpmVariableSignUpBizService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ActHiTaskinstServiceImpl actHiTaskinstService;
    @Autowired
    private BpmnNodeButtonConfBizService bpmnNodeButtonConfBizService;

    /**get pc buttons
     * @param elementId  elementId
     * @param processNum process number
     * @param isJurisdiction  whether has monitoring permission false for no and true for yes
     * @param isInitiate whether is initiate
     * @return initiate=start page，audit=approvement page，toView=view page
     * @date 20190620
     */
    public Map<String, List<ProcessActionButtonVo>> getButtons(String processNum, String elementId, List<String> viewNodeIds,
                                                               Boolean isJurisdiction, Boolean isInitiate) {
        Map<String, List<ProcessActionButtonVo>> buttonMap = new HashMap<String, List<ProcessActionButtonVo>>();


        List<ProcessActionButtonVo> initiateButtons = new ArrayList<>();

        List<ProcessActionButtonVo> auditButtons = new ArrayList<>();

        List<ProcessActionButtonVo> toViewButtons = new ArrayList<>();


        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNum);
        if (bpmBusinessProcess == null || bpmBusinessProcess.getProcessState() == null
                || bpmBusinessProcess.getProcessState() == ProcessStateEnum.HANDLING_STATE.getCode()) {//审批中

            if (processNum != null && StringUtils.hasText(elementId)) {
                List<BpmVariableButton> bpmVariableButtons = bpmVariableButtonService
                        .getButtonsByProcessNumber(processNum, Lists.newArrayList(elementId));
                initiateButtons = getButtons(bpmVariableButtons,ButtonPageTypeEnum.INITIATE);
                auditButtons = getButtons(bpmVariableButtons,ButtonPageTypeEnum.AUDIT);
            }

            if (processNum != null) {
                if(!CollectionUtils.isEmpty(viewNodeIds)){
                    List<BpmVariableButton> bpmVariableButtons = bpmVariableButtonService
                            .getButtonsByProcessNumber(processNum, viewNodeIds);
                    toViewButtons=getButtons(bpmVariableButtons,ButtonPageTypeEnum.TO_VIEW);
                }
                List<BpmVariableViewPageButton> bpmVariableViewPageButtons = bpmVariableViewPageButtonService
                        .getButtonsByProcessNumber(processNum);

                List<ProcessActionButtonVo> globalViewButtons = toViewButtons(bpmVariableViewPageButtons, isInitiate);
                if(!CollectionUtils.isEmpty(globalViewButtons)){
                    toViewButtons.addAll(globalViewButtons);
                }
                //节点单独配置覆盖全局的,由于查看页并没有当前节点概念,因此取的是当前审批人所在的所有节点的按钮权限
                List<ProcessActionButtonVo> nodeConfButtons = getNodeConfButtons(bpmBusinessProcess,isInitiate);

                if(!CollectionUtils.isEmpty(nodeConfButtons)){
                    toViewButtons.addAll(nodeConfButtons);
                }
            }
            if (isJurisdiction) {

                // if has monitor permission, add change handler and end button
                ProcessActionButtonVo change = ProcessActionButtonVo.builder()
                        .buttonType(ButtonTypeEnum.BUTTON_TYPE_CHANGE_ASSIGNEE.getCode())
                        .name(ButtonTypeEnum.BUTTON_TYPE_CHANGE_ASSIGNEE.getDesc())
                        .show(ProcessButtonEnum.DEAL_WITH_TYPE.getCode())
                        .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build();

                ProcessActionButtonVo end = ProcessActionButtonVo.builder()
                        .buttonType(ButtonTypeEnum.BUTTON_TYPE_STOP.getCode())
                        .name(ButtonTypeEnum.BUTTON_TYPE_STOP.getDesc())
                        .show(ProcessButtonEnum.DEAL_WITH_TYPE.getCode())
                        .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build();

                // initiateButtons.add(end);
                // initiateButtons.add(change);

                // auditButtons.add(end);
                // auditButtons.add(change);

                toViewButtons.add(end);
                toViewButtons.add(change);
            }

            String procInstId=Optional.ofNullable(bpmBusinessProcess).map(BpmBusinessProcess::getProcInstId).orElse("");
            //when is more node,if yes then add undertake button
            if (isMoreNode(processNum,procInstId, elementId)) {
                // add undertake button
                ProcessActionButtonVo undertake = ProcessActionButtonVo.builder()
                        .buttonType(ButtonTypeEnum.BUTTON_TYPE_UNDERTAKE.getCode())
                        .name(ButtonTypeEnum.BUTTON_TYPE_UNDERTAKE.getDesc()).show(ProcessButtonEnum.DEAL_WITH_TYPE.getCode())
                        .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build();
                auditButtons.clear();
                auditButtons.add(undertake);
            }
        } else if (bpmBusinessProcess.getProcessState() == ProcessStateEnum.HANDLED_STATE.getCode()
                //|| bpmBusinessProcess.getProcessState() == ProcessStateEnum.DISAGREE_STATE.getCode()
                || bpmBusinessProcess.getProcessState() == ProcessStateEnum.REJECT_STATE.getCode()
                || bpmBusinessProcess.getProcessState() == ProcessStateEnum.END_STATE.getCode()) {

            // process complete
            List<ProcessActionButtonVo> toViewButtonsComplete = new ArrayList<>();

            //query view page button
            if (processNum != null) {
                List<BpmVariableViewPageButton> bpmVariableViewPageButtons = bpmVariableViewPageButtonService
                        .getButtonsByProcessNumber(processNum);

                toViewButtons = toViewButtons(bpmVariableViewPageButtons, isInitiate);

                //节点单独配置覆盖全局的,由于查看页并没有当前节点概念,因此取的是当前审批人所在的所有节点的按钮权限
                List<ProcessActionButtonVo> nodeConfButtons = getNodeConfButtons(bpmBusinessProcess,isInitiate);
                if(!CollectionUtils.isEmpty(nodeConfButtons)){
                    toViewButtons=nodeConfButtons;
                }
                //process complete, filter invalid button
                for (ProcessActionButtonVo processActionButtonVo : toViewButtons) {
                    if (!processActionButtonVo.getButtonType().equals(ButtonTypeEnum.BUTTON_TYPE_ABANDONED.getCode())
                    ||!processActionButtonVo.getButtonType().equals(ButtonTypeEnum.BUTTON_TYPE_PROCESS_DRAW_BACK.getCode())
                            ||!processActionButtonVo.getButtonType().equals(ButtonTypeEnum.BUTTON_TYPE_STOP.getCode())
                            ||!processActionButtonVo.getButtonType().equals(ButtonTypeEnum.BUTTON_TYPE_SUBMIT.getCode())
                            ||!processActionButtonVo.getButtonType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())
                    ) {
                        toViewButtonsComplete.add(processActionButtonVo);
                    }

                }


                //stat page and aprovement page also set the view page button data
                initiateButtons.addAll(toViewButtonsComplete);
                auditButtons.addAll(toViewButtonsComplete);
                toViewButtons = toViewButtonsComplete;
            }

        }


        buttonMap.put(ButtonPageTypeEnum.INITIATE.getName(), buttonsSort(NodeUtil.repeatButtonFilter(initiateButtons)));

        buttonMap.put(ButtonPageTypeEnum.AUDIT.getName(), buttonsSort(NodeUtil.repeatButtonFilter(auditButtons)));

        buttonMap.put(ButtonPageTypeEnum.TO_VIEW.getName(), buttonsSort(NodeUtil.repeatButtonFilter(toViewButtons)));
        return buttonMap;
    }

    private List<ProcessActionButtonVo> toViewButtons(List<BpmVariableViewPageButton> btnVarList, Boolean isInitiate) {
        List<ProcessActionButtonVo> buttonlist = new ArrayList<ProcessActionButtonVo>();
        for (BpmVariableViewPageButton item : btnVarList) {
            if (isInitiate) {
                if (item.getViewType() == 1) {
                    buttonlist.add(ProcessActionButtonVo.builder().buttonType(item.getButtonType())
                            .name(item.getButtonName()).show(ProcessButtonEnum.VIEW_TYPE.getCode())
                            .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build());
                }
            } else {
                if (item.getViewType() == 2) {
                    buttonlist.add(ProcessActionButtonVo.builder().buttonType(item.getButtonType())
                            .name(item.getButtonName()).show(ProcessButtonEnum.VIEW_TYPE.getCode())
                            .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build());
                }
            }
        }
        return buttonlist;
    }

    private List<ProcessActionButtonVo> getButtons(List<BpmVariableButton> bpmVariableButtons,ButtonPageTypeEnum buttonPageTypeEnum) {
        List<ProcessActionButtonVo> buttonlist = new ArrayList<>();
        for (BpmVariableButton bpmVariableButton : bpmVariableButtons) {
            if (bpmVariableButton.getButtonPageType() == buttonPageTypeEnum.getCode()) {
                buttonlist.add(ProcessActionButtonVo.builder().buttonType(bpmVariableButton.getButtonType())
                        .name(bpmVariableButton.getButtonName()).show(ProcessButtonEnum.DEAL_WITH_TYPE.getCode())
                        .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build());
            }
        }
        return buttonlist;
    }
    /**
     * to check whether is more node and is or sign,and does not  undertaked
     * @param processNum
     * @param elementId
     * @return
     */
    public boolean isMoreNode(String processNum,String procInstId, String elementId) {
        List<BpmVariableMultiplayer> list = bpmVariableMultiplayerService.isMoreNode(processNum, elementId);
        if(list==null){
            List<BpmVariableSignUp> signUpList = bpmVariableSignUpBizService.getSignUpList(processNum);
            if(!CollectionUtils.isEmpty(signUpList)){
                List<String> subElementStrs = signUpList.stream().map(BpmVariableSignUp::getSubElements).collect(Collectors.toList());
                for (String subElementStr : subElementStrs) {
                    List<BpmnConfCommonElementVo> bpmnConfCommonElementVos = JSON.parseArray(subElementStr, BpmnConfCommonElementVo.class);
                    if(!CollectionUtils.isEmpty(bpmnConfCommonElementVos)){
                        BpmnConfCommonElementVo bpmnConfCommonElementVo = bpmnConfCommonElementVos.get(0);
                        if(bpmnConfCommonElementVo.getElementId().equals(elementId)&&ELEMENT_PROPERTY_SIGN_UP_PARALLEL_OR.getCode().equals(bpmnConfCommonElementVo.getElementProperty())){
                            List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).taskDefinitionKey(elementId).list();
                            return tasks.size()>1;
                        }
                    }

                }
            }
        }
        // if it is more node and is or sign,and does not  undertaked,and has more than one,return undertake button
        return list != null && list.size() > 1 && list.get(0).getSignType() == 2;
    }
    // ordering the buttons
    private List<ProcessActionButtonVo> buttonsSort(List<ProcessActionButtonVo> buttons) {
        buttons.sort((o1, o2) -> {
            ConfigFlowButtonSortEnum sort1 = ConfigFlowButtonSortEnum.getEnumByCode(o1.getButtonType());
            ConfigFlowButtonSortEnum sort2 = ConfigFlowButtonSortEnum.getEnumByCode(o2.getButtonType());
            if(sort1==null||sort2==null){
                return 0;
            }
            return sort1.getSort() - sort2.getSort();
        });
        return buttons;
    }


    private List<ProcessActionButtonVo> getNodeConfButtons(BpmBusinessProcess bpmBusinessProcess,Boolean isInitiate){
        List<BpmnNodeButtonConf> bpmnNodeButtonConfs=null;
        if(isInitiate){
            bpmnNodeButtonConfs= bpmnNodeButtonConfBizService.getMapper().queryConfByBpmnConde(bpmBusinessProcess.getVersion());

        }else{
            List<String> hisTaskDefKeys = actHiTaskinstService
                    .queryRecordsByProcInstId(bpmBusinessProcess.getProcInstId())
                    .stream().filter(a -> a.getEndTime() != null&& SecurityUtils.getLogInEmpIdSafe().equals(a.getAssignee()))
                    .map(ActHiTaskinst::getTaskDefKey)
                    .distinct()
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(hisTaskDefKeys)){
                List<NodeXelementXvarXverifyInfo> nodeIdsByElementIds = bpmVariableMultiplayerService.getBaseMapper().getNodeIdsByElementIds(bpmBusinessProcess.getBusinessNumber(), hisTaskDefKeys);
                if (!nodeIdsByElementIds.isEmpty()) {
                    List<String> nodeIds = nodeIdsByElementIds.stream().map(NodeXelementXvarXverifyInfo::getNodeId).collect(Collectors.toList());
                    bpmnNodeButtonConfs = bpmnNodeButtonConfBizService.getService().queryByNodeIds(nodeIds, ButtonPageTypeEnum.TO_VIEW);
                }
                //只能显示在发起人页的按钮不应显示在其它页面
                if(Boolean.TRUE.equals(isInitiate)&&!CollectionUtils.isEmpty(bpmnNodeButtonConfs)){
                    bpmnNodeButtonConfs=bpmnNodeButtonConfs.stream().filter(a->!Objects.equals(a.getStartPageOnly(),1)).collect(Collectors.toList());
                }
            }

        }

        if(!CollectionUtils.isEmpty(bpmnNodeButtonConfs)){
            List<ProcessActionButtonVo> processActionButtonVos = bpmnNodeButtonConfs.stream().map(item -> ProcessActionButtonVo.builder().buttonType(item.getButtonType())
                            .name(item.getButtonName()).show(ProcessButtonEnum.VIEW_TYPE.getCode())
                            .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build())
                    .collect(Collectors.toList());
            return processActionButtonVos;
        }
       return new ArrayList<>();
    }
}
