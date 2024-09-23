package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmVariableButton;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname BpmVariableButtonMapper
 * @Description TODO
 * @Date 2021-11-27 15:27
 * @Created by AntOffice
 */
@Mapper
public interface BpmVariableButtonMapper extends BaseMapper<BpmVariableButton> {
    /**
     * query node button by process number and element id
     *
     * @param processNum
     * @param elementId
     * @return
     */
    List<BpmVariableButton> getButtonsByProcessNumber(@Param("processNum") String processNum,
                                                      @Param("elementId") String elementId);
}
