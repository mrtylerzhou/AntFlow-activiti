package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BpmProcessNoticeServiceImpl extends ServiceImpl<BpmProcessNoticeMapper, BpmProcessNotice> {




    /**
     * save process notice
     *
     * @param processKey
     * @param notifyTypeIds
     */
    public void saveProcessNotice(String processKey, List<Integer> notifyTypeIds) {
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        this.getBaseMapper().delete(wrapper);
        if (!ObjectUtils.isEmpty(notifyTypeIds)) {
            notifyTypeIds.forEach(o -> {
                this.getBaseMapper().insert(BpmProcessNotice.builder()
                        .processKey(processKey)
                        .type(o)
                        .build());
            });
        }
    }

    public List<BpmProcessNotice> processNoticeList(String processKey) {
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        return this.getBaseMapper().selectList(wrapper);
    }

    public Map<String,List<BpmProcessNotice>> processNoticeMap(List<String> processKeys) {
        LambdaQueryWrapper<BpmProcessNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BpmProcessNotice::getProcessKey,processKeys);
        List<BpmProcessNotice> bpmProcessNotices = this.baseMapper.selectList(wrapper);
       return bpmProcessNotices.stream()
           .collect(Collectors.groupingBy(BpmProcessNotice::getProcessKey));
    }
}
