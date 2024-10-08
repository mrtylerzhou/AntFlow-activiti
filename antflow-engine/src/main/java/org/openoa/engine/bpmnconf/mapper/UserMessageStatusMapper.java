package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.UserMessageStatus;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMessageStatusMapper extends BaseMapper<UserMessageStatus> {

}
