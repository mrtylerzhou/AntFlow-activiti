package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessForward;

import java.util.List;


public interface BpmProcessForwardService extends IService<BpmProcessForward> {
    void addProcessForward(BpmProcessForward forward);

    List<BpmProcessForward> allBpmProcessForward(String userId);
}
