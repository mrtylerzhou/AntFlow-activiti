package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnNodeConditionsConf;
import org.openoa.base.vo.BusinessDataVo;

import java.util.List;

public interface BpmnNodeConditionsConfService extends IService<BpmnNodeConditionsConf> {
    List<String> queryConditionParamNameByProcessNumber(BusinessDataVo businessDataVo);
}
