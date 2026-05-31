package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.entity.BpmnConf;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNameBizService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class BpmProcessNameBizServiceImpl implements BpmProcessNameBizService {

    @Override
    public void editProcessName(BpmnConf bpmnConfByCode) {
        BpmProcessName existing = this.getService().findByFormCode(bpmnConfByCode.getFormCode());
        if (!ObjectUtils.isEmpty(existing) && !ObjectUtils.isEmpty(existing.getId())) {
            // update name if changed
            if (!bpmnConfByCode.getBpmnName().equals(existing.getProcessName())) {
                existing.setProcessName(bpmnConfByCode.getBpmnName());
                this.getService().updateById(existing);
            }
        } else {
            // insert new
            BpmProcessName process = BpmProcessName.builder()
                    .processName(bpmnConfByCode.getBpmnName())
                    .processKey(bpmnConfByCode.getFormCode())
                    .build();
            this.getMapper().insert(process);
        }
    }

    /**
     * get process name by key
     *
     * @param processKey
     * @return
     */
    @Override
    public BpmProcessName getBpmProcessName(String processKey) {
        BpmProcessName processName = this.getService().findByFormCode(processKey);
        if (ObjectUtils.isEmpty(processName)) {
            return new BpmProcessName();
        }
        return processName;
    }
}
