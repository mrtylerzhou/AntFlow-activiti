package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.UserMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageMapper extends BaseMapper<UserMessage> {
    List<UserMessage> pageList(@Param("page") PageDto page, @Param("userId") Long userId);

    /**
     * delete by ids
     *
     * @param ids
     * @param userId
     * @return
     */
    Boolean deleteByIds(@Param("ids") String[] ids, @Param("userId") Long userId);

    /**
     * clear all
     */
    Boolean clean(@Param("userId") Integer userId);

    /**
     * clear latest 3 month's message
     *
     * @param beforeDate
     */
    void cleanUserMessage(@Param("beforeDate") String beforeDate);

}
