package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.BpmProcessEfficiency;

/**
 * 流程效能统计 Mapper
 *
 * @author AntFlow
 * @since 0.5
 */
@Mapper
public interface BpmProcessEfficiencyMapper extends BaseMapper<BpmProcessEfficiency> {

}
