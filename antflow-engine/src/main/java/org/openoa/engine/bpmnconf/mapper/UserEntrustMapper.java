package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.UserEntrust;
import org.openoa.base.vo.Entrust;

import java.util.List;

@Mapper
public interface UserEntrustMapper extends BaseMapper<UserEntrust> {
    List<Entrust> getEntrustList(@Param("userId") Integer userId);

    List<Entrust> getEntrustListNew(@Param("userId") String userId);

    List<Entrust> getEntrustPageList(Page page,@Param("userId") String userId);
}
