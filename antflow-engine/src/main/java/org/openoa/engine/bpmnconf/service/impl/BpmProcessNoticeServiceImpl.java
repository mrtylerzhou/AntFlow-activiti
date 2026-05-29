package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessNotice;
import org.openoa.base.entity.BpmnTemplate;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnTemplateVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNoticeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class BpmProcessNoticeServiceImpl extends ServiceImpl<BpmProcessNoticeMapper, BpmProcessNotice> implements BpmProcessNoticeService {



    @Override
    public List<BpmProcessNotice> processNoticeList(String processKey) {
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        return this.getBaseMapper().selectList(wrapper);
    }

    @Override
    public Map<String,List<BpmProcessNotice>> processNoticeMap(List<String> processKeys) {
        LambdaQueryWrapper<BpmProcessNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BpmProcessNotice::getProcessKey,processKeys);
        List<BpmProcessNotice> bpmProcessNotices = this.baseMapper.selectList(wrapper);
       return bpmProcessNotices.stream()
           .collect(Collectors.groupingBy(BpmProcessNotice::getProcessKey));
    }
}
