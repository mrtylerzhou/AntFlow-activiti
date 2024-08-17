package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmBusinessProcess;
import org.springframework.stereotype.Repository;

@Repository
public interface BpmBusinessProcessMapper extends BaseMapper<BpmBusinessProcess> {

    public BpmBusinessProcess findBpmBusinessProcess(BpmBusinessProcess bpmBusinessProcess);

    /***
     * update bpm business process
     */
    public Integer updateBpmBusinessProcess(@Param("description") String description, @Param("entryId") String entryId, @Param("processState") Integer processState);

    /**
     * delete business process
     */
    public Integer delteBusinessProcess(@Param("businessKey") String businessKey);


}
