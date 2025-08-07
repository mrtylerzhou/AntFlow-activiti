package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.entity.BpmProcessNameRelevancy;
import org.openoa.base.entity.BpmnConf;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNameBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNameRelevancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
public class BpmProcessNameBizServiceImpl implements BpmProcessNameBizService {
    @Autowired
    private BpmProcessNameRelevancyService bpmProcessNameRelevancyService;

    @Override
    public void editProcessName(BpmnConf bpmnConfByCode) {
        BpmProcessName processName = this.getService().findProcessName(bpmnConfByCode.getBpmnName());
        boolean falg = bpmProcessNameRelevancyService.selectCout(bpmnConfByCode.getFormCode());
        if (!ObjectUtils.isEmpty(processName.getId())) {
            if (!falg) {
                bpmProcessNameRelevancyService.add(BpmProcessNameRelevancy.builder()
                        .processKey(bpmnConfByCode.getFormCode())
                        .processNameId(processName.getId())
                        .build());
            }
        } else {
            if (falg) {
                BpmProcessNameRelevancy processNameRelevancy = bpmProcessNameRelevancyService.findProcessNameRelevancy(bpmnConfByCode.getFormCode());
                BpmProcessName bpmProcessName = this.getMapper().selectById(processNameRelevancy.getProcessNameId());
                bpmProcessName.setProcessName(bpmnConfByCode.getBpmnName());
                this.getService().updateById(bpmProcessName);
            } else {
                BpmProcessName process = BpmProcessName.builder()
                        .processName(bpmnConfByCode.getBpmnName())
                        .build();
                this.getMapper().insert(process);

                //get id,if it is null then reutrn null
                long id = Optional.ofNullable(process.getId()).orElse(0L);
                bpmProcessNameRelevancyService.add(BpmProcessNameRelevancy.builder()
                        .processKey(bpmnConfByCode.getFormCode())
                        .processNameId(id)
                        .build());

            }

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
        BpmProcessNameRelevancy processNameRelevancy = bpmProcessNameRelevancyService.findProcessNameRelevancy(processKey);
        if (ObjectUtils.isEmpty(processNameRelevancy)) {
            return new BpmProcessName();
        }
        return this.getMapper().selectById(processNameRelevancy.getProcessNameId());
    }
}
