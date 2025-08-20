package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmnNodeFormRelatedUserConf;

import java.util.List;

@Mapper
public interface BpmnNodeFormRelatedUserConfMapper extends BaseMapper<BpmnNodeFormRelatedUserConf> {
    List<BpmnNodeFormRelatedUserConf> queryByConfId(@Param("confId")Long confId);
}
