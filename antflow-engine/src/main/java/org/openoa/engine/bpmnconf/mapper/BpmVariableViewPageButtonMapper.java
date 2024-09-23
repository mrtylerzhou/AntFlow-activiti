package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmVariableViewPageButton;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BpmVariableViewPageButtonMapper extends BaseMapper<BpmVariableViewPageButton> {

    /**
     * get view page buttons by processNumber
     *
     * @param processNum
     * @return
     */
    List<BpmVariableViewPageButton> getButtonsByProcessNumber(@Param("processNum") String processNum);

}