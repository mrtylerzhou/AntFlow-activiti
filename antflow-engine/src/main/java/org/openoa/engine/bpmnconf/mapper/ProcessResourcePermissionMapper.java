package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.ProcessResourcePermission;

@Mapper
public interface ProcessResourcePermissionMapper extends BaseMapper<ProcessResourcePermission> {
}
