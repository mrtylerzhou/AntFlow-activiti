package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessNodeRecord;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeRecordMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeRecordService;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Repository
public class BpmProcessNodeRecordServiceImpl extends ServiceImpl<BpmProcessNodeRecordMapper, BpmProcessNodeRecord> implements BpmProcessNodeRecordService {

    /***
     * insert node overtime record
     */
    @Override
    public boolean addBpmProcessNodeRecord(BpmProcessNodeRecord processNodeRecord) {
        getBaseMapper().insert(processNodeRecord);
        return true;
    }

    /****
     * get current node overtime record
     */
    @Override
    public BpmProcessNodeRecord getBpmProcessNodeRecord(BpmProcessNodeRecord processNodeRecord) {
        QueryWrapper<BpmProcessNodeRecord> wrapper = new QueryWrapper<BpmProcessNodeRecord>();
        wrapper.eq("processInstance_id", processNodeRecord.getProcessInstanceId());
        wrapper.eq("task_id", processNodeRecord.getTaskId());
        List<BpmProcessNodeRecord> list = getBaseMapper().selectList(wrapper);
        if (!ObjectUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
