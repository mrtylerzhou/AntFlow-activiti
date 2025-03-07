package org.openoa.base.service;



import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.MsgTopics;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.MqProcessEventVo;

import java.util.Date;
import java.util.Optional;

/**
 * 流程提交,同意,撤销,退回,完成等触发发送消息事件
 *
 * @Author tylerzhou
 * Date on 2021/8/19
 */
public class ProcessEventSendMessageAdaptor extends BaseSendMqMsgAdaptor<MqProcessEventVo, BusinessDataVo> {
    @Override
    public void doCallBack(BusinessDataVo param) {
        if (param==null) {
            return;
        }
        super.doCallBack(param);
    }

    @Override
    protected String getTopicName() {
        return MsgTopics.WORKFLOW_EVENT_PUSH;
    }

    @Override
    public MqProcessEventVo formattedValue(BusinessDataVo businessDataVo) {
        if (businessDataVo==null) {
            return null;
        }
        String processNumber = businessDataVo.getProcessNumber();
        if (StringUtils.isEmpty(processNumber)) {
            throw new JiMuBizException("未获取到流程编号!");
        }
        BpmBusinessProcessService bpmBusinessProcessService = SpringBeanUtils.getBean(BpmBusinessProcessService.class);
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (bpmBusinessProcess==null) {
            return null;
        }
        MqProcessEventVo mqProcessEventVo = new MqProcessEventVo();
        mqProcessEventVo.setProcessCode(processNumber);
        mqProcessEventVo.setBusinessId(bpmBusinessProcess.getBusinessId());
        mqProcessEventVo.setProcInstId(bpmBusinessProcess.getProcInstId());
        mqProcessEventVo.setButtonOperationType(businessDataVo.getMsgProcessEventEnum().getCode());
        mqProcessEventVo.setTaskId(businessDataVo.getTaskId());
        mqProcessEventVo.setOpTime(new Date());
        mqProcessEventVo.setOperationUserId(SecurityUtils.getLogInEmpIdSafe());
        return mqProcessEventVo;
    }
}
