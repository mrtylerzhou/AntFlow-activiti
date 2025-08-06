package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmProcessNodeSubmit;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeSubmitMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeSubmitService;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Repository
@Slf4j
public class BpmProcessNodeSubmitServiceImpl extends ServiceImpl<BpmProcessNodeSubmitMapper, BpmProcessNodeSubmit> implements BpmProcessNodeSubmitService {



    /**
     * query to check whether the previous operation is submit or disagree
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public BpmProcessNodeSubmit findBpmProcessNodeSubmit(String processInstanceId) {
        QueryWrapper<BpmProcessNodeSubmit> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", processInstanceId);
        wrapper.orderByDesc("create_time");
        List<BpmProcessNodeSubmit> list = getBaseMapper().selectList(wrapper);
        if (!ObjectUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * add process node submit info
     *
     * @return
     */
    @Override
    public boolean addProcessNode(BpmProcessNodeSubmit processNodeSubmit) {
        QueryWrapper<BpmProcessNodeSubmit> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", processNodeSubmit.getProcessInstanceId());
        getBaseMapper().delete(wrapper);
        getBaseMapper().insert(processNodeSubmit);
        return true;
    }

    /**
     * delete node submit info
     */
    @Override
    public boolean deleteProcessNode(String processInstanceId) {
        QueryWrapper<BpmProcessNodeSubmit> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", processInstanceId);
        getBaseMapper().delete(wrapper);
        return true;
    }

}


