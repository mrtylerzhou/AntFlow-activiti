package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.BpmBusiness;

public interface BpmBusinessService extends IService<BpmBusiness> {
    boolean editProcessBusiness(BusinessDataVo vo);

    boolean updateBusinessState(BusinessDataVo vo);

    void deleteBusiness(BusinessDataVo vo);

    Integer getBpmBusinessCount(Integer userId);
}
