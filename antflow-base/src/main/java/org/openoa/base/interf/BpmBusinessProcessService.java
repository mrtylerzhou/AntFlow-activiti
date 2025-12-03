package org.openoa.base.interf;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BpmBusinessProcessService extends IService<BpmBusinessProcess> {
    BpmBusinessProcess findBpmBusinessProcess(String businessId, String businessNumber);

    @Transactional(propagation = Propagation.REQUIRED)
    void addBusinessProcess(BpmBusinessProcess businessProcess);

    void addBusinessProcess(String businessId, String key, String entryId, String processNum, String bpmnCode, String description);

    BpmBusinessProcess updateBusinessProcess(BpmBusinessProcess bpmBusinessProcess);

    BpmBusinessProcess getBpmBusinessProcess(String processCode);

    BpmBusinessProcess getBpmBusinessProcess(BpmBusinessProcess bpmBusinessProcess);

    boolean updateBpmBusinessProcess(String procInstId);

    List<BpmBusinessProcess> listBpmBusinessProcess(List<String> processNumbers);

    boolean checkData(String processNumber);

    boolean checkProcessData(String entryId);

    void updateProcessIsDel(String processNumber);
    void updateProcessStatus(String processNumber, ProcessStateEnum processStateEnum);
}
