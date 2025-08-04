package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmProcessVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNameMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNameService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BpmProcessNameServiceImpl extends ServiceImpl<BpmProcessNameMapper, BpmProcessName> implements BpmProcessNameService {

    private static Map<String, BpmProcessVo> processVoMap = new ConcurrentHashMap<>();




    /**
     * advance search config
     */
    @Override
    public List<BaseIdTranStruVo> listResult() {
        QueryWrapper<BpmProcessName> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        List<BpmProcessName> bpmProcessNames = Optional.ofNullable(this.getBaseMapper().selectList(wrapper)).orElse(Collections.emptyList());
        List<BaseIdTranStruVo> list = bpmProcessNames.stream().map(o -> BaseIdTranStruVo.builder()
                .id(o.getId().toString())
                .name(o.getProcessName())
                .build()
        ).collect(Collectors.toList());
        return list;
    }


    @Override
    public BpmProcessName findProcessName(String processName) {
        QueryWrapper<BpmProcessName> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        wrapper.eq("process_name", processName);
        List<BpmProcessName> bpmProcessNames = Optional.ofNullable(this.getBaseMapper().selectList(wrapper)).orElse(Collections.emptyList());
        if (!ObjectUtils.isEmpty(bpmProcessNames)) {
            return bpmProcessNames.get(0);
        } else {
            return new BpmProcessName();
        }
    }

    /**
     * cached
     */
    @Override
    public Map<String, BpmProcessVo> loadProcessName() {
        Map<String, BpmProcessVo> map = new HashMap<>();
        List<BpmProcessVo> list = this.getBaseMapper().allProcess();
        if (list == null) {
            return Collections.emptyMap();
        }
        for (BpmProcessVo next : list) {
            map.put(next.getProcessKey(), next);
        }
        processVoMap.putAll(map);
        return map;
    }

    @Override
    public BpmProcessVo get(String processKey) {
        BpmProcessVo bpmProcessVo = processVoMap.get(processKey);
        if (!ObjectUtils.isEmpty(bpmProcessVo)) {
            return bpmProcessVo;
        }
        //将流程和名称查询缓存
        loadProcessName();
        return Optional.ofNullable(processVoMap.get(processKey)).orElse(new BpmProcessVo());
    }


}