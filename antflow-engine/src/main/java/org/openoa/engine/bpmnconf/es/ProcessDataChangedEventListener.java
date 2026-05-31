package org.openoa.engine.bpmnconf.es;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.MqProcessEventVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Spring event listener that triggers ES indexing when process data changes.
 * Replaces the MQ consumer (ProcessAndFinanceDataListener) from the original project.
 * Only active when antflow.es.enabled=true.
 *
 * @Author tylerzhou
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "antflow.es", name = "enabled", havingValue = "true")
public class ProcessDataChangedEventListener {

    @Autowired
    private ProcessDataIndexService processDataIndexService;

    @EventListener
    public void onProcessDataChanged(ProcessDataChangedEvent event) {
        log.debug("Received ProcessDataChangedEvent: processNumber={}, operationType={}",
                event.getProcessNumber(), event.getOperationType());
        MqProcessEventVo vo = new MqProcessEventVo();
        vo.setProcessCode(event.getProcessNumber());
        vo.setFormCode(event.getFormCode());
        vo.setBusinessId(event.getBusinessId());
        vo.setTaskId(event.getTaskId());
        vo.setButtonOperationType(event.getOperationType());
        vo.setOpTime(new Date());
        vo.setOperationUserId(SecurityUtils.getLogInEmpIdSafe());
        processDataIndexService.process(vo);
    }
}
