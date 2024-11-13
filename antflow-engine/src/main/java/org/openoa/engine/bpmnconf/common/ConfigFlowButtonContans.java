package org.openoa.engine.bpmnconf.common;

import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.constant.enums.ConfigFlowButtonSortEnum;
import org.openoa.base.constant.enums.ProcessButtonEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.util.FilterUtil;
import org.openoa.base.vo.ProcessActionButtonVo;
import org.openoa.engine.bpmnconf.confentity.BpmVariableButton;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.engine.bpmnconf.confentity.BpmVariableViewPageButton;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableButtonServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableViewPageButtonServiceImpl;
import org.openoa.base.constant.enums.ProcessStateEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    /**get pc buttons
     * @param elementId  elementId
     * @param processNum process number
     * @param isJurisdiction  whether has monitoring permission false for no and true for yes
     * @param isInitiate whether is initiate
     * @return initiate=start page，audit=approvement page，toView=view page
     * @date 20190620
     */
    public Map<String, List<ProcessActionButtonVo>> getButtons(String processNum, String elementId,
                                                               Boolean isJurisdiction, Boolean isInitiate) {
        Map<String, List<ProcessActionButtonVo>> buttonMap = new HashMap<String, List<ProcessActionButtonVo>>();


        List<ProcessActionButtonVo> initiateButtons = new ArrayList<>();

        List<ProcessActionButtonVo> auditButtons = new ArrayList<>();

        List<ProcessActionButtonVo> toViewButtons = new ArrayList<>();


        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNum);
        if (bpmBusinessProcess == null || bpmBusinessProcess.getProcessState() == null
                || bpmBusinessProcess.getProcessState() == ProcessStateEnum.COMLETE_STATE.getCode()) {//审批中

            if (processNum != null && elementId != null) {
                List<BpmVariableButton> bpmVariableButtons = bpmVariableButtonService
                        .getButtonsByProcessNumber(processNum, elementId);
                initiateButtons = getButtons(bpmVariableButtons,ButtonPageTypeEnum.INITIATE);
                auditButtons = getButtons(bpmVariableButtons,ButtonPageTypeEnum.AUDIT);
            }

            if (processNum != null) {
                List<BpmVariableViewPageButton> bpmVariableViewPageButtons = bpmVariableViewPageButtonService
                        .getButtonsByProcessNumber(processNum);

                toViewButtons = toViewButtons(bpmVariableViewPageButtons, isInitiate);
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


            //when is more node,if yes then add undertake button
            if (isMoreNode(processNum, elementId)) {
                // add undertake button
                ProcessActionButtonVo undertake = ProcessActionButtonVo.builder()
                        .buttonType(ButtonTypeEnum.BUTTON_TYPE_UNDERTAKE.getCode())
                        .name(ButtonTypeEnum.BUTTON_TYPE_UNDERTAKE.getDesc()).show(ProcessButtonEnum.DEAL_WITH_TYPE.getCode())
                        .type(ProcessButtonEnum.DEFAULT_COLOR.getDesc()).build();
                auditButtons.clear();
                auditButtons.add(undertake);
            }
        } else if (bpmBusinessProcess.getProcessState() == ProcessStateEnum.HANDLE_STATE.getCode()
                //|| bpmBusinessProcess.getProcessState() == ProcessStateEnum.DISAGREE_STATE.getCode()
                || bpmBusinessProcess.getProcessState() == ProcessStateEnum.CRMCEL_STATE.getCode()
                || bpmBusinessProcess.getProcessState() == ProcessStateEnum.END_STATE.getCode()) {

            // process complete
            List<ProcessActionButtonVo> toViewButtonsComplete = new ArrayList<>();

            //query view page button
            if (processNum != null) {
                List<BpmVariableViewPageButton> bpmVariableViewPageButtons = bpmVariableViewPageButtonService
                        .getButtonsByProcessNumber(processNum);

                toViewButtons = toViewButtons(bpmVariableViewPageButtons, isInitiate);


                //process complete, filter invalid button
                for (ProcessActionButtonVo processActionButtonVo : toViewButtons) {
                    if (!processActionButtonVo.getButtonType().equals(ButtonTypeEnum.BUTTON_TYPE_ABANDONED.getCode())) {
                        toViewButtonsComplete.add(processActionButtonVo);
                    }

                }


                //stat page and aprovement page also set the view page button data
                initiateButtons.addAll(toViewButtonsComplete);
                auditButtons.addAll(toViewButtonsComplete);
                toViewButtons = toViewButtonsComplete;
            }

        }


        buttonMap.put(ButtonPageTypeEnum.INITIATE.getName(), buttonsSort(repeatFilter(initiateButtons)));

        buttonMap.put(ButtonPageTypeEnum.AUDIT.getName(), buttonsSort(repeatFilter(auditButtons)));

        buttonMap.put(ButtonPageTypeEnum.TO_VIEW.getName(), buttonsSort(repeatFilter(toViewButtons)));
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
    public boolean isMoreNode(String processNum, String elementId) {
        List<BpmVariableMultiplayer> list = bpmVariableMultiplayerService.isMoreNode(processNum, elementId);

        // if it is more node and is or sign,and does not  undertaked,and has more than one,return undertake button
        return list != null && list.size() > 1 && list.get(0).getSignType() == 2;
    }
    // ordering the buttons
    private List<ProcessActionButtonVo> buttonsSort(List<ProcessActionButtonVo> buttons) {
        buttons.sort((o1, o2) -> {
            ConfigFlowButtonSortEnum sort1 = ConfigFlowButtonSortEnum.getEnumByCode(o1.getButtonType());
            ConfigFlowButtonSortEnum sort2 = ConfigFlowButtonSortEnum.getEnumByCode(o2.getButtonType());
            assert sort1 != null;
            assert sort2 != null;
            return sort1.getSort() - sort2.getSort();
        });
        return buttons;
    }
    /** deduplicate buttons by type
     * @param initiateButtons
     * @return
     */
    public List<ProcessActionButtonVo> repeatFilter(List<ProcessActionButtonVo> initiateButtons) {
        if(ObjectUtils.isEmpty(initiateButtons)){
            return Lists.newArrayList();
        }
        List<ProcessActionButtonVo> lists = initiateButtons
                .stream()
                .filter(FilterUtil.distinctByKeys(ProcessActionButtonVo::getButtonType))
                .collect(Collectors.toList());
        return lists;
    }

}
