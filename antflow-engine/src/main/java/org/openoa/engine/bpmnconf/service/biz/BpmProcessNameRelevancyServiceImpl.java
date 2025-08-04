package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessNameRelevancy;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNameRelevancyMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNameRelevancyService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BpmProcessNameRelevancyServiceImpl extends ServiceImpl<BpmProcessNameRelevancyMapper, BpmProcessNameRelevancy> implements BpmProcessNameRelevancyService {


    @Override
    public boolean selectCout(String formCode) {
        QueryWrapper<BpmProcessNameRelevancy> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", formCode);
        wrapper.eq("is_del", 0);
        long count = getBaseMapper().selectCount(wrapper);
        return count > 0;
    }

    @Override
    public BpmProcessNameRelevancy findProcessNameRelevancy(String formCode) {
        QueryWrapper<BpmProcessNameRelevancy> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", formCode);
        wrapper.eq("is_del", 0);
        return this.getBaseMapper().selectOne(wrapper);
    }

    @Override
    public boolean add(BpmProcessNameRelevancy processNameRelevancy) {
        getBaseMapper().insert(processNameRelevancy);
        return true;
    }

    /**
     * process key list
     *
     * @param id
     * @return
     */
    @Override
    public List<String> processKeyList(Long id) {
        QueryWrapper<BpmProcessNameRelevancy> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        wrapper.eq("process_name_id", id);
        return Optional.ofNullable(getBaseMapper().selectList(wrapper).stream().map(BpmProcessNameRelevancy::getProcessKey).collect(Collectors.toList())).orElse(Arrays.asList());
    }

}
