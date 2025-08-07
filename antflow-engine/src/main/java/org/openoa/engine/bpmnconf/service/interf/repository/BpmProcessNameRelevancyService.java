package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessNameRelevancy;
import org.openoa.base.interf.IAFService;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNameRelevancyMapper;

import java.util.List;

public interface BpmProcessNameRelevancyService extends IAFService<BpmProcessNameRelevancyMapper,BpmProcessNameRelevancy> {
    boolean selectCout(String formCode);

    BpmProcessNameRelevancy findProcessNameRelevancy(String formCode);

    boolean add(BpmProcessNameRelevancy processNameRelevancy);

    List<String> processKeyList(Long id);
}
