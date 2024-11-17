package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;

@Mapper
public interface BpmnConfLfFormdataFieldMapper extends BaseMapper<BpmnConfLfFormdataField> {
    void updateByConfIdAndFieldName(@Param("confId")Long confId,@Param("fieldName")String fieldName);
}
