package org.openoa.engine.bpmnconf.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.FlowClairvoyanceResultVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程千里眼 Mapper
 *
 * @author AntFlow
 */
@Mapper
public interface FlowClairvoyanceMapper {

    /**
     * 分页查询审批中的流程(按创建时间倒序)
     */
    List<Map<String, Object>> selectProcessBatch(@Param("timeLowerBound") Date timeLowerBound,
                                                  @Param("offset") int offset,
                                                  @Param("batchSize") int batchSize);

    /**
     * 查询当前节点命中的审批人(仅CURRENT模式)
     */
    List<Map<String, Object>> selectCurrentNodeMatches(@Param("procInstIds") List<String> procInstIds,
                                                       @Param("userIds") List<String> userIds);

    /**
     * 查询multiplayer表中命中的审批人(CURRENT_FUTURE/FUTURE/ALL模式)
     */
    List<Map<String, Object>> selectMultiplayerMatches(@Param("processNumbers") List<String> processNumbers,
                                                       @Param("userIds") List<String> userIds,
                                                       @Param("nodeScope") String nodeScope);
}
