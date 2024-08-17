package org.openoa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.openoa.entity.BizDemo;
import org.openoa.mapper.BizDemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class BizDemoServiceImpl extends ServiceImpl<BizDemoMapper, BizDemo> {
    @Autowired
    private BizDemoMapper bizdemoMapper;

    public BizDemo getById(Integer id) {
        return bizdemoMapper.selectById(id);
    }
    public BizDemo getBizDemoByFlowKey(String flowkey) {

        BizDemo _bizdemo=new BizDemo();
        _bizdemo.setFlowKey(flowkey);
        LambdaQueryWrapper<BizDemo> lqw=new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(_bizdemo.getFlowKey()), BizDemo::getFlowKey,_bizdemo.getFlowKey());
        List<BizDemo> list = bizdemoMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(list))
            return  null;
        return list.get(0);
    }

    public Boolean addBizDemo(BizDemo dto) {
        int result =  bizdemoMapper.insert(dto);
        return result > 0;
    }
}
