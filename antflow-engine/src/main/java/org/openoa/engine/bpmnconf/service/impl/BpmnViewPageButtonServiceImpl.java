package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmnViewPageButton;
import org.openoa.engine.bpmnconf.mapper.BpmnViewPageButtonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname BpmnViewPageButtonServiceImpl
 * @Description view pagebutton
 * @Created by AntOffice
 */
@Service
public class BpmnViewPageButtonServiceImpl extends ServiceImpl<BpmnViewPageButtonMapper, BpmnViewPageButton> {

    @Autowired
    private BpmnViewPageButtonMapper mapper;

    public Integer deleteByConfId(Long confId) {
        int deleteSize = baseMapper.delete(new QueryWrapper<BpmnViewPageButton>().eq("conf_id", confId));
        return deleteSize;
    }

}
