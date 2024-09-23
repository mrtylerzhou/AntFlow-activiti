package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.vo.BpmProcessVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BpmProcessNameMapper extends BaseMapper<BpmProcessName> {

    public List<BpmProcessVo> allProcess();

    BpmProcessVo getBpmProcessVo(@Param("processKey") String processKey);
}
