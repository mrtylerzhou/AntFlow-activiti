package org.openoa.engine.lowflow.service;

import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.service.AntFlowOrderPostProcessor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.activitilistener.WorkflowButtonHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AntFlowButtonsOperationPostProcessor implements AntFlowOrderPostProcessor<BusinessDataVo> {

    @Autowired
    private WorkflowButtonHandler workflowButtonHandler;

    @Override
    public void postProcess(BusinessDataVo vo) {
        ProcessOperationEnum poEnum = ProcessOperationEnum.getEnumByCode(vo.getOperationType());
        if (poEnum == null) {
            throw new IllegalArgumentException("无效的操作类型: " + vo.getOperationType());
        }

        switch (poEnum) {
            case BUTTON_TYPE_SUBMIT:
                workflowButtonHandler.onSubmit(vo);
                break;
            case BUTTON_TYPE_RESUBMIT:
                workflowButtonHandler.onResubmit(vo);
                break;
            case BUTTON_TYPE_AGREE:
                workflowButtonHandler.onAgree(vo);
                break;
            case BUTTON_TYPE_DIS_AGREE:
                workflowButtonHandler.onDisAgree(vo);
                break;
            case BUTTON_TYPE_VIEW_BUSINESS_PROCESS:
                workflowButtonHandler.onViewBusinessProcess(vo);
                break;
            case BUTTON_TYPE_ABANDON:
                workflowButtonHandler.onAbandon(vo);
                break;
            case BUTTON_TYPE_UNDERTAKE:
                workflowButtonHandler.onUndertake(vo);
                break;
            case BUTTON_TYPE_CHANGE_ASSIGNEE:
                workflowButtonHandler.onChangeAssignee(vo);
                break;
            case BUTTON_TYPE_STOP:
                workflowButtonHandler.onStop(vo);
                break;
            case BUTTON_TYPE_FORWARD:
                workflowButtonHandler.onForward(vo);
                break;
            case BUTTON_TYPE_BACK_TO_MODIFY:
                workflowButtonHandler.onBackToModify(vo);
                break;
            case BUTTON_TYPE_JP:
                workflowButtonHandler.onJp(vo);
                break;
            case BUTTON_TYPE_ZB:
                workflowButtonHandler.onZb(vo);
                break;
            case BUTTON_TYPE_CHOOSE_ASSIGNEE:
                workflowButtonHandler.onChooseAssignee(vo);
                break;
            case BUTTON_TYPE_BACK_TO_ANY_NODE:
                workflowButtonHandler.onBackToAnyNode(vo);
                break;
            case BUTTON_TYPE_REMOVE_ASSIGNEE:
                workflowButtonHandler.onRemoveAssignee(vo);
                break;
            case BUTTON_TYPE_ADD_ASSIGNEE:
                workflowButtonHandler.onAddAssignee(vo);
                break;
            case BUTTON_TYPE_CHANGE_FUTURE_ASSIGNEE:
                workflowButtonHandler.onChangeFutureAssignee(vo);
                break;
            case BUTTON_TYPE_REMOVE_FUTURE_ASSIGNEE:
                workflowButtonHandler.onRemoveFutureAssignee(vo);
                break;
            case BUTTON_TYPE_ADD_FUTURE_ASSIGNEE:
                workflowButtonHandler.onAddFutureAssignee(vo);
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作类型: " + poEnum);
        }
    }

    @Override
    public int order() {
        return 0;
    }
}
