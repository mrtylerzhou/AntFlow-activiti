package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUp;

import java.util.List;

@Mapper
public interface BpmVariableSignUpMapper extends BaseMapper<BpmVariableSignUp> {
    List<String> getSignUpPrevNodeIdsByeElementId(@Param("processNum") String processNum,
                                                             @Param("elementId") String elementId);
}
