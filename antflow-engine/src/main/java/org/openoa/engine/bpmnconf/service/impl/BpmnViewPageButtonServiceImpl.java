package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnViewPageButton;
import org.openoa.engine.bpmnconf.mapper.BpmnViewPageButtonMapper;

import org.openoa.engine.bpmnconf.service.interf.repository.BpmnViewPageButtonService;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @Classname BpmnViewPageButtonServiceImpl
 * @Description view pagebutton
 * @Created by AntOffice
 */
@Repository
public class BpmnViewPageButtonServiceImpl extends ServiceImpl<BpmnViewPageButtonMapper, BpmnViewPageButton> implements BpmnViewPageButtonService {

    @Override
    public Integer deleteByConfId(Long confId) {
        return baseMapper.delete( AFWrappers. <BpmnViewPageButton>lambdaTenantQuery().eq(BpmnViewPageButton::getConfId, confId));
    }

}
