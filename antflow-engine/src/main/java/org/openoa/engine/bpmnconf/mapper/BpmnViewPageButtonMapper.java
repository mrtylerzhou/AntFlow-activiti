package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmnViewPageButton;
import org.springframework.stereotype.Repository;

/**
 * @Classname BpmnViewPageButtonMapper
 * @Description view page button mapper
 * @since 0.5
 * @Created by AntOffice
 */
@Mapper
public interface BpmnViewPageButtonMapper extends BaseMapper<BpmnViewPageButton> {
}
