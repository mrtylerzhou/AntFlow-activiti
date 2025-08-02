package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.entity.BpmProcessNameRelevancy;
import org.openoa.base.vo.BpmProcessVo;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BpmProcessNameServiceImpl extends ServiceImpl<BpmProcessNameMapper, BpmProcessName> {

    private static Map<String, BpmProcessVo> processVoMap = new ConcurrentHashMap<>();


    @Autowired
    private BpmProcessNameRelevancyServiceImpl bpmProcessNameRelevancyService;

    public void editProcessName(BpmnConf bpmnConfByCode) {
        BpmProcessName processName = this.findProcessName(bpmnConfByCode.getBpmnName());
        boolean falg = bpmProcessNameRelevancyService.selectCout(bpmnConfByCode.getFormCode());
        if (!ObjectUtils.isEmpty(processName.getId())) {
            if (!falg) {
                bpmProcessNameRelevancyService.add(BpmProcessNameRelevancy.builder()
                        .processKey(bpmnConfByCode.getFormCode())
                        .processNameId(processName.getId())
                        .build());
            }
        } else {
            if (falg) {
                BpmProcessNameRelevancy processNameRelevancy = bpmProcessNameRelevancyService.findProcessNameRelevancy(bpmnConfByCode.getFormCode());
                BpmProcessName bpmProcessName = this.getBaseMapper().selectById(processNameRelevancy.getProcessNameId());
                bpmProcessName.setProcessName(bpmnConfByCode.getBpmnName());
                this.updateById(bpmProcessName);
            } else {
                BpmProcessName process = BpmProcessName.builder()
                        .processName(bpmnConfByCode.getBpmnName())
                        .build();
                this.getBaseMapper().insert(process);

                //get id,if it is null then reutrn null
                long id = Optional.ofNullable(process.getId()).orElse(0L);
                bpmProcessNameRelevancyService.add(BpmProcessNameRelevancy.builder()
                        .processKey(bpmnConfByCode.getFormCode())
                        .processNameId(id)
                        .build());

            }

        }
    }

    /**
     * advance search config
     */
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

    public BpmProcessVo get(String processKey) {
        BpmProcessVo bpmProcessVo = processVoMap.get(processKey);
        if (!ObjectUtils.isEmpty(bpmProcessVo)) {
            return bpmProcessVo;
        }
        //将流程和名称查询缓存
        loadProcessName();
        return Optional.ofNullable(processVoMap.get(processKey)).orElse(new BpmProcessVo());
    }

    /**
     * get process name by key
     *
     * @param processKey
     * @return
     */
    public BpmProcessName getBpmProcessName(String processKey) {
        BpmProcessNameRelevancy processNameRelevancy = bpmProcessNameRelevancyService.findProcessNameRelevancy(processKey);
        if (ObjectUtils.isEmpty(processNameRelevancy)) {
            return new BpmProcessName();
        }
        return this.getBaseMapper().selectById(processNameRelevancy.getProcessNameId());
    }
}