package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 协助节点按钮操作策略
 * 协助节点语义为"办理"而非"审批",不代表同意/不同意,但流程仍需向下流转
 * 因此委托到同意(AGREE)处理策略完成任务推进
 *
 * @since 0.5
 */
@Slf4j
@Component
public class AssistProcessImpl implements ProcessOperationAdaptor {

    @Autowired
    private ResubmitProcessImpl resubmitProcess;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        log.info("协助节点办理,委托同意逻辑推进流程. processNumber:{}", vo.getProcessNumber());
        //保持operationType=41(协助)不篡改,审批记录正确反映操作类型
        //委托到同意处理策略(ResubmitProcessImpl同时处理RESUBMIT/AGREE/JP)
        resubmitProcess.doProcessButton(vo);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_ASSIST);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_ASSIST);
    }
}
