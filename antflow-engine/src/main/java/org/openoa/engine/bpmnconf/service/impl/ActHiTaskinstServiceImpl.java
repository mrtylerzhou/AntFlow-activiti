package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.engine.bpmnconf.mapper.ActHiTaskinstMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class ActHiTaskinstServiceImpl extends ServiceImpl<ActHiTaskinstMapper, ActHiTaskinst> {
    public ActHiTaskinst queryLastHisRecord(String procInstId){

        return queryRecordsByProcInstId(procInstId).get(0);
    }
    public List<ActHiTaskinst> queryRecordsByProcInstId(String procInstId){
        LambdaQueryWrapper<ActHiTaskinst> hisQryWrapper = Wrappers.<ActHiTaskinst>lambdaQuery()
                .eq(ActHiTaskinst::getProcInstId, procInstId)
                .orderByDesc(ActHiTaskinst::getEndTime);
        return this.list(hisQryWrapper);
    }
    public List<ActHiTaskinst> queryRecordsByProcInstIdOrderByCreateTimeDesc(String procInstId){
        LambdaQueryWrapper<ActHiTaskinst> hisQryWrapper = Wrappers.<ActHiTaskinst>lambdaQuery()
                .eq(ActHiTaskinst::getProcInstId, procInstId)
                .orderByDesc(ActHiTaskinst::getStartTime);
        return this.list(hisQryWrapper);
    }
    public List<ActHiTaskinst> queryRecordsByProcInstIds(Collection<String> procInstIds){
        LambdaQueryWrapper<ActHiTaskinst> hisQryWrapper = Wrappers.<ActHiTaskinst>lambdaQuery()
                .in(ActHiTaskinst::getProcInstId, procInstIds);
        return this.list(hisQryWrapper);
    }
    public ActHiTaskinst queryLastHisRecord(String procInstId,String assigneeId){
        LambdaQueryWrapper<ActHiTaskinst> hisQryWrapper = Wrappers.<ActHiTaskinst>lambdaQuery()
                .eq(ActHiTaskinst::getProcInstId, procInstId)
                .eq(ActHiTaskinst::getAssignee,assigneeId)
                .orderByDesc(ActHiTaskinst::getEndTime);
        return this.list(hisQryWrapper).get(0);
    }
    public ActHiTaskinst queryByTaskId(String taskId){
        return this.getById(taskId);
    }
    public void  deleteById(String taskId){
        this.getBaseMapper().deleteById(taskId);
    }
}
