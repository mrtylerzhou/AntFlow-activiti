package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class BpmProcessNoticeServiceImpl extends ServiceImpl<BpmProcessNoticeMapper, BpmProcessNotice> {


    @Autowired
    private BpmProcessNoticeMapper mapper;

    /**
     * save process notice
     *
     * @param processKey
     * @param notifyTypeIds
     */
    public void saveProcessNotice(String processKey, List<Integer> notifyTypeIds) {
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        mapper.delete(wrapper);
        if (!ObjectUtils.isEmpty(notifyTypeIds)) {
            notifyTypeIds.stream().forEach(o -> {
                mapper.insert(BpmProcessNotice.builder()
                        .processKey(processKey)
                        .type(o)
                        .build());
            });
        }
    }

    public List<BpmProcessNotice> processNoticeList(String processKey) {
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        return mapper.selectList(wrapper);
    }
}
