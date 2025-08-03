package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessNodeRecord;

public interface BpmProcessNodeRecordService extends IService<BpmProcessNodeRecord> {
    boolean addBpmProcessNodeRecord(BpmProcessNodeRecord processNodeRecord);

    BpmProcessNodeRecord getBpmProcessNodeRecord(BpmProcessNodeRecord processNodeRecord);
}
