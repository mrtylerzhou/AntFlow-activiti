package org.openoa.engine.bpmnconf.activitilistener;

import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.engine.bpmnconf.service.biz.BpmVariableMessageListenerServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.ThirdPartyCallBackServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableMessageBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openoa.base.constant.enums.CallbackTypeEnum.*;

/**
 * 此类是antflow动作事件监听器,如果想要统一处理流程流转正常流转事件,请使用{@link BpmnTaskListener}
 */
@Component
public class AntFlowOperationListener implements WorkflowButtonOperationHandler {
    @Autowired
    private ThirdPartyCallBackServiceImpl thirdPartyCallBackService;
    @Autowired
    private BpmVariableMessageBizService variableMessageBizService;
    @Autowired
    private BpmVariableMessageListenerServiceImpl bpmVariableMessageListenerService;

    /**
     * 流程提交
     */
    @Override
    public void onSubmit(BusinessDataVo businessData) {
        if(Boolean.TRUE.equals(businessData.getIsOutSideAccessProc())){
            thirdPartyCallBackService.doCallback( PROC_STARTED_CALL_BACK, businessData.getBpmnConfVo(),
                    businessData.getProcessNumber(), businessData.getBusinessId(), SecurityUtils.getLogInEmpNameSafe());
        }

    }

    /**
     * 重新提交
     */
    @Override
    public void onResubmit(BusinessDataVo businessData) {
    }

    /**
     * 同意
     */
    @Override
    public void onAgree(BusinessDataVo businessData) {
        if(Boolean.TRUE.equals(businessData.getIsOutSideAccessProc())){
            thirdPartyCallBackService.doCallback( PROC_COMMIT_CALL_BACK, businessData.getBpmnConfVo(),
                    businessData.getProcessNumber(), businessData.getBusinessId(),SecurityUtils.getLogInEmpNameSafe());
        }

    }

    /**
     * 不同意
     */
    @Override
    public void onDisAgree(BusinessDataVo businessData) {

    }

    /**
     * 查看流程详情
     */
    @Override
    public void onViewBusinessProcess(BusinessDataVo businessData) {
    }

    /**
     * 作废
     */
    @Override
    public void onAbandon(BusinessDataVo businessData) {
        if(Boolean.TRUE.equals(businessData.getIsOutSideAccessProc())){
            thirdPartyCallBackService.doCallback( PROC_END_CALL_BACK, businessData.getBpmnConfVo(),
                    businessData.getProcessNumber(), businessData.getBusinessId(),SecurityUtils.getLogInEmpNameSafe());
        }

    }

    /**
     * 承办
     */
    @Override
    public void onUndertake(BusinessDataVo businessData) {

    }

    /**
     * 变更处理人
     */
    @Override
    public void onChangeAssignee(BusinessDataVo businessData) {
    }

    /**
     * 终止
     */
    @Override
    public void onStop(BusinessDataVo businessData) {
        bpmVariableMessageListenerService.sendProcessMessages(EventTypeEnum.PROCESS_CANCELLATION,businessData);
    }

    /**
     * 转发
     */
    @Override
    public void onForward(BusinessDataVo businessData) {
        bpmVariableMessageListenerService.sendProcessMessages(EventTypeEnum.PROCESS_FORWARD,businessData);
    }

    /**
     * 退回修改
     */
    @Override
    public void onBackToModify(BusinessDataVo businessData) {
        variableMessageBizService.sendTemplateMessages(businessData);
    }

    /**
     * 加批
     */
    @Override
    public void onJp(BusinessDataVo businessData) {
        variableMessageBizService.sendTemplateMessages(businessData);
    }

    /**
     * 转办
     */
    @Override
    public void onZb(BusinessDataVo businessData) {
    }

    /**
     * 自选审批人
     */
    @Override
    public void onChooseAssignee(BusinessDataVo businessData) {
    }

    /**
     * 退回任意节点
     */
    @Override
    public void onBackToAnyNode(BusinessDataVo businessData) {
    }

    /**
     * 减签
     */
    @Override
    public void onRemoveAssignee(BusinessDataVo businessData) {
    }

    /**
     * 加签
     */
    @Override
    public void onAddAssignee(BusinessDataVo businessData) {
    }

    /**
     * 变更未来节点审批人
     */
    @Override
    public void onChangeFutureAssignee(BusinessDataVo businessData) {
    }

    /**
     * 未来节点减签
     */
    @Override
    public void onRemoveFutureAssignee(BusinessDataVo businessData) {
    }

    /**
     * 未来节点加签
     */
    @Override
    public void onAddFutureAssignee(BusinessDataVo businessData) {
    }


    /**
     * 需要注意的是外部SaaS工作流完成并不走这里
     * @param vo
     */
    @Override
    public void onFinishData(BusinessDataVo vo) {
        //注意流程完成通知消息并不在这里,实际上已经有了,请勿要在这里写
    }
}
