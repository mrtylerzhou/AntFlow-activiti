package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessForward;

import org.openoa.engine.bpmnconf.mapper.BpmProcessForwardMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessForwardService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.*;

@Repository
public class BpmProcessForwardServiceImpl extends ServiceImpl<BpmProcessForwardMapper, BpmProcessForward> implements BpmProcessForwardService {




    /**
     * add process forward data
     *
     * @param forward
     */
    @Override
    public void addProcessForward(BpmProcessForward forward) {
        this.getBaseMapper().insert(forward);
    }

    /**
     * update forward info
     *
     * @param forward
     */
    public void updateProcessForward(BpmProcessForward forward) {
        QueryWrapper<BpmProcessForward> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", forward.getProcessInstanceId());
        wrapper.eq("forward_user_id", forward.getForwardUserId());
        this.getBaseMapper().selectList(wrapper).forEach(o -> {
            o.setIsRead(1);
            this.getBaseMapper().updateById(o);
        });
    }


    /**
     * query process forward by user id
     */
    @Override
    public List<BpmProcessForward> allBpmProcessForward(String userId) {
        return Optional.ofNullable(this.getBaseMapper().selectList(new QueryWrapper<BpmProcessForward>()
                .eq("is_del", 0)
                .eq("forward_user_id", userId)
        ))
                .orElse(Collections.emptyList());
    }

}
