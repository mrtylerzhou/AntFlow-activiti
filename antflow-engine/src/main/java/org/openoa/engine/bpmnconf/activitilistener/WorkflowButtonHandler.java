package org.openoa.engine.bpmnconf.activitilistener;

import org.openoa.base.interf.ProcessFinishListener;
import org.openoa.base.vo.BusinessDataVo;

public interface WorkflowButtonHandler {

    /**
     * 流程提交
     */
    void onSubmit(BusinessDataVo businessData);

    /**
     * 重新提交
     */
    void onResubmit(BusinessDataVo businessData);

    /**
     * 同意
     */
    void onAgree(BusinessDataVo businessData);

    /**
     * 不同意
     */
    void onDisAgree(BusinessDataVo businessData);

    /**
     * 查看流程详情
     */
    void onViewBusinessProcess(BusinessDataVo businessData);

    /**
     * 作废
     */
    void onAbandon(BusinessDataVo businessData);

    /**
     * 承办
     */
    void onUndertake(BusinessDataVo businessData);

    /**
     * 变更处理人
     */
    void onChangeAssignee(BusinessDataVo businessData);

    /**
     * 终止
     */
    void onStop(BusinessDataVo businessData);

    /**
     * 转发
     */
    void onForward(BusinessDataVo businessData);

    /**
     * 退回修改
     */
    void onBackToModify(BusinessDataVo businessData);

    /**
     * 加批
     */
    void onJp(BusinessDataVo businessData);

    /**
     * 转办
     */
    void onZb(BusinessDataVo businessData);

    /**
     * 自选审批人
     */
    void onChooseAssignee(BusinessDataVo businessData);

    /**
     * 退回任意节点
     */
    void onBackToAnyNode(BusinessDataVo businessData);

    /**
     * 减签
     */
    void onRemoveAssignee(BusinessDataVo businessData);

    /**
     * 加签
     */
    void onAddAssignee(BusinessDataVo businessData);

    /**
     * 变更未来节点审批人
     */
    void onChangeFutureAssignee(BusinessDataVo businessData);

    /**
     * 未来节点减签
     */
    void onRemoveFutureAssignee(BusinessDataVo businessData);

    /**
     * 未来节点加签
     */
    void onAddFutureAssignee(BusinessDataVo businessData);
    void onFinishData(BusinessDataVo vo);
}
