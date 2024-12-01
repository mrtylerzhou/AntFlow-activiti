package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmnApproveRemind;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdata;

@Mapper
public interface BpmnConfLfFormdataMapper  extends BaseMapper<BpmnConfLfFormdata> {
    BpmnConfLfFormdata getByFormCode(@Param("formCode") String formCode);
}
