package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNodeRecord;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeRecordMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class BpmProcessNodeRecordServiceImpl extends ServiceImpl<BpmProcessNodeRecordMapper, BpmProcessNodeRecord> implements BpmProcessNodeRecordService {

    @Autowired
    private BpmProcessNodeRecordMapper mapper;

    /***
     * insert node overtime record
     */
    public boolean addBpmProcessNodeRecord(BpmProcessNodeRecord processNodeRecord) {
        mapper.insert(processNodeRecord);
        return true;
    }

    /****
     * get current node overtime record
     */
    public BpmProcessNodeRecord getBpmProcessNodeRecord(BpmProcessNodeRecord processNodeRecord) {
        QueryWrapper<BpmProcessNodeRecord> wrapper = new QueryWrapper<BpmProcessNodeRecord>();
        wrapper.eq("processInstance_id", processNodeRecord.getProcessInstanceId());
        wrapper.eq("task_id", processNodeRecord.getTaskId());
        List<BpmProcessNodeRecord> list = mapper.selectList(wrapper);
        if (!ObjectUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
