package org.openoa.base.service;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.BusinessCallBackAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.MqProcessEventVo;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Process event callback adaptor that indexes process data to Elasticsearch.
 * Replaces the MQ stub (ProcessEventSendMessageAdaptor) in the callback chain.
 * Delegates to ProcessDataIndexService in antflow-engine via Spring bean lookup.
 *
 * @Author tylerzhou
 */
public class ProcessEventIndexAdaptor implements BusinessCallBackAdaptor<Void, BusinessDataVo> {

    @Override
    public Void formattedValue(BusinessDataVo businessDataVo) {
        return null;
    }

    @Override
    public void doCallBack(BusinessDataVo param) {
        if (param == null) {
            return;
        }
        MqProcessEventVo vo = buildProcessEventVo(param);
        if (vo == null) {
            return;
        }
        try {
            // ProcessDataIndexService is in antflow-engine, resolve via Spring context
            Class<?> serviceClass = Class.forName("org.openoa.engine.bpmnconf.es.ProcessDataIndexService");
            Object service = SpringBeanUtils.getBean(serviceClass);
            Method processMethod = serviceClass.getMethod("process", MqProcessEventVo.class);
            processMethod.invoke(service, vo);
        } catch (ClassNotFoundException e) {
            // ProcessDataIndexService not on classpath (ES not enabled), silently skip
        } catch (Exception e) {
            // Log but don't fail the operation
            org.slf4j.LoggerFactory.getLogger(getClass())
                    .error("Failed to index process data to ES: {}", e.getMessage(), e);
        }
    }

    private MqProcessEventVo buildProcessEventVo(BusinessDataVo businessDataVo) {
        String processNumber = businessDataVo.getProcessNumber();
        if (StringUtils.isEmpty(processNumber)) {
            return null;
        }
        BpmBusinessProcessService bpmBusinessProcessService = SpringBeanUtils.getBean(BpmBusinessProcessService.class);
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (bpmBusinessProcess == null) {
            return null;
        }
        MqProcessEventVo vo = new MqProcessEventVo();
        vo.setProcessCode(processNumber);
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        vo.setProcInstId(bpmBusinessProcess.getProcInstId());
        if (businessDataVo.getMsgProcessEventEnum() != null) {
            vo.setButtonOperationType(businessDataVo.getMsgProcessEventEnum().getCode());
        }
        vo.setTaskId(businessDataVo.getTaskId());
        vo.setOpTime(new Date());
        vo.setFormCode(businessDataVo.getFormCode());
        String loginEmpId = SecurityUtils.getLogInEmpIdSafe();
        vo.setOperationUserId(loginEmpId);
        return vo;
    }
}
