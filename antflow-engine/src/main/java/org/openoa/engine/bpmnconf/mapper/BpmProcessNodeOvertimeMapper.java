package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNodeOvertime;
import org.openoa.base.vo.BpmProcessNodeOvertimeVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmProcessNodeOvertimeMapper extends BaseMapper<BpmProcessNodeOvertime> {
    /**
     * query notice type
     *
     * @param processKey
     * @return
     */
    public List<Integer> selectNoticeType(@Param("processKey") String processKey);

    /**
     * query over time node
     *
     * @param processKey
     * @return
     */
    public List<BpmProcessNodeOvertimeVo> selectNoticeNodeName(@Param("processKey") String processKey);
}
