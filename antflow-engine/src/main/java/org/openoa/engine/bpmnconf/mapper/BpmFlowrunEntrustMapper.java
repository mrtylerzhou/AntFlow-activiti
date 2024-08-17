package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmFlowrunEntrust;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmFlowrunEntrustMapper extends BaseMapper<BpmFlowrunEntrust> {
    /**

     * check whether there is an entrust data by specified user id
     *
     * @param userId    当前处理人id
     * @param procDefId process def key
     * @return
     */
    public UserEntrust getBpmEntrust(@Param("actual") Integer userId, @Param("procDefId") String procDefId);

    /**
     * delete run info
     */
    public Integer deleteBpmFlowruninfo(@Param("businessKey") String businessKey);

    /**
     * get forward records
     *
     * @param procInstId
     * @return
     */
    public List<Integer> getCirculated(@Param("runinfoid") String procInstId);

    /**
     * update  entrust
     *
     * @param processInstanceId
     * @param loginUserId
     * @return
     */
    public Boolean updateBpmFlowrunEntrust(@Param("processInstanceId") String processInstanceId, @Param("userId") Integer loginUserId);
}