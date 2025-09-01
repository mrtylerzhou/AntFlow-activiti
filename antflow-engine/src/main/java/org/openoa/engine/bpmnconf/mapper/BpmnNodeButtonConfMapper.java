package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmnNodeButtonConf;

import java.util.List;

@Mapper
public interface BpmnNodeButtonConfMapper extends BaseMapper<BpmnNodeButtonConf> {

    List<BpmnNodeButtonConf> queryConfByBpmnConde(@Param("bpmnCode") String bpmnCode);
}
