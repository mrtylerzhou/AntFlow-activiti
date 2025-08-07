package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessNodeSubmit;

public interface BpmProcessNodeSubmitService extends IService<BpmProcessNodeSubmit> {
    BpmProcessNodeSubmit findBpmProcessNodeSubmit(String processInstanceId);

    boolean addProcessNode(BpmProcessNodeSubmit processNodeSubmit);

    boolean deleteProcessNode(String processInstanceId);
}
