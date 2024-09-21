package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.BpmProcessNameRelevancy;
import org.springframework.stereotype.Repository;

@Mapper
public interface BpmProcessNameRelevancyMapper extends BaseMapper<BpmProcessNameRelevancy> {

}
